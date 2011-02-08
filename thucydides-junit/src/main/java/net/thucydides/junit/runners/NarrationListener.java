package net.thucydides.junit.runners;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.screenshots.Photographer;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an AcceptanceTestRun object.
 * This includes recording the names and results of each test, 
 * and taking and storing screenshots at strategic points during the tests.
 * @author johnsmart
 *
 */
class NarrationListener extends StickyFailureListener {

    private final Photographer photographer;
    private final AcceptanceTestRun acceptanceTestRun;
    
    private TestStep currentTestStep;
    
    
    public NarrationListener(Photographer photographer) {
        this.photographer = photographer;
        acceptanceTestRun = new AcceptanceTestRun();
    }
    
    private void getCurrentTestStepFrom(Description description) {
        if (currentTestStep == null) {
            currentTestStep = new TestStep(fromTestName(description.getMethodName()));
        }
    }
    
    @Override
    public void testRunStarted(Description description) throws Exception {        
        acceptanceTestRun.setTitle(fromClassname(description.getClassName()));
        super.testRunStarted(description);
    }

    /**
     * Create a new test step for use with this test.
     */
    @Override
    public void testStarted(Description description) throws Exception {
        getCurrentTestStepFrom(description);
        super.testStarted(description);
    }
    /**
     * Ignored tests (e.g. @Ignored) should be marked as skipped.
     */
    @Override
    public void testIgnored(Description description) throws Exception {
        getCurrentTestStepFrom(description);
        markCurrentTestAs(IGNORED);
        super.testIgnored(description);
    }    

    @Override
    public void testFailure(Failure failure) throws Exception {
        markCurrentTestAs(FAILURE);
        super.testFailure(failure);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);

        updatePreviousTestFailures();
        
        if (noPreviousTestHasFailed()) {
            File screenshot = takeScreenshotAtEndOfTestFor(aTestCalled(description));
            currentTestStep.setScreenshot(screenshot);
        }
        updateTestResultIfRequired();
        acceptanceTestRun.recordStep(currentTestStep);
        currentTestStep = null;
    }
    
    private boolean noPreviousTestHasFailed() {
        return !aPreviousTestHasFailed();
    }

    private void updateTestResultIfRequired() {
        if (currentTestResultIsKnown()) {
            return;
        }
        
        if (aPreviousTestHasFailed()) {
            markCurrentTestAs(SKIPPED);
        } else {
            markCurrentTestAs(SUCCESS);
        }
    }

    private boolean currentTestResultIsKnown() {
        return currentTestStep.getResult() != null;
    }

    private void markCurrentTestAs(TestResult result) {      
        currentTestStep.setResult(result);
    }

    protected String aTestCalled(Description description) {
        return description.getMethodName();
    }
    
    private File takeScreenshotAtEndOfTestFor(String testName) throws IOException {
       return photographer.takeScreenshot(testName);
    }  
    
    /**
     * Turns a classname into a human-readable title.
     */
    private String fromClassname(String className) {
        return className;
    }

    /**
     * Turns a classname into a human-readable title.
     */
    private String fromTestName(String testName) {
        return testName;
    }
    
}