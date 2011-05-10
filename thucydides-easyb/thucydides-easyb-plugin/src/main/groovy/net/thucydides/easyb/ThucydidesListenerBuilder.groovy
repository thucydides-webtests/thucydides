package net.thucydides.easyb

import org.easyb.listener.ListenerBuilder
import org.easyb.listener.ExecutionListener
import net.thucydides.core.steps.StepListener

class ThucydidesListenerBuilder implements ListenerBuilder{

    private final ThucydidesExecutionListener executionListener;

    ThucydidesListenerBuilder(StepListener stepListener) {
        executionListener = new ThucydidesExecutionListener(stepListener);
    }

    ExecutionListener get() {
        return executionListener;
    }
}
