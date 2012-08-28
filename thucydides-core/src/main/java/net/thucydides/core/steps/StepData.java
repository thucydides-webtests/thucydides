package net.thucydides.core.steps;

import net.thucydides.core.csv.CSVTestDataSource;
import net.thucydides.core.csv.TestDataSource;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

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
        TestDataSourcePath testDataSourcePath
                = new TestDataSourcePath(Injectors.getInjector().getInstance(EnvironmentVariables.class));
        this.testDataSource = testDataSourcePath.getInstanciatedTestDataPath(testDataSource);
    }

    public static StepData withTestDataFrom(final String testDataSource) {
        return new StepData(testDataSource);
    }

    @SuppressWarnings("unchecked")
    public <T> T run(final T steps) throws IOException {

        useDefaultStepFactoryIfUnassigned();
        TestDataSource testdata = new CSVTestDataSource(testDataSource, separator);

        Class<?> scenarioStepsClass = (Class<?>) steps.getClass().getSuperclass();
        List<T> instanciatedSteps = (List<T>) testdata.getInstanciatedInstancesFrom(scenarioStepsClass, factory);

        DataDrivenStepFactory dataDrivenStepFactory = new DataDrivenStepFactory(factory);
        T stepsProxy = (T) dataDrivenStepFactory.newDataDrivenSteps(scenarioStepsClass, instanciatedSteps);

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
