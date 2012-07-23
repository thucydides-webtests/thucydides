package net.thucydides.core.csv;

import au.com.bytecode.opencsv.CSVReader;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
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
    private final char separator;

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataSource.class);

    public CSVTestDataSource(final String path, final char separatorValue) throws IOException {
        this.separator = separatorValue;
        testData = loadTestDataFrom(getDataFileFor(path));
    }

    public CSVTestDataSource(final String path) throws IOException {
        this(path, CSVReader.DEFAULT_SEPARATOR);
    }

    public static boolean validTestDataPath(final String path) {
        if (validFileSystemPath(path)) {
            return true;
        } else {
            URL testDataUrl = CSVTestDataSource.class.getClassLoader().getResource(path);
            return (testDataUrl != null ) && new File(testDataUrl.getFile()).exists();
        }
    }

    private Reader getDataFileFor(final String path) throws FileNotFoundException {
        if (isAClasspathResource(path)) {
            try {
                return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path));
            } catch(Throwable e) {
                LOGGER.error(e.getMessage(), e);
                throw new FileNotFoundException("Could not load test data from " + path);
            }
        }
        return new FileReader(new File(path));
    }

    private static boolean isAClasspathResource(final String path) {
        return (!validFileSystemPath(path));
    }

    private static boolean validFileSystemPath(final String path) {
        File file = new File(path);
        return file.exists();
    }

    protected List<Map<String, String>> loadTestDataFrom(final Reader testDataReader) throws IOException {

        CSVReader reader = new CSVReader(testDataReader, separator);
        List<String[]> rows = reader.readAll();

        List<Map<String, String>> loadedData = new ArrayList<Map<String, String>>();
        String[] titleRow = rows.get(0);

        for (int i = 1; i < rows.size(); i++) {
            String[] dataRow = rows.get(i);
            loadedData.add(dataEntryFrom(titleRow, dataRow));
        }
        return loadedData;
    }

    private Map<String, String> dataEntryFrom(final String[] titleRow, final String[] dataRow) {
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
    public <T> List<T> getDataAsInstancesOf(final Class<T> clazz, final Object... constructorArgs) {
        List<Map<String, String>> data = getData();

        List<T> resultsList = new ArrayList<T>();
        for (Map<String, String> rowData : data) {
            resultsList.add(newInstanceFrom(clazz, rowData, constructorArgs));
        }
        return resultsList;
    }

    public <T extends ScenarioSteps> List<T> getInstanciatedInstancesFrom(final Class<T> clazz,
                                                                          final StepFactory factory) {
        List<Map<String, String>> data = getData();

        List<T> resultsList = new ArrayList<T>();
        for (Map<String, String> rowData : data) {
            resultsList.add(newInstanceFrom(clazz, factory, rowData));
        }
        return resultsList;
    }

    private <T> T newInstanceFrom(final Class<T> clazz,
                                  final Map<String,String> rowData,
                                  final Object... constructorArgs) {

        T newObject = createNewInstanceOf(clazz, constructorArgs);
        assignPropertiesFromTestData(clazz, rowData, newObject);
        return newObject;
    }

    private <T extends ScenarioSteps> T newInstanceFrom(final Class<T> clazz,
                                                        final StepFactory factory,
                                                        final Map<String,String> rowData) {
        T newObject = factory.getUniqueStepLibraryFor(clazz);
        assignPropertiesFromTestData(clazz, rowData, newObject);
        return newObject;
    }

    private <T> void assignPropertiesFromTestData(final Class<T> clazz,
                                                  final Map<String, String> rowData,
                                                  final T newObject) {
        Set<String> propertyNames = rowData.keySet();

        boolean validPropertyFound = false;
        for (String columnHeading : propertyNames) {
            String value = rowData.get(columnHeading);
            String property = FieldName.from(columnHeading).inNormalizedForm();

            if (assignPropertyValue(newObject, property, value)) {
                validPropertyFound = true;
            }
        }
        if (!validPropertyFound) {
            throw new FailedToInitializeTestData("No properties or public fields matching the data columns were found "
                                                 + "or could be assigned for the class " + clazz.getName()
                                                 + "using test data: " + rowData);
        }
    }

    protected <T> T createNewInstanceOf(final Class<T> clazz, final Object... constructorArgs) {
        try {
            return InstanceBuilder.newInstanceOf(clazz, constructorArgs);
        } catch (Exception e) {
            LOGGER.error("Could not create test data bean", e);
            throw new FailedToInitializeTestData("Could not create test data beans", e);
        }
    }

    protected <T> boolean assignPropertyValue(final T newObject, final String property, final String value) {
        boolean valueWasAssigned = true;
        try {
            InstanceBuilder.inObject(newObject).setPropertyValue(property, value);
        } catch (FailedToInitializeTestData e) {
            valueWasAssigned = false;
        }
        return valueWasAssigned;
    }

}
