package net.thucydides.core.steps;

import net.sf.cglib.proxy.Enhancer;
import net.thucydides.core.Thucydides;
import net.thucydides.core.pages.Pages;

import java.util.List;

/**
 * Create a proxy for scenario steps objects to be used for data-driven tests.
 */
public class DataDrivenStepFactory {

    private final StepFactory factory;

    public DataDrivenStepFactory(StepFactory factory) {
        this.factory = factory;
    }

    public Object newDataDrivenSteps(final Class<?> scenarioStepsClass,
                                            final List<?> instantiatedSteps) {

        DataDrivenStepInterceptor stepInterceptor = new DataDrivenStepInterceptor(instantiatedSteps);
        return factory.instantiateNewStepLibraryFor(scenarioStepsClass, stepInterceptor);
    }
}
