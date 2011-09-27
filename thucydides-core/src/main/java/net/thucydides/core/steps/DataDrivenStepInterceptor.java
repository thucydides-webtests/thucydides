package net.thucydides.core.steps;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Invoke a step multiple times, each time initialized with a different set of test data.
 */
public class DataDrivenStepInterceptor implements MethodInterceptor {

    private List<? extends ScenarioSteps> instantiatedSteps;

    public DataDrivenStepInterceptor(List<? extends ScenarioSteps> instantiatedSteps) {
        this.instantiatedSteps = instantiatedSteps;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        DataDrivenStep.startDataDrivenStep();
        for (ScenarioSteps steps : instantiatedSteps) {
            runMethodAndIgnoreExceptions(steps, proxy, method, args);
        }
        DataDrivenStep.endDataDrivenStep();
        return null;
    }

    private void runMethodAndIgnoreExceptions(ScenarioSteps steps,  MethodProxy proxy, Method method, Object[] args) throws Throwable {
         if (!method.getName().equals("finalize")) {
            proxy.invoke(steps, args);
         }
    }
}
