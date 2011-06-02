package net.thucydides.core.csv;

import au.com.bytecode.opencsv.CSVReader;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
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
    private final char separator;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataSource.class);

    public CSVTestDataSource(final String path, char separator) throws IOException {
        this.separator = separator;
        testData = loadTestDataFrom(getDataFileFor(path));
    }

    public CSVTestDataSource(final String path) throws IOException {
        this(path, CSVReader.DEFAULT_SEPARATOR);
    }    
    
    private Reader getDataFileFor(final String path) throws FileNotFoundException {
        if (isAClasspathResource(path)) {
            return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path));
        }
        return new FileReader(new File(path));
    }

    private boolean isAClasspathResource(final String path) {
        return (!validFileSystemPath(path));
    }

    private boolean validFileSystemPath(final String path) {
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
    public <T> List<T> getDataAsInstancesOf(final Class<T> clazz, Object... constructorArgs) {
        List<Map<String, String>> data = getData();

        List<T> resultsList = new ArrayList<T>();
        for (Map<String, String> rowData : data) {
            resultsList.add(newInstanceFrom(clazz, rowData, constructorArgs));
        }
        return resultsList;
    }

    public <T extends ScenarioSteps> List<T> getInstanciatedInstancesFrom(Class<T> clazz, StepFactory factory) {
        List<Map<String, String>> data = getData();

        List<T> resultsList = new ArrayList<T>();
        for (Map<String, String> rowData : data) {
            resultsList.add(newInstanceFrom(clazz, factory, rowData));
        }
        return resultsList;
    }

    private <T extends ScenarioSteps> T newInstanceFrom(final Class<T> clazz,
                                                        final StepFactory factory,
                                                        final Map<String,String> rowData) {
        T newObject = (T) factory.newSteps(clazz);

        Set<String> propertyNames = rowData.keySet();
        for (String columnHeading : propertyNames) {
            String value = rowData.get(columnHeading);

            String property = FieldName.from(columnHeading).inNormalizedForm();
            assignPropertyValue(newObject, property, value);
        }
        return newObject;
    }

    private <T> T newInstanceFrom(final Class<T> clazz,
                                  final Map<String,String> rowData,
                                  final Object... constructorArgs) {
        T newObject = createNewInstanceOf(clazz, constructorArgs);

        Set<String> propertyNames = rowData.keySet();
        for (String columnHeading : propertyNames) {
            String value = rowData.get(columnHeading);

            String property = FieldName.from(columnHeading).inNormalizedForm();
            assignPropertyValue(newObject, property, value);
        }
        return newObject;
    }

    private <T> T createNewInstanceOf(final Class<T> clazz, final Object... constructorArgs) {
        try {
            return newInstanceOf(clazz, constructorArgs);
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

    protected <T> T newInstanceOf(final Class<T> clazz,
                                  final Object... constructorArgs)
                                    throws InstantiationException, IllegalAccessException, InvocationTargetException {

        if (thereIsADefaultConstructorFor(clazz)) {
            return clazz.newInstance();
        } else {
            return invokeConstructorFor(clazz, constructorArgs);
        }
    }

    private <T> T invokeConstructorFor(final Class<T> clazz, final Object[] constructorArgs)
                                    throws InvocationTargetException, IllegalAccessException, InstantiationException {

        Constructor[] constructors = clazz.getDeclaredConstructors();

        for(Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == constructorArgs.length) {
                return (T) constructor.newInstance(constructorArgs);
            }
        }
        throw new IllegalStateException("No matching constructor found for " + clazz + " and " + constructorArgs);
    }

    private <T> boolean thereIsADefaultConstructorFor(final Class<T> clazz) {

        Constructor[] constructors = clazz.getDeclaredConstructors();
        for(Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }
}
