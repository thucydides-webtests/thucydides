package net.thucydides.junit.runners;

import net.thucydides.core.annotations.InvalidStepsFieldException;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.InvalidManagedPagesFieldException;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverAssertionError;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import net.thucydides.junit.integration.samples.OpenStaticDemoPageWithFailureSample;
import net.thucydides.samples.AnnotatedSingleTestScenario;
import net.thucydides.samples.SampleFailingScenarioWithEmptyTests;
import net.thucydides.samples.SampleNoSuchElementExceptionScenario;
import net.thucydides.samples.SamplePassingScenario;
import net.thucydides.samples.SamplePassingScenarioWithFieldsInParent;
import net.thucydides.samples.SamplePassingScenarioWithPrivateFields;
import net.thucydides.samples.SamplePassingScenarioWithEmptyTests;
import net.thucydides.samples.SamplePassingScenarioWithIgnoredTests;
import net.thucydides.samples.SamplePassingScenarioWithPendingTests;
import net.thucydides.samples.SampleScenarioWithoutPages;
import net.thucydides.samples.SampleScenarioWithoutStepAnnotations;
import net.thucydides.samples.SampleScenarioWithoutSteps;
import net.thucydides.samples.SingleTestScenario;
import net.thucydides.samples.SingleTestScenarioWithWebdriverException;
import net.thucydides.samples.SuccessfulSingleTestScenario;
import net.thucydides.samples.TestIgnoredScenario;
import net.thucydides.samples.TestScenarioWithGroups;
import net.thucydides.samples.TestScenarioWithParameterizedSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WhenRunningATestScenario extends AbstractTestStepRunnerTest {


    @Mock
    FirefoxDriver mockWebDriver;

    @Mock
    WebdriverManager webdriverManager;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        File temporaryDirectory = tempFolder.newFolder("screenshots");
    }

    @After
    public void resetSystemProperties() {
        System.setProperty("thucycides.step.delay", "");
        WebdriverProxyFactory.getFactory().clearMockDriver();
    }


    class TestableThucydidesRunner extends ThucydidesRunner {

        public TestableThucydidesRunner(final Class<?> klass) throws InitializationError {
            super(klass);
        }

        @Override
        protected WebdriverManager getWebdriverManager() {
            return webdriverManager;
        }
    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        Story userStory = testOutcome1.getUserStory();

        assertThat(userStory.getName(), is("Sample passing scenario"));

        assertThat(testOutcome1.getTitle(), is("Happy day scenario"));
        assertThat(testOutcome1.getMethodName(), is("happy_day_scenario"));
        assertThat(testOutcome1.getTestSteps().size(), is(4));

        assertThat(testOutcome1.getUserStory(), is(userStory));
        assertThat(testOutcome2.getTitle(), is("Edge case 1"));
        assertThat(testOutcome2.getMethodName(), is("edge_case_1"));
        assertThat(testOutcome2.getTestSteps().size(), is(3));

        assertThat(testOutcome3.getUserStory(), is(userStory));
        assertThat(testOutcome3.getTitle(), is("Edge case 2"));
        assertThat(testOutcome3.getMethodName(), is("edge_case_2"));
        assertThat(testOutcome3.getTestSteps().size(), is(2));
    }

    @Test
    public void private_annotated_fields_should_be_allowed() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenarioWithPrivateFields.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        Story userStory = testOutcome1.getUserStory();

        assertThat(userStory.getName(), is("Sample passing scenario with private fields"));

        assertThat(testOutcome1.getTitle(), is("Happy day scenario"));
        assertThat(testOutcome1.getMethodName(), is("happy_day_scenario"));
        assertThat(testOutcome1.getTestSteps().size(), is(4));

        assertThat(testOutcome1.getUserStory(), is(userStory));
        assertThat(testOutcome2.getTitle(), is("Edge case 1"));
        assertThat(testOutcome2.getMethodName(), is("edge_case_1"));
        assertThat(testOutcome2.getTestSteps().size(), is(3));

        assertThat(testOutcome3.getUserStory(), is(userStory));
        assertThat(testOutcome3.getTitle(), is("Edge case 2"));
        assertThat(testOutcome3.getMethodName(), is("edge_case_2"));
        assertThat(testOutcome3.getTestSteps().size(), is(2));
    }

    @Test
    public void annotated_fields_should_be_allowed_in_parent_classes() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenarioWithFieldsInParent.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        Story userStory = testOutcome1.getUserStory();

        assertThat(userStory.getName(), is("Sample passing scenario with fields in parent"));

        assertThat(testOutcome1.getTitle(), is("Happy day scenario"));
        assertThat(testOutcome1.getMethodName(), is("happy_day_scenario"));
        assertThat(testOutcome1.getTestSteps().size(), is(4));

        assertThat(testOutcome1.getUserStory(), is(userStory));
        assertThat(testOutcome2.getTitle(), is("Edge case 1"));
        assertThat(testOutcome2.getMethodName(), is("edge_case_1"));
        assertThat(testOutcome2.getTestSteps().size(), is(3));

        assertThat(testOutcome3.getUserStory(), is(userStory));
        assertThat(testOutcome3.getTitle(), is("Edge case 2"));
        assertThat(testOutcome3.getMethodName(), is("edge_case_2"));
        assertThat(testOutcome3.getTestSteps().size(), is(2));
    }
    @Test
    public void tests_marked_as_pending_should_be_skipped() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenarioWithPendingTests.class);
        runner.setWebDriverFactory(webDriverFactory);
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

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenarioWithIgnoredTests.class);
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

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenarioWithEmptyTests.class);
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
    public void tests_should_be_run_even_after_a_webdriver_error() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(OpenStaticDemoPageWithFailureSample.class);
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

        ThucydidesRunner runner = new ThucydidesRunner(SampleFailingScenarioWithEmptyTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        assertThat(executedSteps.get(0).getStepCount(), is(1));
        assertThat(executedSteps.get(0).getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void the_test_runner_skips_any_tests_after_a_failure() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);

        runner.run(new RunNotifier());
        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.size(), is(6));
        assertThat(steps.get(0).isSuccessful(), is(true));
        assertThat(steps.get(1).isIgnored(), is(true));
        assertThat(steps.get(2).isPending(), is(true));
        assertThat(steps.get(3).isSuccessful(), is(true));
        assertThat(steps.get(4).isFailure(), is(true));
        assertThat(steps.get(5).isSkipped(), is(true));
    }

    @Test
    public void the_test_runner_skips_any_ignored_tests() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(TestIgnoredScenario.class);

        runner.run(new RunNotifier());
        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0).isIgnored(), is(true));
    }



    @Test
    public void the_test_runner_skips_any_tests_after_a_webdriver_error() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SampleNoSuchElementExceptionScenario.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.size(), is(6));
        assertThat(steps.get(0).isSuccessful(), is(true));
        assertThat(steps.get(1).isIgnored(), is(true));
        assertThat(steps.get(2).isPending(), is(true));
        assertThat(steps.get(3).isSuccessful(), is(true));
        assertThat(steps.get(4).isFailure(), is(true));
        assertThat(steps.get(5).isSkipped(), is(true));
    }


    @Test
    public void when_a_test_fails_the_message_is_recorded_in_the_test_step() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getErrorMessage(), allOf(containsString("Expected: is <2>"), containsString("got: <1>")));
    }

    @Test
    public void when_a_test_fails_the_exception_is_recorded_in_the_test_step() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getException(), is(AssertionError.class));
    }

    @Test
    public void when_a_test_throws_a_webdriver_exception_it_is_recorded_in_the_test_step() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenarioWithWebdriverException.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getException(), is(WebdriverAssertionError.class));
    }

    @Test
    public void the_test_runner_should_notify_test_failures() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        RunNotifier notifier = mock(RunNotifier.class);
        runner.run(notifier);

        verify(notifier).fireTestFailure((Failure)anyObject());
    }

    @Test
    public void the_test_runner_initializes_the_steps_object() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.run(new RunNotifier());


    }

    @Test
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), greaterThan(0));

        TestOutcome testOutcome = executedScenarios.get(0);

        assertThat(testOutcome.getTitle(), is("Happy day scenario"));
    }

    @Test
    public void the_test_runner_records_each_step_of_the_test_scenario() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
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
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(1));
        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep ignored = testOutcome.getTestSteps().get(1);
        TestStep pending = testOutcome.getTestSteps().get(2);
        TestStep skipped = testOutcome.getTestSteps().get(5);

        assertThat(ignored.getResult(), is(TestResult.IGNORED));
        assertThat(pending.getResult(), is(TestResult.PENDING));
        assertThat(skipped.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void the_test_runner_executes_steps_with_parameters() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), greaterThan(0));
        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep ignored = testOutcome.getTestSteps().get(1);
        TestStep pending = testOutcome.getTestSteps().get(2);
        TestStep skipped = testOutcome.getTestSteps().get(5);

        assertThat(ignored.getResult(), is(TestResult.IGNORED));
        assertThat(pending.getResult(), is(TestResult.PENDING));
        assertThat(skipped.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void the_test_runner_should_store_screenshots_only_for_successful_and_failed_tests() throws InitializationError {

        WebdriverProxyFactory.getFactory().useMockDriver(mockWebDriver);

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.size(), is(6));
        verify(mockWebDriver, times(3)).getScreenshotAs(OutputType.FILE);

    }

    @Test
    public void the_test_runner_executes_tests_in_groups() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithGroups.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(1));
        List<TestStep> testSteps = executedScenarios.get(0).getTestSteps();
        assertThat(testSteps.size(), is(3));
    }

    @Test
    public void the_test_runner_records_an_acceptance_test_result_for_each_test() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(3));
    }

    @Test
    public void the_test_runner_derives_the_user_story_from_the_test_case_class() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        Story userStory = testOutcome.getUserStory();

        assertThat(userStory.getName(), is("Successful single test scenario"));
        // TODO: Fix
        //assertThat(userStory.getSource(), is("net.thucydides.samples.SuccessfulSingleTestScenario"));
        //assertThat(userStory.getCode(), is("US01"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep firstStep = testOutcome.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("Step that succeeds"));
    }

    @Test
    public void default_test_names_can_be_overriden_in_the_Test_annotation() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep pendingStep = testOutcome.getTestSteps().get(2);

        assertThat(pendingStep.getDescription(), is("A pending step"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_parameters() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep firstStep = testOutcome.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("Step with a parameter: <span class='single-parameter'>foo</span>"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_multiple_parameters() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep secondStep = testOutcome.getTestSteps().get(1);

        assertThat(secondStep.getDescription(), is("Step with two parameters: <span class='parameters'>foo, 2</span>"));
    }

    @Test
    public void step_titles_can_be_overridden_with_the_StepDescription_annotation() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);
        TestStep firstStep = testOutcome.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("A step that succeeds indeed!"));
    }

    @Test
    public void scenario_titles_can_be_overridden_with_the_Title_annotation() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = executedScenarios.get(0);

        assertThat(testOutcome.getTitle(), is("Oh happy days!"));
    }

    @Test
    public void test_runner_takes_a_screenshot_after_each_step() throws InitializationError {

        WebdriverProxyFactory.getFactory().useMockDriver(mockWebDriver);

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        verify(mockWebDriver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_test_scenario_does_not_need_a_steps_field() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutStepAnnotations.class);
        runner.run(new RunNotifier());
    }

    @Test
    public void the_test_scenario_does_not_need_steps() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutSteps.class);
        runner.run(new RunNotifier());
    }


    @Test(expected = InvalidManagedPagesFieldException.class)
    public void the_test_scenario_must_have_a_pages_field() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutPages.class);
        runner.run(new RunNotifier());
    }

    @Mock
    WebDriverFactory webDriverFactory;

    @Test
    public void the_manager_should_ignore_close_if_the_webdriver_if_not_defined() {
        WebdriverManager manager = new WebdriverManager(webDriverFactory);

        manager.closeDriver();
    }

}
