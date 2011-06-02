package net.thucydides.core.steps;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Invoke a step multiple times, each time initialized with a different set of test data.
 */
public class DataDrivenStepInterceptor implements MethodInterceptor {

    private List<? extends ScenarioSteps> instantiatedSteps;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDrivenStepInterceptor.class);

    public DataDrivenStepInterceptor(List<? extends ScenarioSteps> instantiatedSteps) {
        this.instantiatedSteps = instantiatedSteps;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        for (ScenarioSteps steps : instantiatedSteps) {
            runMethodAndIgnoreExceptions(steps, proxy, method, args);
        }
        return null;
    }

    private void runMethodAndIgnoreExceptions(ScenarioSteps steps,  MethodProxy proxy, Method method, Object[] args) {
         try {
             proxy.invoke(steps, args);
         } catch (Throwable e) {
            LOGGER.info("Skipping exception when running data-driven step tests: ", e.getMessage());
         }
    }
}
