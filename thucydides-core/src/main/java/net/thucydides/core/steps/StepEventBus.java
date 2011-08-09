package net.thucydides.core.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A simple event bus for Step-related notifications.
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

    public void registerListener(final StepListener listener) {
        registeredListeners.add(listener);
    }

    public void testStarted(final String testName) {

        clear();

        for(StepListener stepListener : registeredListeners) {
            stepListener.testStarted(testName);
        }
    }

    public void clear() {
        stepStack.clear();
        stepFailed = false;
        this.resultTally = new TestStepResult();
    }

    public void testFinished() {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testFinished(resultTally);
        }
        clear();
    }

    private void pushStep(String stepName) {
        stepStack.push(stepName);
    }

    private void popStep() {
        stepStack.pop();
        if (stepStack.isEmpty()) {
            clearStepFailures();
        }
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

    public void stepFinished(final ExecutedStepDescription description) {
        resultTally.logExecutedTest();
        for(StepListener stepListener : registeredListeners) {
            stepListener.stepFinished(description);
        }
        popStep();
    }

    public void stepFailed(final StepFailure failure) {

        resultTally.logFailure(failure);

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepFailed(failure);
        }
        stepFailed = true;
    }

    public void stepIgnored(ExecutedStepDescription description) {

        resultTally.logIgnoredTest();

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepIgnored(description);
        }
    }

    public void dropListener(final StepListener stepListener) {
        registeredListeners.remove(stepListener);
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
}
