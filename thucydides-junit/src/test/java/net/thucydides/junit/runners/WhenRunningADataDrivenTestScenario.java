package net.thucydides.junit.runners;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.junit.annotations.InvalidManagedPagesFieldException;
import net.thucydides.junit.annotations.InvalidStepsFieldException;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.steps.ScenarioStepListener;
import net.thucydides.samples.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class WhenRunningADataDrivenTestScenario extends AbstractTestStepRunnerTest {


    TestableWebDriverFactory webDriverFactory;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Before
    public void initMocks() {
        File temporaryDirectory = tempFolder.newFolder("screenshots");
        webDriverFactory = new TestableWebDriverFactory(temporaryDirectory);
    }

    @Test
    public void a_data_driven_test_driver_should_run_one_test_per_row_of_data() throws Throwable  {

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        assertThat(executedScenarios.size(), is(3));
    }

    @Test
    public void a_separate_xml_report_should_be_generated_from_each_row_of_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new XMLFileFilter());
        assertThat(reports.length, is(3));
    }

    @Test
    public void xml_report_names_should_reflect_the_test_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportFilenames, allOf(hasItem("sample_data_driven_scenario_happy_day_scenario_a_1.xml"),
                hasItem("sample_data_driven_scenario_happy_day_scenario_b_2.xml"),
                hasItem("sample_data_driven_scenario_happy_day_scenario_c_3.xml")));

    }

    @Test
    public void xml_report_contents_should_reflect_the_test_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));

        assertThat(reportContents, hasItem(containsString("Happy day scenario [a/1]")));
        assertThat(reportContents, hasItem(containsString("Happy day scenario [b/2]")));
        assertThat(reportContents, hasItem(containsString("Happy day scenario [c/3]")));

    }

    private List<String> filenamesOf(File[] files) {
        List<String> filenames = new ArrayList<String>();
        for(File file : files) {
            filenames.add(file.getName());
        }
        return filenames;
    }


    private List<String> contentsOf(File[] files) throws IOException {
        List<String> contents = new ArrayList<String>();
        for(File file : files) {
            contents.add(stringContentsOf(file));
        }
        return contents;
    }

    private String stringContentsOf(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

    @Test
    public void html_report_names_should_reflect_the_test_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new HTMLFileFilter());
        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new HTMLFileFilter()));
        assertThat(reportFilenames, allOf(hasItem("sample_data_driven_scenario_happy_day_scenario_a_1.html"),
                hasItem("sample_data_driven_scenario_happy_day_scenario_b_2.html"),
                hasItem("sample_data_driven_scenario_happy_day_scenario_c_3.html")));
    }


    @Test
    public void a_separate_html_report_should_be_generated_from_each_row_of_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new HTMLFileFilter());
        assertThat(reports.length, is(3));
    }

    @Test
    public void a_data_driven_test_driver_should_open_one_browser_per_row_of_data() throws Throwable  {
       
        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);
        runner.run(new RunNotifier());

        assertThat(webDriverFactory.fireFoxOpenedCount(), is(3));
    }


    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".html");
        }
    }

    private class XMLFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".xml");
        }
    }
}
