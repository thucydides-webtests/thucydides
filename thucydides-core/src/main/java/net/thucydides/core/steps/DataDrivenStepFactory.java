package net.thucydides.core.steps;

import net.sf.cglib.proxy.Enhancer;
import net.thucydides.core.pages.Pages;

import java.util.List;

/**
 * Create a proxy for scenario steps objects to be used for data-driven tests.
 */
public class DataDrivenStepFactory {

    private static final Class<?>[] CONSTRUCTOR_ARG_TYPES = {Pages.class};

    public static ScenarioSteps newDataDrivenSteps(final Class<? extends ScenarioSteps> scenarioStepsClass,
                                                   final List<? extends ScenarioSteps> instantiatedSteps) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(scenarioStepsClass);

        DataDrivenStepInterceptor stepInterceptor = new DataDrivenStepInterceptor(instantiatedSteps);
        enhancer.setCallback(stepInterceptor);

        Object[] arguments = new Object[1];
        arguments[0] = instantiatedSteps.get(0).getPages();
        ScenarioSteps steps = (ScenarioSteps) enhancer.create(CONSTRUCTOR_ARG_TYPES, arguments);

        return steps;
    }
}
