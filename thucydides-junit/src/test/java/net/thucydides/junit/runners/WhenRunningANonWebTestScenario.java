package net.thucydides.junit.runners;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.samples.NonWebTestScenarioWithParameterizedSteps;
import net.thucydides.samples.SampleNonWebScenarioWithError;
import net.thucydides.samples.SamplePassingNonWebScenario;
import net.thucydides.samples.SamplePassingNonWebScenarioWithEmptyTests;
import net.thucydides.samples.SamplePassingNonWebScenarioWithIgnoredTests;
import net.thucydides.samples.SamplePassingNonWebScenarioWithPendingTests;
import net.thucydides.samples.SingleNonWebTestScenario;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.junit.util.FileFormating.md5;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WhenRunningANonWebTestScenario extends AbstractTestStepRunnerTest {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    EnvironmentVariables environmentVariables;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

    Injector injector;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector = Guice.createInjector(new ThucydidesModule());
        StepEventBus.getEventBus().clear();
    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingNonWebScenario.class, injector);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        assertThat(testOutcome1.getTitle(), is("Happy day scenario"));
        assertThat(testOutcome1.getMethodName(), is("happy_day_scenario"));
        assertThat(testOutcome1.getTestSteps().size(), is(4));

        assertThat(testOutcome2.getTitle(), is("Edge case 1"));
        assertThat(testOutcome2.getMethodName(), is("edge_case_1"));
        assertThat(testOutcome2.getTestSteps().size(), is(3));

        assertThat(testOutcome3.getTitle(), is("Edge case 2"));
        assertThat(testOutcome3.getMethodName(), is("edge_case_2"));
        assertThat(testOutcome3.getTestSteps().size(), is(2));
    }


    @Test
    public void tests_marked_as_pending_should_be_pending() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingNonWebScenarioWithPendingTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        assertThat(testOutcome1.getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome2.getResult(), is(TestResult.PENDING));
        assertThat(testOutcome3.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void tests_marked_as_ignored_should_be_skipped() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingNonWebScenarioWithIgnoredTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        assertThat(testOutcome1.getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome2.getResult(), is(TestResult.IGNORED));
        assertThat(testOutcome3.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void tests_with_no_steps_should_be_marked_as_pending() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingNonWebScenarioWithEmptyTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        assertThat(testOutcome1.getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome2.getResult(), is(TestResult.PENDING));
        assertThat(testOutcome3.getResult(), is(TestResult.PENDING));
    }


    @Test
    public void tests_should_be_run_after_an_assertion_error() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SampleNonWebScenarioWithError.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        assertThat(testOutcome1.getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome2.getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome3.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void failing_tests_with_no_steps_should_still_record_the_error() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SampleNonWebScenarioWithError.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        assertThat(executedSteps.get(0).getResult(), is(TestResult.FAILURE));
        assertThat(executedSteps.get(0).getTestFailureCause().getMessage(), is("Oh bother!"));
    }

    @Test
    public void the_test_runner_skips_any_tests_after_a_failure() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleNonWebTestScenario.class);

        runner.run(new RunNotifier());
        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.size(), is(6));
        assertThat(steps.get(0).isSuccessful(), is(true));
        assertThat(steps.get(1).isIgnored(), is(true));
        assertThat(steps.get(2).isPending(), is(true));
        assertThat(steps.get(3).isIgnored(), is(true));
        assertThat(steps.get(4).isIgnored(), is(true));
        assertThat(steps.get(5).isIgnored(), is(true));
    }


    @Ignore("Come back to check this")
    @Test
    public void the_test_runner_should_notify_test_failures() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleNonWebTestScenario.class);
        RunNotifier notifier = mock(RunNotifier.class);
        runner.run(notifier);

        verify(notifier, atLeast(1)).fireTestFailure((Failure) anyObject());
    }

    @Test
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingNonWebScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), greaterThan(0));

        TestOutcome testOutcome = executedScenarios.get(0);

        assertThat(testOutcome.getTitle(), is("Happy day scenario"));
    }

    @Test
    public void the_test_runner_records_each_step_of_the_test_scenario() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingNonWebScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(3));

        TestOutcome testOutcome = executedScenarios.get(0);
        assertThat(testOutcome.getTestSteps().size(), is(4));

        TestOutcome testOutcome2 = executedScenarios.get(1);
        assertThat(testOutcome2.getTestSteps().size(), is(3));

        TestOutcome testOutcome3 = executedScenarios.get(2);
        assertThat(testOutcome3.getTestSteps().size(), is(2));
    }

    @Test
    public void the_test_runner_distinguishes_between_ignored_skipped_and_pending_steps() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SingleNonWebTestScenario.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(1));
        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep ignored = testOutcome.getTestSteps().get(1);
        TestStep pending = testOutcome.getTestSteps().get(2);
        TestStep skipped = testOutcome.getTestSteps().get(5);

        assertThat(ignored.getResult(), is(TestResult.IGNORED));
        assertThat(pending.getResult(), is(TestResult.PENDING));
    }


    @Test
    public void the_test_runner_should_not_store_screenshots_for_non_web_tests() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleNonWebTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.get(0).getScreenshots().size(), is(0));


    }


    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_parameters() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep firstStep = testOutcome.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("Step with a parameter: <span class='single-parameter'>foo</span>"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_multiple_parameters() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep secondStep = testOutcome.getTestSteps().get(1);

        assertThat(secondStep.getDescription(), is("Step with two parameters: <span class='parameters'>foo, 2</span>"));
    }


    class TestableThucydidesRunner extends ThucydidesRunner {

        private final File testOutputDirectory;

        public TestableThucydidesRunner(final Class<?> klass, File outputDirectory) throws InitializationError {
            super(klass);
            testOutputDirectory = outputDirectory;
        }

        public TestableThucydidesRunner(Class<?> klass,
                                        WebDriverFactory webDriverFactory,
                                        File outputDirectory) throws InitializationError {
            super(klass, webDriverFactory);
            testOutputDirectory = outputDirectory;
        }

        @Override
        public File getOutputDirectory() {
            return testOutputDirectory;
        }
    }

    @Test
    public void xml_test_results_are_written_to_the_output_directory() throws InitializationError {

        File outputDirectory = temporaryFolder.newFolder("output");

        ThucydidesRunner runner = new TestableThucydidesRunner(SamplePassingNonWebScenario.class,
                outputDirectory);
        runner.run(new RunNotifier());

        List<String> generatedXMLReports = Arrays.asList(outputDirectory.list(new XMLFileFilter()));
        assertThat(generatedXMLReports.size(), is(3));
        assertThat(generatedXMLReports, hasItems(md5("sample_passing_non_web_scenario_edge_case_1.xml"),
                md5("sample_passing_non_web_scenario_edge_case_2.xml"),
                md5("sample_passing_non_web_scenario_happy_day_scenario.xml")));


    }

    @Test
    public void html_test_results_are_written_to_the_output_directory() throws InitializationError {

        File outputDirectory = temporaryFolder.newFolder("output");

        ThucydidesRunner runner = new TestableThucydidesRunner(SamplePassingNonWebScenario.class,
                outputDirectory);
        runner.run(new RunNotifier());

        List<String> generatedHtmlReports = Arrays.asList(outputDirectory.list(new HTMLFileFilter()));
        assertThat(generatedHtmlReports.size(), is(3));
        assertThat(generatedHtmlReports, hasItems(md5("sample_passing_non_web_scenario_edge_case_1.html"),
                md5("sample_passing_non_web_scenario_edge_case_2.html"),
                md5("sample_passing_non_web_scenario_happy_day_scenario.html")));
    }

    private class XMLFileFilter implements FilenameFilter {
        public boolean accept(File file, String filename) {
            return filename.endsWith(".xml");
        }
    }

    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File file, String filename) {
            return filename.endsWith(".html");
        }
    }

}