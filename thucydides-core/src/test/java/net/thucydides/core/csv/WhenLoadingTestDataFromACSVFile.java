package net.thucydides.core.csv;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class WhenLoadingTestDataFromACSVFile {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File temporaryDirectory;

    @Before
    public void setupTemporaryDirectory() {
        temporaryDirectory = temporaryFolder.newFolder("testdata");
    }

    protected File useTestDataIn(String filename, String... data) throws IOException {
        File testDataFile = new File(temporaryDirectory, filename);

        BufferedWriter out = new BufferedWriter(new FileWriter(testDataFile));

        for (String row : data) {
            out.write(row);
            out.newLine();
        }
        out.close();

        return testDataFile;
    }

    @Test
    public void should_be_able_to_load_test_data_from_a_specified_CSV_file() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData, is(notNullValue()));
        assertThat(loadedData.size(), is(1));
    }

    @Test
    public void should_be_able_to_load_test_data_from_the_classpath() throws IOException {

        TestDataSource testdata = new CSVTestDataSource("testdata/test.csv");

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData, is(notNullValue()));
        assertThat(loadedData.size(), is(3));
    }


    @Test
    public void should_use_column_headings_to_identify_fields_in_the_test_data() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_ignore_unknown_headings() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unused",
                "Bill, 10 main street, 123456789,   extra data here");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_allow_non_comma_separators_to_be_used() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name; address;        phone",
                "Bill; 10 main street, BillVille; 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath(),';');

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street, BillVille"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_ignore_unknown_headings_with_no_matching_columns() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unused",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_ignore_extra_columns_in_data_rows() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789,   extra data here");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_read_multple_rows_of_data() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789",
                "Tim,  12 main street, 123456700");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();

        assertThat(loadedData.size(), is(2));
        Map<String,String> row1 = loadedData.get(0);
        assertThat(row1.get("name"), is("Bill"));
        assertThat(row1.get("address"), is("10 main street"));
        assertThat(row1.get("phone"), is("123456789"));

        Map<String,String> row2 = loadedData.get(1);
        assertThat(row2.get("name"), is("Tim"));
        assertThat(row2.get("address"), is("12 main street"));
        assertThat(row2.get("phone"), is("123456700"));

    }

    @Test
    public void should_load_nothing_if_no_data_is_present() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv", "");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData.size(), is(0));
    }

    @Test
    public void should_load_nothing_if_only_the_titles_are_present() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                                          "name, address,        phone");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData.size(), is(0));
    }

    @Test
    public void should_load_data_as_objects() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        assertThat(loadedData.size(), is(1));
    }

    @Test
    public void should_load_multiple_rows_of_data_as_objects() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789",
                "Tim,  12 main street, 123456700");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        assertThat(loadedData.size(), is(2));
    }

    @Test
    public void should_assign_fields_in_loaded_objects() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person, allOf(hasProperty("name", is("Bill")),
                hasProperty("address", is("10 main street")),
                hasProperty("phone", is("123456789"))));
    }

    @Test
    public void empty_fields_should_be_set_to_empty_strings() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, , 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getAddress(), is(""));
    }

    @Test
    public void unknown_fields_should_be_ignored() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unknown",
                "Bill, 10 main street, 123456789, whatever");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getName(), is("Bill"));
        assertThat(person.getAddress(), is("10 main street"));
        assertThat(person.getPhone(), is("123456789"));
    }

    @Test
    public void should_work_with_upper_case_column_headings() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "NAME, ADDRESS,        PHONE",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getName(), is("Bill"));
        assertThat(person.getAddress(), is("10 main street"));
        assertThat(person.getPhone(), is("123456789"));
    }

    @Test
    public void should_camel_case_upper_cased_column_headings_with_spaces() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "NAME, ADDRESS,        PHONE, DATE OF BIRTH",
                "Bill, 10 main street, 123456789, 10/10/1980");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getDateOfBirth(), is("10/10/1980"));
    }

    class CSVTestDataSourceThrowsInstantiationException extends CSVTestDataSource {

        public CSVTestDataSourceThrowsInstantiationException(String sourceFile) throws IOException {
            super(sourceFile);
        }

        @Override
        protected <T> T newInstanceOf(Class<T> clazz, Object... constructorArgs) throws InstantiationException, IllegalAccessException {
            throw new InstantiationException("Oh nose!");
        }
    }

    @Test(expected = FailedToInitializeTestData.class)
    public void caller_should_be_notified_if_test_data_cannot_be_instantiated() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unknown",
                "Bill, 10 main street, 123456789, whatever");

        TestDataSource testdata = new CSVTestDataSourceThrowsInstantiationException(testDataFile.getAbsolutePath());

        testdata.getDataAsInstancesOf(Person.class);
    }

    class CSVTestDataSourceThrowsIllegalAccessException extends CSVTestDataSource {

        public CSVTestDataSourceThrowsIllegalAccessException(String sourceFile) throws IOException {
            super(sourceFile);
        }

        @Override
        protected <T> T newInstanceOf(Class<T> clazz, Object... constructorArgs) throws InstantiationException, IllegalAccessException {
            throw new IllegalAccessException("Oh nose!");
        }
    }

    @Test(expected = FailedToInitializeTestData.class)
    public void caller_should_be_notified_if_test_data_cannot_be_accessed() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSourceThrowsIllegalAccessException(testDataFile.getAbsolutePath());

        testdata.getDataAsInstancesOf(Person.class);
    }


    class CSVTestDataSourceThrowsIllegalAccessExceptionOnPropertySet extends CSVTestDataSource {

        public CSVTestDataSourceThrowsIllegalAccessExceptionOnPropertySet(String sourceFile) throws IOException {
            super(sourceFile);
        }

        @Override
        protected <T> void setPropertyValue(T newObject, String property, String value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            throw new IllegalAccessException();
        }
    }

    @Test(expected = FailedToInitializeTestData.class)
    public void caller_should_be_notified_if_bean_property_cannot_be_initialized() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSourceThrowsIllegalAccessExceptionOnPropertySet(testDataFile.getAbsolutePath());

        testdata.getDataAsInstancesOf(Person.class);
    }
}
