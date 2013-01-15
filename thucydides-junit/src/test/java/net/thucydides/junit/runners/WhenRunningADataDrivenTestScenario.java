package net.thucydides.junit.runners;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.model.DataTableRow;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.samples.SampleCSVDataDrivenScenario;
import net.thucydides.samples.SampleDataDrivenScenario;
import net.thucydides.samples.SampleParallelDataDrivenScenario;
import net.thucydides.samples.SamplePassingScenarioWithTestSpecificData;
import net.thucydides.samples.SampleScenarioSteps;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static net.thucydides.core.steps.StepData.withTestDataFrom;
import static net.thucydides.junit.util.FileFormating.digest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class WhenRunningADataDrivenTestScenario {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void a_data_driven_test_driver_should_run_one_test_per_row_of_data() throws Throwable  {

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomesForAllParameterSets();

        assertThat(executedScenarios.size(), is(20));

        for (TestOutcome to : executedScenarios) {
            System.out.println("############ " + to.getMethodName());
            System.out.println("$$$$$$$$$$$$$$ " + to.getResult());
            for(DataTableRow row : to.getDataTable().getRows()) {
                System.out.println("@@@@@@@@@@@@@@ " + row.getValues() + " ---> " + row.getResult());
            }
        }
    }

    @Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes() throws Throwable  {

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = runner.aggregateTestOutcomesByTestMethods();

        assertThat(aggregatedScenarios.size(), is(2));
    }

    @Test
    public void a_data_driven_test_should_also_be_able_to_use_data_from_a_CSV_file() throws Throwable  {

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomesForAllParameterSets();

        assertThat(executedScenarios.size(), is(6));
    }


    @Test
    public void a_separate_xml_report_should_be_generated_for_each_scenario() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                                         outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new XMLFileFilter());
        assertThat(reports.length, is(2));
    }

    @Test
    public void a_separate_xml_report_should_be_generated_for_each_scenario_when_using_data_from_a_CSV_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new XMLFileFilter());
        assertThat(reports.length, is(2));
    }

    @Test
    public void xml_report_names_should_reflect_the_test_scenarios() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportFilenames, hasItem(digest("sample_data_driven_scenario_happy_day_scenario.xml")));
        assertThat(reportFilenames, hasItem(digest("sample_data_driven_scenario_not_so_happy_day_scenario.xml")));
    }

    @Test
    public void xml_report_names_should_reflect_the_test_scenarios_when_data_is_read_from_csv_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportFilenames, hasItem(digest("sample_c_s_v_data_driven_scenario_data_driven_test.xml")));
        assertThat(reportFilenames, hasItem(digest("sample_c_s_v_data_driven_scenario_another_data_driven_test.xml")));
    }

    @Test
    public void xml_report_contents_should_reflect_the_test_data() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportContents, hasItemContainsString("<value>a</value>"));
        assertThat(reportContents, hasItemContainsString("<value>1</value>"));
        assertThat(reportContents, hasItemContainsString("<value>B</value>"));
        assertThat(reportContents, hasItemContainsString("<value>2</value>"));
    }

    @Test
    public void xml_report_contents_should_reflect_the_test_data_from_the_csv_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));

        assertThat(reportContents, hasItemContainsString("Jack Black"));
        assertThat(reportContents, hasItemContainsString("Joe Smith"));
    }

    private Matcher<? super List<String>> hasItemContainsString(String expectedValue) {
        return new HasItemContainsString(expectedValue);
    }

    private static class HasItemContainsString extends TypeSafeMatcher<List<String>> {

        private final String expectedValue;

        private HasItemContainsString(String expectedValue) {
            this.expectedValue = expectedValue;
        }

        @Override
        protected boolean matchesSafely(List<String> values) {
            return !filter(containsString(expectedValue), values).isEmpty();
        }

        public void describeTo(Description description) {
            description.appendText("Expecting a list containing a string that contains ").appendValue(expectedValue);
        }
    }


    @Test
    public void when_test_data_is_provided_for_a_step_a_single_test_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                                         outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(SamplePassingScenarioWithTestSpecificData.class);

        runner.run(new RunNotifier());

        List reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));
        assertThat(reportContents.size(), is(1));
    }

    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(3));

    }

    @Test
    public void when_a_step_is_skipped_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

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
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

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
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndABreakingTestSample.class);

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

        @Managed(driver="htmlunit")
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

        @Managed(driver="htmlunit")
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
    public static class ScenarioWithTestSpecificDataAndASkippedTestSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step_that_is_skipped();
        }
    }

    @RunWith(ThucydidesRunner.class)
    public static class ScenarioWithTestSpecificDataAndABreakingTestSample {

        @Managed(driver="htmlunit")
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
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificData.class);
//        runner.setWebDriverFactory(webDriverFactory);

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
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificData.class);
//        runner.setWebDriverFactory(webDriverFactory);

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
    public void when_the_Concurrent_annotation_is_used_tests_should_be_run_in_parallel() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleParallelDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(outputDirectory.listFiles(new XMLFileFilter()));

        assertThat(reportContents, hasItemContainsString("<value>a</value>"));
        assertThat(reportContents, hasItemContainsString("<value>1</value>"));
        assertThat(reportContents, hasItemContainsString("<value>b</value>"));
        assertThat(reportContents, hasItemContainsString("<value>2</value>"));
        assertThat(reportContents, hasItemContainsString("<value>c</value>"));
        assertThat(reportContents, hasItemContainsString("<value>3</value>"));

    }

    @Test
    public void the_Concurrent_annotation_indicates_that_tests_should_be_run_in_parallel() throws Throwable  {

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleParallelDataDrivenScenario.class);

        assertThat(runner.runTestsInParallelFor(SampleParallelDataDrivenScenario.class), is(true));
        assertThat(runner.runTestsInParallelFor(SampleDataDrivenScenario.class), is(false));
    }

    @Test
    public void by_default_the_number_of_threads_is_2_times_the_number_of_CPU_cores() throws Throwable  {

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleParallelDataDrivenScenario.class);
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
                   = getTestRunnerUsing(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);
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
                   = getTestRunnerUsing(ParallelDataDrivenScenarioWithRelativeThreadCountSample.class);
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
                   = getTestRunnerUsing(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);
        runner.getThreadCountFor(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);

    }

    private List<String> filenamesOf(File[] files) {
        List filenames = new ArrayList<String>();
        for(File file : files) {
            filenames.add(file.getName());
        }
        return filenames;
    }


    private List<String> contentsOf(File[] files) throws IOException {
        List<String> contents = new ArrayList();
        for(File file : files) {
            contents.add(stringContentsOf(file));
        }
        return contents;
    }

    private String stringContentsOf(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

    @Test
    public void html_report_names_should_reflect_the_test_scenario() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        AcceptanceTestReporter reporter = mock(AcceptanceTestReporter.class);

        runner.run(new RunNotifier());

        List<String> reportFilenames = filenamesOf(outputDirectory.listFiles(new HTMLFileFilter()));
        assertThat(reportFilenames, allOf(hasItem(digest("sample_data_driven_scenario_happy_day_scenario.html")),
                hasItem(digest("sample_data_driven_scenario_not_so_happy_day_scenario.html"))));
    }


    @Test
    public void a_separate_html_report_should_be_generated_from_each_scenario() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName(),
                                         outputDirectory.getAbsolutePath());

        ThucydidesParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        runner.run(new RunNotifier());

        File[] reports = outputDirectory.listFiles(new HTMLFileFilter());
        assertThat(reports.length, is(2));
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

    protected ThucydidesRunner getNormalTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new ThucydidesRunner(testClass, factory, configuration);
    }
    
    protected ThucydidesParameterizedRunner getTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new ThucydidesParameterizedRunner(testClass, configuration, factory);
    }
}
