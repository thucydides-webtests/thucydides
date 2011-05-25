package net.thucydides.core.csv;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test data from a CSV file.
 */
public class CSVTestDataSource implements TestDataSource {

    private final List<Map<String, String>> testData;

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataSource.class);

    public CSVTestDataSource(final String path) throws IOException {
        testData = loadTestDataFrom(getDataFileFor(path));
    }

    private Reader getDataFileFor(String path) throws FileNotFoundException {
        if (isAClasspathResource(path)) {
            return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path));
        }
        return new FileReader(new File(path));
    }

    private boolean isAClasspathResource(String path) {
        return (!validFileSystemPath(path));
    }

    private boolean validFileSystemPath(String path) {
        File file = new File(path);
        return file.exists();
    }

    protected List<Map<String, String>> loadTestDataFrom(final Reader testDataReader) throws IOException {

        CSVReader reader = new CSVReader(testDataReader);
        List<String[]> rows = reader.readAll();

        List<Map<String, String>> loadedData = new ArrayList<Map<String, String>>();
        String[] titleRow = rows.get(0);

        for (int i = 1; i < rows.size(); i++) {
            String[] dataRow = rows.get(i);
            loadedData.add(dataEntryFrom(titleRow, dataRow));
        }
        return loadedData;
    }

    private Map dataEntryFrom(final String[] titleRow, final String[] dataRow) {
        Map<String, String> dataset = new HashMap<String, String>();

        for (int column = 0; column < titleRow.length; column++) {
            if (column < dataRow.length) {
                String title = titleRow[column].trim();
                String value = dataRow[column].trim();
                dataset.put(title, value);
            }
        }

        return dataset;
    }

    public List<Map<String, String>> getData() {
        return testData;
    }

    /**
     * Returns the test data as a list of JavaBean instances.
     */
    public <T> List<T> getDataAsInstancesOf(final Class<T> clazz) {
        List<Map<String, String>> testData = getData();

        List<T> resultsList = new ArrayList<T>();
        for (Map<String, String> rowData : testData) {
            resultsList.add(newInstanceFrom(clazz, rowData));
        }
        return resultsList;
    }

    private <T> T newInstanceFrom(final Class<T> clazz, final Map<String,String> rowData) {
        T newObject = createNewInstanceOf(clazz);

        Set<String> propertyNames = rowData.keySet();
        for (String columnHeading : propertyNames) {
            String value = rowData.get(columnHeading);

            String property = FieldName.from(columnHeading).inNormalizedForm();
            assignPropertyValue(newObject, property, value);
        }
        return newObject;
    }

    private <T> T createNewInstanceOf(final Class<T> clazz) {
        try {
            return newInstanceOf(clazz);
        } catch (Exception e) {
            LOGGER.error("Could not create test data bean", e);
            throw new FailedToInitializeTestData("Could not create test data beans", e);
        }
    }

    private <T> void assignPropertyValue(final T newObject, final String property, final String value) {
        try {
            setPropertyValue(newObject, property, value);

        } catch (NoSuchMethodException e) {
            LOGGER.info("Skipping unknown field");
        } catch (Exception e) {
            LOGGER.error("Could not create test data bean", e);
            throw new FailedToInitializeTestData("Could not create test data beans", e);
        }
    }

    protected <T> void setPropertyValue(final T newObject, 
                                        final String property, 
                                        final String value) throws IllegalAccessException, 
                                                                   InvocationTargetException, 
                                                                   NoSuchMethodException {
        PropertyUtils.setProperty(newObject, property, value);
    }

    protected <T> T newInstanceOf(final Class<T> clazz) throws InstantiationException,  
                                                               IllegalAccessException {
        return clazz.newInstance();
    }
}
