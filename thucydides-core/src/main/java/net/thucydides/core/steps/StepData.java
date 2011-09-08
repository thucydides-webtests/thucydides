package net.thucydides.core.steps;

import net.thucydides.core.csv.CSVTestDataSource;
import net.thucydides.core.csv.TestDataSource;

import java.io.IOException;
import java.util.List;

/**
 * Data-driven test step execution.
 * These methods let you load
 */
public final class StepData {

    private final String testDataSource;
    private char separator = ',';
    private StepFactory factory;

    private static final ThreadLocal<StepFactory> factoryThreadLocal = new ThreadLocal<StepFactory>();

    public StepData(final String testDataSource) {
        this.testDataSource = testDataSource;
    }

    public static StepData withTestDataFrom(final String testDataSource) {
        return new StepData(testDataSource);
    }

    @SuppressWarnings("unchecked")
    public <T extends ScenarioSteps> T run(final T steps) throws IOException {

        useDefaultStepFactoryIfUnassigned();
        TestDataSource testdata = new CSVTestDataSource(testDataSource, separator);

        Class<? extends ScenarioSteps> scenarioStepsClass = (Class<? extends ScenarioSteps>) steps.getClass().getSuperclass();
        List<T> instanciatedSteps = (List<T>) testdata.getInstanciatedInstancesFrom(scenarioStepsClass, factory);

        T stepsProxy = (T) DataDrivenStepFactory.newDataDrivenSteps(scenarioStepsClass, instanciatedSteps);

        return stepsProxy;
    }

    private void useDefaultStepFactoryIfUnassigned() {
        if (factory == null) {
            factory = getDefaultStepFactory();
        }
    }

    public StepData usingFactory(final StepFactory factory) {
        this.factory = factory;
        return this;
    }

    public static void setDefaultStepFactory(final StepFactory factory) {
        factoryThreadLocal.set(factory);
    }

    public static StepFactory getDefaultStepFactory() {
        return factoryThreadLocal.get();
    }

    public StepData separatedBy(char newSeparator) {
        this.separator = newSeparator;
        return this;
    }
}
