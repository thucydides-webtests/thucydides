package net.thucydides.junit.runners;

import net.thucydides.core.annotations.InvalidStepsFieldException;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.steps.InvalidManagedPagesFieldException;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.samples.AnnotatedSingleTestScenario;
import net.thucydides.samples.SampleNoSuchElementExceptionScenario;
import net.thucydides.samples.SamplePassingScenario;
import net.thucydides.samples.SampleScenarioWithoutPages;
import net.thucydides.samples.SampleScenarioWithoutSteps;
import net.thucydides.samples.SingleTestScenario;
import net.thucydides.samples.SingleTestScenarioWithWebdriverException;
import net.thucydides.samples.SuccessfulSingleTestScenario;
import net.thucydides.samples.TestIgnoredScenario;
import net.thucydides.samples.TestScenarioWithGroups;
import net.thucydides.samples.TestScenarioWithParameterizedSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.NoSuchElementException;
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
import static org.mockito.Mockito.when;

public class WhenRunningATestScenario extends AbstractTestStepRunnerTest {


    TestableWebDriverFactory webDriverFactory;

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

        webDriverFactory = new TestableWebDriverFactory(temporaryDirectory);
        when(webdriverManager.getWebdriver()).thenReturn(mockWebDriver);

    }

    @After
    public void resetSystemProperties() {
        System.setProperty("thucycides.step.delay", "");
        WebdriverProxyFactory.clearMockDriver();
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
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedSteps = runner.getAcceptanceTestRuns();
        assertThat(executedSteps.size(), is(3));
        AcceptanceTestRun testRun1 = executedSteps.get(0);
        AcceptanceTestRun testRun2 = executedSteps.get(1);
        AcceptanceTestRun testRun3 = executedSteps.get(2);

        UserStory userStory = testRun1.getUserStory();

        assertThat(userStory.getName(), is("Sample passing scenario"));

        assertThat(testRun1.getTitle(), is("Happy day scenario"));
        assertThat(testRun1.getMethodName(), is("happy_day_scenario"));
        assertThat(testRun1.getTestSteps().size(), is(4));

        assertThat(testRun1.getUserStory(), is(userStory));
        assertThat(testRun2.getTitle(), is("Edge case 1"));
        assertThat(testRun2.getMethodName(), is("edge_case_1"));
        assertThat(testRun2.getTestSteps().size(), is(3));

        assertThat(testRun3.getUserStory(), is(userStory));
        assertThat(testRun3.getTitle(), is("Edge case 2"));
        assertThat(testRun3.getMethodName(), is("edge_case_2"));
        assertThat(testRun3.getTestSteps().size(), is(2));
    }

    @Test
    public void the_test_runner_skips_any_tests_after_a_failure() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
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
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0).isIgnored(), is(true));
    }



    @Test
    public void the_test_runner_skips_any_tests_after_a_webdriver_error() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SampleNoSuchElementExceptionScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
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
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getErrorMessage(), allOf(containsString("Expected: is <2>"), containsString("got: <1>")));
    }

    @Test
    public void when_a_test_fails_the_exception_is_recorded_in_the_test_step() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getException(), is(AssertionError.class));
    }

    @Test
    public void when_a_test_throws_a_webdriver_exception_it_is_recorded_in_the_test_step() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenarioWithWebdriverException.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getException(), is(NoSuchElementException.class));
    }

    @Test
    public void the_test_runner_should_notify_test_failures() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        RunNotifier notifier = mock(RunNotifier.class);
        runner.run(notifier);

        verify(notifier).fireTestFailure((Failure)anyObject());
    }

    @Test
    public void the_test_runner_initializes_the_steps_object() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());


    }

    @Test
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), greaterThan(0));

        AcceptanceTestRun testRun = executedScenarios.get(0);

        assertThat(testRun.getTitle(), is("Happy day scenario"));
    }

    @Test
    public void the_test_runner_records_each_step_of_the_test_scenario() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), is(3));

        AcceptanceTestRun testRun = executedScenarios.get(0);
        assertThat(testRun.getTestSteps().size(), is(4));

        AcceptanceTestRun testRun2 = executedScenarios.get(1);
        assertThat(testRun2.getTestSteps().size(), is(3));

        AcceptanceTestRun testRun3 = executedScenarios.get(2);
        assertThat(testRun3.getTestSteps().size(), is(2));
    }

    @Test
    public void the_test_runner_distinguishes_between_ignored_skipped_and_pending_steps() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), is(1));
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep ignored = testRun.getTestSteps().get(1);
        TestStep pending = testRun.getTestSteps().get(2);
        TestStep skipped = testRun.getTestSteps().get(5);

        assertThat(ignored.getResult(), is(TestResult.IGNORED));
        assertThat(pending.getResult(), is(TestResult.PENDING));
        assertThat(skipped.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void the_test_runner_executes_steps_with_parameters() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), greaterThan(0));
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep ignored = testRun.getTestSteps().get(1);
        TestStep pending = testRun.getTestSteps().get(2);
        TestStep skipped = testRun.getTestSteps().get(5);

        assertThat(ignored.getResult(), is(TestResult.IGNORED));
        assertThat(pending.getResult(), is(TestResult.PENDING));
        assertThat(skipped.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void the_test_runner_should_store_screenshots_only_for_successful_and_failed_tests() throws InitializationError {

        WebdriverProxyFactory.useMockDriver(mockWebDriver);

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        assertThat(steps.size(), is(6));
        verify(mockWebDriver, times(3)).getScreenshotAs(OutputType.FILE);

    }

    @Test
    public void the_test_runner_executes_tests_in_groups() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithGroups.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), is(1));
        List<TestStep> testSteps = executedScenarios.get(0).getTestSteps();
        assertThat(testSteps.size(), is(3));
    }

    @Test
    public void the_test_runner_records_an_acceptance_test_result_for_each_test() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), is(3));
    }

    @Test
    public void the_test_runner_derives_the_user_story_from_the_test_case_class() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);
        UserStory userStory = testRun.getUserStory();

        assertThat(userStory.getName(), is("Successful single test scenario"));
        assertThat(userStory.getSource(), is("net.thucydides.samples.SuccessfulSingleTestScenario"));
        assertThat(userStory.getCode(), is("US01"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("Step that succeeds"));
    }

    @Test
    public void default_test_names_can_be_overriden_in_the_Test_annotation() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep pendingStep = testRun.getTestSteps().get(2);

        assertThat(pendingStep.getDescription(), is("A pending step"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_parameters() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("Step with a parameter: <span class='single-parameter'>foo</span>"));
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_multiple_parameters() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep secondStep = testRun.getTestSteps().get(1);

        assertThat(secondStep.getDescription(), is("Step with two parameters: <span class='parameters'>foo, 2</span>"));
    }

    @Test
    public void step_titles_can_be_overridden_with_the_StepDescription_annotation() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), is("A step that succeeds indeed!"));
    }

    @Test
    public void scenario_titles_can_be_overridden_with_the_Title_annotation() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();

        AcceptanceTestRun testRun = executedScenarios.get(0);

        assertThat(testRun.getTitle(), is("Oh happy days!"));
    }

    @Test
    public void test_runner_takes_a_screenshot_after_each_step() throws InitializationError {

        WebdriverProxyFactory.useMockDriver(mockWebDriver);

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        verify(mockWebDriver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test(expected = InvalidStepsFieldException.class)
    public void the_test_scenario_must_have_a_steps_field() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutSteps.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
    }

    @Test(expected = InvalidManagedPagesFieldException.class)
    public void the_test_scenario_must_have_a_pages_field() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutPages.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
    }

}
