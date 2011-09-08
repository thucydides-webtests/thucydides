package net.thucydides.core.csv;

import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;

import java.util.List;
import java.util.Map;

/**
 * A set of test data used in parameterized web tests.
 * Test data can come from a number of sources, such as CSV files, Excel spreadsheet, arrays, etc.
 */
public interface TestDataSource {
    List<Map<String, String>> getData();

    <T> List<T> getDataAsInstancesOf(Class<T> clazz, Object... constructorArgs);

    <T extends ScenarioSteps> List<T> getInstanciatedInstancesFrom(Class<T> clazz, StepFactory factory);

}
