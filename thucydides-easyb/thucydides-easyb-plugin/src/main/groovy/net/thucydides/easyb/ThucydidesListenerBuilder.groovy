package net.thucydides.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.listener.ListenerBuilder

 /**
 * Used by Easyb to instantiate an ExecutionListener for the easyb thucydides plugin.
 * This class maintains a thread-local singleton to handle result processing and report generation
 * from the easyb stories.
 */
class ThucydidesListenerBuilder implements ListenerBuilder {

    public static final ThreadLocal executionListenerThreadLocal = new ThreadLocal();

    ThucydidesListenerBuilder() {
    }

    ExecutionListener get() {
        exeuctionListenerMustHaveBeenAssigned();
        return getListener();
    }

    public void exeuctionListenerMustHaveBeenAssigned() {
        if (!getListener()) {
            instantiateStepListener();
        }
    }

    private instantiateStepListener() {
        resetListener();
        setListener(new ThucydidesExecutionListener());
    }


    private static void setListener(ThucydidesExecutionListener executionListener) {
        executionListenerThreadLocal.set(executionListener);
    }

    public static void resetListener() {
        executionListenerThreadLocal.remove();
    }

    private static ThucydidesExecutionListener getListener() {
        return executionListenerThreadLocal.get();
    }

}
