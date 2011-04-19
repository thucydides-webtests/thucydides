package net.thucydides.junit.runners;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.UserStory;
import net.thucydides.junit.annotations.InvalidManagedPagesFieldException;
import net.thucydides.junit.annotations.InvalidStepsFieldException;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.steps.ScenarioStepListener;
import net.thucydides.samples.AnnotatedSingleTestScenario;
import net.thucydides.samples.SamplePassingScenario;
import net.thucydides.samples.SampleScenarioWithoutPages;
import net.thucydides.samples.SampleScenarioWithoutSteps;
import net.thucydides.samples.SingleTestScenario;
import net.thucydides.samples.SuccessfulSingleTestScenario;
import net.thucydides.samples.TestScenarioWithGroups;
import net.thucydides.samples.TestScenarioWithParameterizedSteps;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class WhenRunningATestScenario extends AbstractTestStepRunnerTest {


    TestableWebDriverFactory webDriverFactory;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void initMocks() {
        File temporaryDirectory = tempFolder.newFolder("screenshots");
        webDriverFactory = new TestableWebDriverFactory(temporaryDirectory);
    }

    @After
    public void resetSystemProperties() {
        System.setProperty("thucycides.step.delay", "");
    }

    @Test    
    public void the_steps_maintain_a_browser_open_across_the_execution_of_all_the_steps() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        assertThat(webDriverFactory.fireFoxOpenedCount(), is(1));
    }
    
    
    @Test    
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedSteps = runner.getAcceptanceTestRuns();
        assertThat(executedSteps.size(), greaterThan(0));
    }
    
    @Test
    public void the_test_runner_skips_any_tests_after_a_failure() throws Exception  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runExpectingFailure(runner);
        
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
    public void when_a_test_fails_the_message_is_recorded_in_the_test_step() throws Exception  {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runExpectingFailure(runner);

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getErrorMessage(), allOf(containsString("Expected: is <2>"), containsString("got: <1>")));
    }

    @Test
    public void when_a_test_fails_the_exception_is_recorded_in_the_test_step() throws Exception  {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);

        runExpectingFailure(runner);

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep failingStep = (ConcreteTestStep) steps.get(4);
        assertThat(failingStep.getException(), is(AssertionError.class));
    }

    private void runExpectingFailure(ThucydidesRunner runner) {
        boolean assertThrown = false;
        try {
            runner.run(new RunNotifier());
        } catch(AssertionError e) {
           assertThrown = true;
        }
        assertThat(assertThrown, is(true));
    }
    
    @Test(expected=AssertionError.class)
    public void the_test_runner_should_throw_an_exception_at_the_end_of_the_test_if_a_step_fails() throws Exception  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
    }
    
    @Test 
    public void the_test_runner_initializes_the_steps_object() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        
    }
    
    @Test    
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), greaterThan(0));
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        
        assertThat(testRun.getTitle(), is("Happy day scenario"));
    }

    @Test    
    public void the_test_runner_records_each_step_of_the_test_scenario() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), greaterThan(0));
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        assertThat(testRun.getTestSteps().size(), is(4));
    }
    
    @Test    
    public void the_test_runner_distinguishes_between_ignored_skipped_and_pending_steps() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runExpectingFailure(runner);
        
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
    public void the_test_runner_executes_steps_with_parameters() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runExpectingFailure(runner);
        
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
    public void the_test_runner_should_store_screenshots_only_for_successful_and_failed_tests() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runExpectingFailure(runner);
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        assertThat(steps.size(), is(6));
        assertThat(((ConcreteTestStep)steps.get(0)).getScreenshot(), is(notNullValue()));
        assertThat(((ConcreteTestStep)steps.get(1)).getScreenshot(), is(nullValue()));
        assertThat(((ConcreteTestStep)steps.get(2)).getScreenshot(), is(nullValue()));
        assertThat(((ConcreteTestStep)steps.get(3)).getScreenshot(), is(notNullValue()));
        assertThat(((ConcreteTestStep)steps.get(4)).getScreenshot(), is(notNullValue()));
        assertThat(((ConcreteTestStep)steps.get(5)).getScreenshot(), is(nullValue()));
        
    }
    
    @Test    
    public void the_test_runner_executes_tests_in_groups() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithGroups.class);
        runner.setWebDriverFactory(webDriverFactory);
        runExpectingFailure(runner);
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), is(1));
        List<TestStep> testSteps = executedScenarios.get(0).getTestSteps();
        assertThat(testSteps.size(), is(3));
    }

    @Test    
    public void the_test_runner_records_an_acceptance_test_result_for_each_test() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), is(3));
    }

    @Test    
    public void the_test_runner_derives_the_user_story_from_the_test_case_class() throws InitializationError  {
       
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
    public void the_test_runner_records_each_step_with_a_nice_name() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);
        
        assertThat(firstStep.getDescription(), is("Step that succeeds"));
    }

    @Test    
    public void default_test_names_can_be_overriden_in_the_Test_annotation() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep pendingStep = testRun.getTestSteps().get(2);
        
        assertThat(pendingStep.getDescription(), is("A pending step"));
    }

    @Test    
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_parameters() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);
        
        assertThat(firstStep.getDescription(), is("Step with a parameter: <span class='single-parameter'>foo</span>"));
    }
    
    @Test    
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_multiple_parameters() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep secondStep = testRun.getTestSteps().get(1);
        
        assertThat(secondStep.getDescription(), is("Step with two parameters: <span class='parameters'>foo, 2</span>"));
    }

    @Test    
    public void step_titles_can_be_overridden_with_the_StepDescription_annotation() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);
        
        assertThat(firstStep.getDescription(), is("A step that succeeds indeed!"));
    }

    @Test    
    public void scenario_titles_can_be_overridden_with_the_Title_annotation() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        
        assertThat(testRun.getTitle(), is("Oh happy days!"));
    }

    @Test
    public void test_runner_takes_a_screenshot_after_each_step() throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        TakesScreenshot driver = (TakesScreenshot) webDriverFactory.getDriver();
        
        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void the_user_can_slow_down_the_execution_of_the_test_steps_using_an_external_parameter() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.initWebdriverManager();

        ScenarioStepListener stepListener = runner.getStepListener();
        ScenarioStepListener spy = spy(stepListener);

        runner.setStepListener(spy);

        System.setProperty("thucycides.step.delay", "250");

        runner.run(new RunNotifier());

        verify(spy, times(2)).pauseTestRun(250);
    }

    @Test
    public void by_default_the_tests_are_not_slowed_down() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.initWebdriverManager();

        ScenarioStepListener stepListener = runner.getStepListener();
        ScenarioStepListener spy = spy(stepListener);

        runner.setStepListener(spy);

        runner.run(new RunNotifier());

        verify(spy, never()).pauseTestRun(anyInt());
    }

    @Test(expected=InvalidStepsFieldException.class)
    public void the_test_scenario_must_have_a_steps_field() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutSteps.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
    }

    @Test(expected=InvalidManagedPagesFieldException.class)
    public void the_test_scenario_must_have_a_pages_field() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SampleScenarioWithoutPages.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
    }
    
}
