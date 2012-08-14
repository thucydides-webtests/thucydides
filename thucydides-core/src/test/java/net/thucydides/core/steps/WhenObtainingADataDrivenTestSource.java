package net.thucydides.core.steps;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Test;
import org.junit.runners.model.TestClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * A description goes here.
 * User: john
 * Date: 10/08/12
 * Time: 2:51 PM
 */
public class WhenObtainingADataDrivenTestSource {
//    @UseTestDataFrom(value="${DATADIR}/simple-semicolon-data.csv", separator=';')
//    final static class DataDrivenTestScenarioFromSpecifiedDataDirectory {}

    @Test
    public void should_convert_data_file_path_to_operating_system_localized_path() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("thucydides.data.dir","C:\\some\\dir");

        TestDataSourcePath testDataSourcePath = new TestDataSourcePath(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "\\";
            }
        };

        assertThat(testDataSourcePath.getInstanciatedTestDataPath("${DATADIR}/simple-semicolon-data.csv"), is("C:\\some\\dir\\simple-semicolon-data.csv"));
    }

    @Test
    public void should_convert_data_file_path_to_operating_system_localized_path_in_unix() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("thucydides.data.dir","/some/dir");

        TestDataSourcePath testDataSourcePath = new TestDataSourcePath(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };

        assertThat(testDataSourcePath.getInstanciatedTestDataPath("${DATADIR}/simple-semicolon-data.csv"), is("/some/dir/simple-semicolon-data.csv"));
    }
}
