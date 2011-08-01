package net.thucydides.junit.runners;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.samples.SampleCSVDataDrivenScenario;
import net.thucydides.samples.SampleDataDrivenScenario;
import net.thucydides.samples.SampleParallelDataDrivenScenario;
import net.thucydides.samples.SamplePassingScenarioWithTestSpecificData;
import net.thucydides.samples.SampleScenarioSteps;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.thucydides.core.steps.StepData.withTestDataFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class WhenRunningADataDrivenTestScenario extends AbstractTestStepRunnerTest {


    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Mock
    WebDriverFactory webDriverFactory;

    @Before
    public void initMocks() {
        File temporaryDirectory = tempFolder.newFolder("screenshots");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void a_data_driven_test_driver_should_run_one_test_per_row_of_data() throws Throwable  {

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        assertThat(executedScenarios.size(), is(10));
    }

    @Test
    public void by_default_the_same_browser_instance_should_be_used_throughout() throws Throwable  {

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);
        runner.run(new RunNotifier());

        verify(webDriverFactory, never()).restartBrowser();
    }

    @Test
    public void a_data_driven_test_should_also_be_able_to_use_data_from_a_CSV_file() throws Throwable  {

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleCSVDataDrivenScenario.class,
                                                                                 webDriverFactory);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        assertThat(executedScenarios.size(), is(3));
    }


    @Test
    public void a_separate_xml_report_should_be_generated_from_each_row_of_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new XMLFileFilter());
        assertThat(reports.length, is(10));
    }

    @Test
    public void a_separate_xml_report_should_be_generated_from_each_row_of_data_in_a_CSV_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleCSVDataDrivenScenario.class,
                                                                                 webDriverFactory);

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

        runner.run(new RunNotifier());

        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportFilenames, allOf(hasItem("sample_data_driven_scenario_happy_day_scenario_a_1.xml"),
                hasItem("sample_data_driven_scenario_happy_day_scenario_b_2.xml"),
                hasItem("sample_data_driven_scenario_happy_day_scenario_c_3.xml")));

    }

    @Test
    public void xml_report_names_should_reflect_the_test_data_from_the_csv_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleCSVDataDrivenScenario.class,
                                                                                 webDriverFactory);

        runner.run(new RunNotifier());

        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportFilenames, allOf(hasItem("sample_c_s_v_data_driven_scenario_data_driven_test_Jack_Black.xml"),
                hasItem("sample_c_s_v_data_driven_scenario_data_driven_test_Joe_Smith.xml")));

    }

    @Test
    public void xml_report_contents_should_reflect_the_test_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleDataDrivenScenario.class,
                                                                                 webDriverFactory);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));

        assertThat(reportContents, hasItem(containsString("Happy day scenario [a/1]")));
        assertThat(reportContents, hasItem(containsString("Happy day scenario [b/2]")));
        assertThat(reportContents, hasItem(containsString("Happy day scenario [c/3]")));

    }

    @Test
    public void xml_report_contents_should_reflect_the_test_data_from_the_csv_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleCSVDataDrivenScenario.class,
                                                                                 webDriverFactory);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));

         assertThat(reportContents, hasItem(containsString("Jack Black")));
         assertThat(reportContents, hasItem(containsString("Joe Smith")));
    }


    @Test
    public void when_test_data_is_provided_for_a_step_a_single_test_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenarioWithTestSpecificData.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportContents.size(), is(1));
    }

    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = new ThucydidesRunner(ScenarioWithTestSpecificDataAndAFailingTestSample.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(3));

    }

    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_not_be_skipped() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = new ThucydidesRunner(ScenarioWithTestSpecificDataAndAFailingTestSample.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(3));
        assertThat(dataDrivenSteps.get(1).getResult(), is(TestResult.FAILURE));
        assertThat(dataDrivenSteps.get(2).getResult(), is(TestResult.SUCCESS));

    }

    @Ignore("Come back to this one")
    @Test
    public void when_a_step_fails_with_an_error_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = new ThucydidesRunner(ScenarioWithTestSpecificDataAndABreakingTestSample.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(3));
        assertThat(dataDrivenSteps.get(1).getResult(), is(TestResult.FAILURE));
        assertThat(dataDrivenSteps.get(2).getResult(), is(TestResult.SUCCESS));
    }

    @RunWith(ThucydidesRunner.class)
    public static class ScenarioWithTestSpecificData {

        @Managed
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step();
        }
    }


    @RunWith(ThucydidesRunner.class)
    public static class ScenarioWithTestSpecificDataAndAFailingTestSample {

        @Managed
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step_that_fails();
        }
    }

    @RunWith(ThucydidesRunner.class)
    public static class ScenarioWithTestSpecificDataAndABreakingTestSample {

        @Managed
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step_that_breaks();
        }
    }

    @Test
    public void when_test_data_is_provided_for_a_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = new ThucydidesRunner(ScenarioWithTestSpecificData.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(3));

    }

    @Test
    public void test_step_data_should_appear_in_the_step_titles() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = new ThucydidesRunner(ScenarioWithTestSpecificData.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        TestOutcome testOutcome1 = executedSteps.get(0);
        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();

        TestStep step1 = dataDrivenSteps.get(0);
        TestStep setNameStep1 = step1.getFlattenedSteps().get(0);
        TestStep step2 = dataDrivenSteps.get(1);
        TestStep setNameStep2 = step2.getFlattenedSteps().get(0);

        assertThat(setNameStep1.getDescription(), containsString("Joe Smith"));
        assertThat(setNameStep2.getDescription(), containsString("Jack Black"));


    }

    @Test
    @Ignore
    // TODO: work out why this test fails intermediatesly
    public void when_the_Concurrent_annotation_is_used_tests_should_be_run_in_parallel() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        System.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleParallelDataDrivenScenario.class,
                                                                                 webDriverFactory);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        Thread.currentThread().sleep(1000);

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));

        assertThat(reportContents, hasItem(containsString("Happy day scenario [a/1]")));
        assertThat(reportContents, hasItem(containsString("Happy day scenario [b/2]")));
        assertThat(reportContents, hasItem(containsString("Happy day scenario [c/3]")));

    }

    @Test
    public void the_Concurrent_annotation_indicates_that_tests_should_be_run_in_parallel() throws Throwable  {

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleParallelDataDrivenScenario.class,
                                                                                 webDriverFactory);

        assertThat(runner.runTestsInParallelFor(SampleParallelDataDrivenScenario.class), is(true));
        assertThat(runner.runTestsInParallelFor(SampleDataDrivenScenario.class), is(false));
    }

    @Test
    public void by_default_the_number_of_threads_is_2_times_the_number_of_CPU_cores() throws Throwable  {

        ThucydidesParameterizedRunner runner = new ThucydidesParameterizedRunner(SampleParallelDataDrivenScenario.class,
                                                                                 webDriverFactory);
        int threadCount = runner.getThreadCountFor(SampleParallelDataDrivenScenario.class);

        int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

        assertThat(threadCount, is(AVAILABLE_PROCESSORS * 2));

    }

    @RunWith(ThucydidesParameterizedRunner.class)
    @Concurrent(threads = "7")
    public static final class ParallelDataDrivenScenarioWithSpecifiedThreadCountSample {
        @TestData
        public static Collection testData() {
                return Arrays.asList(new Object[][]{ });
            }
    }

    @Test
    public void the_number_of_threads_can_be_overridden_in_the_concurrent_annotation() throws Throwable  {

        ThucydidesParameterizedRunner runner
                   = new ThucydidesParameterizedRunner(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class,
                                                       webDriverFactory);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);

        assertThat(threadCount, is(7));

    }

    @RunWith(ThucydidesParameterizedRunner.class)
    @Concurrent(threads = "7x")
    public static final class ParallelDataDrivenScenarioWithRelativeThreadCountSample {
        @TestData
        public static Collection testData() {
                return Arrays.asList(new Object[][]{ });
            }
    }

    @Test
    public void the_number_of_threads_can_be_overridden_in_the_concurrent_annotation_using_a_relative_value() throws Throwable  {

        ThucydidesParameterizedRunner runner
                   = new ThucydidesParameterizedRunner(ParallelDataDrivenScenarioWithRelativeThreadCountSample.class,
                                                       webDriverFactory);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithRelativeThreadCountSample.class);

        int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

        assertThat(threadCount, is(7 * AVAILABLE_PROCESSORS));

    }

    @RunWith(ThucydidesParameterizedRunner.class)
    @Concurrent(threads = "xxx")
    public static final class ParallelDataDrivenScenarioWithInvalidThreadCountSample {
        @TestData
        public static Collection testData() {
                return Arrays.asList(new Object[][]{ });
            }
    }
    @Test(expected = IllegalArgumentException.class)
    public void if_the_thread_count_is_invalid_an_exception_should_be_thrown() throws Throwable  {

        ThucydidesParameterizedRunner runner
                   = new ThucydidesParameterizedRunner(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class,
                                                       webDriverFactory);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);

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
        assertThat(reports.length, is(10));
    }

    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".html") && !filename.endsWith("screenshots.html");
        }
    }

    private class XMLFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".xml");
        }
    }

}
