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

    private Stack<String> stepStack = new Stack<String>();

    public void registerListener(final StepListener listener) {
        registeredListeners.add(listener);
    }

    public void stepStarted(final ExecutedStepDescription executedStepDescription) {
        stepStack.push(executedStepDescription.getName());

        for(StepListener stepListener : registeredListeners) {
            stepListener.stepStarted(executedStepDescription);
        }
    }

    public void testStarted(final String testName) {

        for(StepListener stepListener : registeredListeners) {
            stepListener.testStarted(testName);
        }
    }

    public void testFinished() {
        for(StepListener stepListener : registeredListeners) {
            stepListener.testFinished(new TestStepResult());
        }
    }

    public void stepFinished(final ExecutedStepDescription description) {
        for(StepListener stepListener : registeredListeners) {
            stepListener.stepFinished(description);
        }
    }
}
