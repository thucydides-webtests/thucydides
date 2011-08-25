package net.thucydides.core.steps;

import net.thucydides.core.model.Story;
import org.omg.IOP.ComponentIdHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An event bus for Step-related notifications.
 * Use this to integrate Thucydides listeners with testing tools.
 * You create a listener (e.g. an instance of BaseStepListener, or your own), register it using
 * 'registerListener', and then
 */
public class StepEventBus {

    private static ThreadLocal<StepEventBus> stepEventBusThreadLocal = new ThreadLocal<StepEventBus>();

    public static StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            stepEventBusThreadLocal.set(new StepEventBus());
        }
        return stepEventBusThreadLocal.get();
    }

    private List<StepListener> registeredListeners = new ArrayList<StepListener>();

    private TestStepResult resultTally;

    private Stack<String> stepStack = new Stack<String>();
    private Stack<Boolean> webdriverSuspensions = new Stack<Boolean>();

    private boolean stepFailed;

    private boolean pendingTest;

    public StepEventBus registerListener(final StepListener listener) {
        registeredListeners.add(listener);
        return this;
    }

    public void testStarted(final String testName) {

        clear();

        for(StepListener stepListener : registeredListeners) {
            stepListener.testStarted(testName);
        }
    }

    public void testSuiteStarted(final Class<?> testClass) {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testSuiteStarted(testClass);
        }
    }

    public void testSuiteStarted(final Story story) {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testSuiteStarted(story);
        }
    }

    public void clear() {

        stepStack.clear();
        clearStepFailures();
        currentTestIsNotPending();
        this.resultTally = new TestStepResult();
        webdriverSuspensions.clear();
    }

    private void currentTestIsNotPending() {
        pendingTest = false;
    }

    private TestStepResult getResultTally() {
        if (resultTally == null) {
            resultTally = new TestStepResult();
        }
        return resultTally;
    }

    public void testFinished() {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testFinished(getResultTally());
        }
        clear();
    }

    private void pushStep(String stepName) {
        stepStack.push(stepName);
    }

    private void popStep() {
        stepStack.pop();
    }

    private void clearStepFailures() {
        stepFailed = false;
    }

    public boolean aStepInTheCurrentTestHasFailed() {
        return stepFailed;
    }

    public boolean isCurrentTestDataDriven() {
        return DataDrivenStep.inProgress();
    }

    public void stepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepStarted(executedStepDescription);
        }
    }

    public void stepFinished() {
        stepDone();
        getResultTally().logExecutedTest();
        for(StepListener stepListener : registeredListeners) {
            stepListener.stepFinished();
        }
    }

    private void stepDone() {
        popStep();
    }

    public void stepFailed(final StepFailure failure) {

        stepDone();
        getResultTally().logFailure(failure);

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepFailed(failure);
        }
        stepFailed = true;
    }

    public void stepIgnored() {

        stepDone();
        getResultTally().logIgnoredTest();

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepIgnored();
        }
    }

    public void stepPending() {

        stepDone();
        getResultTally().logIgnoredTest();

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepPending();
        }
    }

    public void dropListener(final StepListener stepListener) {
        registeredListeners.remove(stepListener);
    }

    public void dropAllListeners() {
        registeredListeners.clear();
    }

    public boolean webdriverCallsAreSuspended() {
        return aStepInTheCurrentTestHasFailed() || !webdriverSuspensions.isEmpty();
    }

    public void reenableWebdriverCalls() {
        webdriverSuspensions.pop();
    }

    public void temporarilySuspendWebdriverCalls() {
        webdriverSuspensions.push(true);
    }

    /**
     * The test failed, but not during the execution of a step.
     * @param cause the underlying cause of the failure.
     */
    public void testFailed(final Throwable cause) {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testFailed(cause);
        }
    }

    /**
     * Mark the current test method as pending.
     * The test will stil be executed to record the steps, but any webdriver calls will be skipped.
     */
    public void testPending() {
        pendingTest = true;
    }

    public boolean currentTestIsPending() {
        return pendingTest;
    }

    public void testIgnored() {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testIgnored();
        }
    }

    public boolean areStepsRunning() {
        return !stepStack.isEmpty();
    }
}
