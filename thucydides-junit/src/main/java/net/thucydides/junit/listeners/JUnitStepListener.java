package net.thucydides.junit.listeners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.TestStepResult;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.util.List;

import static net.thucydides.core.steps.ExecutedStepDescription.withTitle;

/**
 * Intercepts JUnit events and reports them to Thucydides.
 */
public class JUnitStepListener extends RunListener {

    private BaseStepListener baseStepListener;

    private boolean initialTest = true;

    public JUnitStepListener(final File outputDirectory, final Pages pages) {
        baseStepListener = new BaseStepListener(outputDirectory, pages);
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        if (initialTest) {
            baseStepListener.testRunStartedFor(description.getTestClass());
            initialTest = false;
        }
        baseStepListener.testStarted(description.getMethodName());
    }

    private ExecutedStepDescription withDescriptionFrom(final Description description) {
        Class<? extends ScenarioSteps> stepsClass = (Class<? extends ScenarioSteps>) description.getTestClass();
        return ExecutedStepDescription.of(stepsClass, description.getMethodName());

    }

    @Override
    public void testFinished(final Description description) throws Exception {
        baseStepListener.testFinished(new TestStepResult());
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        baseStepListener.stepFailed(new StepFailure(withTitle(failure.getMessage()), failure.getException()));
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        baseStepListener.stepIgnored(withDescriptionFrom(description));
    }

    public List<TestOutcome> getTestRunResults() {
        return baseStepListener.getTestRunResults();
    }

    public Throwable getError() {
        return baseStepListener.getStepError();
    }

    public boolean hasRecordedFailures() {
        return baseStepListener.aStepHasFailed();
    }

    public void resetStepFailures() {
        baseStepListener.noStepsHaveFailed();
    }
}

