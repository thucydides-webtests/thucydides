package net.thucydides.core.steps;

import net.sf.cglib.proxy.Enhancer;
import net.thucydides.core.pages.Pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces an instance of a set of requirement steps for use in the acceptance tests.
 * Requirement steps navigate through pages using a WebDriver driver.
 *
 */
public class StepFactory {

    private final Pages pages;

    private final List<ScenarioSteps> managedSteps = new ArrayList<ScenarioSteps>();

    private final Map<Class<? extends ScenarioSteps>, ScenarioSteps> index
            = new HashMap<Class<? extends ScenarioSteps>, ScenarioSteps>();
    /**
     * Create a new step factory.
     * All step factories need a Pages object, which is passed to ScenarioSteps objects when they
     * are created.
     */
    public StepFactory(final Pages pages) {
        this.pages = pages;
    }

    private static final Class<?>[] CONSTRUCTOR_ARG_TYPES = {Pages.class};
    
    /**
     * Returns a new ScenarioSteps instance, of the specified type.
     * This is actually a proxy that allows reporting and screenshots to
     * be performed at each step.
     */
    public <T extends ScenarioSteps> T getStepLibraryFor(final Class<T> scenarioStepsClass) {
        if (isStepLibraryInstantiatedFor(scenarioStepsClass)) {
            return getManagedStepLibraryFor(scenarioStepsClass);
        } else {
            return instantiateNewStepLibraryFor(scenarioStepsClass);
        }
    }

    public <T extends ScenarioSteps> T getUniqueStepLibraryFor(final Class<T> scenarioStepsClass) {
        return instantiateUniqueStepLibraryFor(scenarioStepsClass);
    }

    private boolean isStepLibraryInstantiatedFor(Class<? extends ScenarioSteps> scenarioStepsClass) {
        return index.containsKey(scenarioStepsClass);
    }


    @SuppressWarnings("unchecked")
	private <T extends ScenarioSteps> T getManagedStepLibraryFor(Class<T> scenarioStepsClass) {
        return (T) index.get(scenarioStepsClass);
    }

    private <T extends ScenarioSteps> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass) {
        T steps = createProxyStepLibrary(scenarioStepsClass);

        recordManagedStepLibrary(steps);

        indexStepLibrary(scenarioStepsClass, steps);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        return steps;
    }

    private <T extends ScenarioSteps> T instantiateUniqueStepLibraryFor(Class<T> scenarioStepsClass) {
        T steps = createProxyStepLibrary(scenarioStepsClass);

        recordManagedStepLibrary(steps);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        return steps;
    }

    @SuppressWarnings("unchecked")
	private <T extends ScenarioSteps> T createProxyStepLibrary(Class<T> scenarioStepsClass) {
        Enhancer e = new Enhancer();
        e.setSuperclass(scenarioStepsClass);
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        e.setCallback(stepInterceptor);

        Object[] arguments = new Object[1];
        arguments[0] = pages;
        return (T) e.create(CONSTRUCTOR_ARG_TYPES, arguments);
    }

    private <T extends ScenarioSteps> void recordManagedStepLibrary(T steps) {
        managedSteps.add(steps);
    }

    private <T extends ScenarioSteps> void indexStepLibrary(Class<T> scenarioStepsClass, T steps) {
        index.put(scenarioStepsClass, steps);
    }

    private void instantiateAnyNestedStepLibrariesIn(final ScenarioSteps steps,
                                                     final Class<? extends ScenarioSteps> scenarioStepsClass){
        StepAnnotations.injectNestedScenarioStepsInto(steps, this, scenarioStepsClass);
    }

    public void notifyStepFinished() {
        for(ScenarioSteps step : managedSteps) {
            step.done();
        }
    }


}