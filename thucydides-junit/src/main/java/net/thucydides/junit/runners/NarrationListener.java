package net.thucydides.junit.runners;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.annotations.ForUserStory;
import net.thucydides.junit.annotations.StepDescription;
import net.thucydides.junit.internals.TestStatus;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.modeshape.common.text.Inflector;
import org.openqa.selenium.TakesScreenshot;

import com.google.common.base.Preconditions;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an AcceptanceTestRun object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 * 
 * @author johnsmart
 * 
 */
public class NarrationListener extends StickyFailureListener {

    private final Photographer photographer;
    private final AcceptanceTestRun acceptanceTestRun;
    private final Inflector inflector = Inflector.getInstance();
    
    private TestStep currentTestStep;

    public NarrationListener(final TakesScreenshot driver, final Configuration configuration) {
        acceptanceTestRun = new AcceptanceTestRun();
        TakesScreenshot screenshotCapableDriver = (TakesScreenshot) driver;
        photographer = new Photographer(screenshotCapableDriver, configuration.getOutputDirectory());
    }

    private void getCurrentTestStepFrom(final Description description) {
        if (currentTestStep == null) {
            String testName = testNameFrom(description);
            currentTestStep = new TestStep(testName);
        }
    }
    
    public AcceptanceTestRun getAcceptanceTestRun() {
        return acceptanceTestRun;
    }

    /**
     * Set the title of the test run.
     * The title of the test run should only be set once. It is either set explicitly,
     * via the Title annotation, or derived from the class name.
     */
    public void setTestRunTitle(final String title) {
        acceptanceTestRun.setTitle(title);
    }
    
    /**
     * Create a new test step for use with this test.
     */
    @Override
    public void testStarted(final Description description) throws Exception {
        updateTestRunTitleBasedOn(description);
        updateUserStoryIfDefinedIn(description);
        getCurrentTestStepFrom(description);
        super.testStarted(description);
    }

    private void updateUserStoryIfDefinedIn(final Description description) {
        Class<?> userStory = userStoryFor(description);
        if ((userStory != null) && (userStoryHasNotBeenDefinedFor(acceptanceTestRun))) {
            acceptanceTestRun.setUserStory(userStory);
        }
        
    }

    private boolean userStoryHasNotBeenDefinedFor(final AcceptanceTestRun acceptanceTestRun2) {
        return (acceptanceTestRun.getUserStory() == null);
    }

    private void updateTestRunTitleBasedOn(final Description description) {
        if (acceptanceTestRun.getTitle() == null) {
            acceptanceTestRun.setTitle(fromTitleIn(description));
        }
    }

    /**
     * Ignored tests (e.g. @Ignored) should be marked as skipped.
     */
    @Override
    public void testIgnored(final Description description) throws Exception {
        getCurrentTestStepFrom(description);
        super.testIgnored(description);
        
        Method testMethod = getTestMethodFrom(description);
        if (TestStatus.of(testMethod).isPending()) {
            markCurrentTestAs(PENDING);
        } else if (TestStatus.of(testMethod).isIgnored()) {
            markCurrentTestAs(IGNORED);
        } else {
            markCurrentTestAs(SKIPPED);
        }
        recordCurrentTestStep();
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        markCurrentTestAs(FAILURE);
        recordErrorMessageFrom(failure);
        super.testFailure(failure);
    }

    private void recordErrorMessageFrom(final Failure failure) {
        currentTestStep.failedWith(failure.getMessage(), failure.getException());
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        super.testFinished(description);

        updatePreviousTestFailures();

        if (noPreviousTestHasFailed()) {
            File screenshot = takeScreenshotAtEndOfTestFor(aTestCalled(description));
            currentTestStep.setScreenshot(screenshot);
        }
        updateTestResultIfRequired();
        
        recordCurrentTestStep();
    }
    
    private void recordCurrentTestStep() {
        Preconditions.checkNotNull(currentTestStep);
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

    private void markCurrentTestAs(final TestResult result) {
        currentTestStep.setResult(result);
    }

    protected String aTestCalled(final Description description) {
        return description.getMethodName();
    }

    private File takeScreenshotAtEndOfTestFor(final String testName)
            throws IOException {
        String snapshotName = Inflector.getInstance().underscore(testName);
        return getPhotographer().takeScreenshot(snapshotName);
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    /**
     * Turns a classname into a human-readable title.
     */
    protected String fromTitleIn(final Description description) {
        String testClassName = "";
        if (description.getTestClass() != null) {
            testClassName = description.getTestClass().getSimpleName();
        } else {
            testClassName = description.getClassName();
        }
        String testCaseNameWithUnderscores = inflector.underscore(testClassName);
        return inflector.humanize(testCaseNameWithUnderscores);
    }

    private String testNameFrom(final Description description) {
        String annotatedDescription = annotatedDescriptionOf(description);
        if (annotatedDescription != null) {
            return annotatedDescription;
        }
        return humanizedTestNameFrom(description);
    }


    protected Class<?> userStoryFor(final Description description) {
        Class<?> userStory = null;
        try {
            ForUserStory forUserStory = description.getTestClass().getAnnotation(ForUserStory.class);
            if (forUserStory != null) {
                userStory = forUserStory.value();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } 
        return userStory;
    }
    
    protected String annotatedDescriptionOf(final Description description) {
        String annotatedDescription = null;
        try {
            Method testMethod = getTestMethodFrom(description);
            StepDescription stepDescription = (StepDescription) testMethod.getAnnotation(StepDescription.class);
            if (stepDescription != null) {
                annotatedDescription = stepDescription.value();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return annotatedDescription;
    }

    private Method getTestMethodFrom(final Description description) throws NoSuchMethodException {
        return description.getTestClass().getDeclaredMethod(description.getMethodName(), (Class[]) null);
    }
    
    /**
     * Turns a classname into a human-readable title.
     */
    private String humanizedTestNameFrom(final Description description) {
        
        String testName = description.getMethodName();
        String humanizedName = inflector.humanize(inflector.underscore(testName));
        if (!humanizedName.endsWith(".")) {
            humanizedName = humanizedName + ".";
        }
        return humanizedName;
    }
}