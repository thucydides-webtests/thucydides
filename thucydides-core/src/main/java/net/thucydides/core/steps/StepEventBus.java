package net.thucydides.core.steps;

import java.util.ArrayList;
import java.util.List;

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

    public void registerListener(final StepListener listener) {
        registeredListeners.add(listener);
    }

    public void stepStarted(final ExecutedStepDescription executedStepDescription) {
        for(StepListener stepListener : registeredListeners) {
            stepListener.stepStarted(executedStepDescription);
        }
    }
}
