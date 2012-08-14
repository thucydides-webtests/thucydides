package net.thucydides.junit.listeners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.util.List;

/**
 * Intercepts JUnit events and reports them to Thucydides.
 */
public class JUnitStepListener extends RunListener {

    private BaseStepListener baseStepListener;
    private StepListener[] extraListeners;

    private boolean testStarted;

    public static JUnitStepListenerBuilder withOutputDirectory(File outputDirectory) {
        return new JUnitStepListenerBuilder(outputDirectory);     
    }
    
    protected JUnitStepListener(BaseStepListener baseStepListener, StepListener... listeners) {
        testStarted = false;
        this.baseStepListener = baseStepListener;
        this.extraListeners = listeners;

        registerThucydidesListeners();

    }

    public void registerThucydidesListeners() {
        StepEventBus.getEventBus().registerListener(baseStepListener);

        for(StepListener listener : extraListeners) {
            StepEventBus.getEventBus().registerListener(listener);
        }
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);

    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        StepEventBus.getEventBus().testSuiteFinished();
        super.testRunFinished(result);
    }

    /**
     * Called when a test starts. We also need to start the test suite the first
     * time, as the testRunStarted() method is not invoked for some reason.
     */
    @Override
    public void testStarted(final Description description) {
        startTestSuiteForFirstTest(description);
        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().testStarted(description.getMethodName(),
                                               description.getTestClass());
        startTest();
    }

    private void startTestSuiteForFirstTest(Description description) {
        if (!getBaseStepListener().testSuiteRunning()) {
            StepEventBus.getEventBus().testSuiteStarted(description.getTestClass());
        }
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        StepEventBus.getEventBus().testFinished();
        endTest();
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        startTestIfNotYetStarted(failure.getDescription());
        StepEventBus.getEventBus().testFailed(failure.getException());
        endTest();
    }

    private void startTestIfNotYetStarted(Description description) {
        if (!testStarted) {
           testStarted(description);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        StepEventBus.getEventBus().testIgnored();
        endTest();
    }

    public List<TestOutcome> getTestOutcomes() {
        return baseStepListener.getTestOutcomes();
    }

    public Throwable getError() {
        return baseStepListener.getTestFailureCause();
    }

    public boolean hasRecordedFailures() {
        return baseStepListener.aStepHasFailed();
    }

    public void dropListeners() {
        StepEventBus.getEventBus().dropListener(baseStepListener);
        for(StepListener listener : extraListeners) {
            StepEventBus.getEventBus().dropListener(listener);
        }
    }

    private void startTest() {
        testStarted = true;
    }
    private void endTest() {
        testStarted = false;
    }
}
