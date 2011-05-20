package net.thucydides.junit.runners;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.TestStepResult;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.util.List;

import static net.thucydides.core.steps.ExecutedStepDescription.withTitle;

public class JUnitStepListener extends RunListener {

    private BaseStepListener baseStepListener;

    public JUnitStepListener(final File outputDirectory, final Pages pages) {
        baseStepListener = new BaseStepListener(outputDirectory, pages);
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        baseStepListener.testStarted(withDescriptionFrom(description));
    }

    private ExecutedStepDescription withDescriptionFrom(Description description) {
        Class<? extends ScenarioSteps> stepsClass = (Class<? extends ScenarioSteps>) description.getTestClass();
        return ExecutedStepDescription.of(stepsClass, description.getMethodName());

    }

    @Override
    public void testStarted(Description description) throws Exception {
    }

    @Override
    public void testFinished(Description description) throws Exception {
        baseStepListener.testFinished(new TestStepResult());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        System.out.println("TEST FAILED!!!!!");
        baseStepListener.stepFailed(new StepFailure(withTitle(failure.getMessage()), failure.getException()));
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        baseStepListener.stepIgnored(withDescriptionFrom(description));
    }


    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
    }

    public List<AcceptanceTestRun> getTestRunResults() {
        return baseStepListener.getTestRunResults();
    }

    private ExecutedStepDescription executedDescriptionFromgetDescriptionFor(Description description) {
        Class testClass = (Class<? extends ScenarioSteps>) description.getTestClass();
        return ExecutedStepDescription.of(testClass, description.getMethodName());
    }

}

