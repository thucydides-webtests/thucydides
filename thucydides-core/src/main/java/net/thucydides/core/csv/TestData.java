package net.thucydides.core.csv;

import java.util.List;
import java.util.Map;

/**
 * A set of test data used in parameterized web tests.
 * Each set of test data is
 * Test data can come from a number of sources, such as CSV files, Excel spreadsheet, arrays, etc.
 */
public interface TestData {
    public List<Map<String, String>> getData();

    <T> List<T> getDataAsInstancesOf(Class<T> clazz);
}
