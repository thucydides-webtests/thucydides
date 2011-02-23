package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.List;

import javax.swing.border.EmptyBorder;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.junit.annotations.InvalidManagedPagesFieldException;
import net.thucydides.junit.annotations.InvalidStepsFieldException;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.samples.AnnotatedSingleTestScenario;
import net.thucydides.junit.samples.SamplePassingScenario;
import net.thucydides.junit.samples.SampleScenarioWithoutPages;
import net.thucydides.junit.samples.SampleScenarioWithoutSteps;
import net.thucydides.junit.samples.SingleTestScenario;

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
    public void the_test_runner_initializes_the_steps_object() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        
    }
    
    @Test    
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), greaterThan(0));
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        
        assertThat(testRun.getTitle(), is("Happy day scenario"));
    }

    @Test    
    public void the_test_runner_records_each_step_of_the_test_scenario() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        assertThat(executedScenarios.size(), greaterThan(0));
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        assertThat(testRun.getTestSteps().size(), is(6));
    }
    
    @Test    
    public void the_test_runner_distinguishes_between_ignored_skipped_and_pending_steps() throws InitializationError  {
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
    public void the_test_runner_should_store_screenshots_only_for_successful_and_failed_tests() throws InitializationError  {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        AcceptanceTestRun testRun = executedScenarios.get(0);

        List<TestStep> steps = testRun.getTestSteps();
        assertThat(steps.size(), is(6));
        assertThat(steps.get(0).getScreenshot(), is(notNullValue()));
        assertThat(steps.get(1).getScreenshot(), is(nullValue()));
        assertThat(steps.get(2).getScreenshot(), is(nullValue()));
        assertThat(steps.get(3).getScreenshot(), is(notNullValue()));
        assertThat(steps.get(4).getScreenshot(), is(notNullValue()));
        assertThat(steps.get(5).getScreenshot(), is(nullValue()));
        
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
    public void the_test_runner_records_each_step_with_a_nice_name() throws InitializationError  {
       
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();
        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        TestStep firstStep = testRun.getTestSteps().get(0);
        
        assertThat(firstStep.getDescription(), is("Step that succeeds."));
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
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());
        
        TakesScreenshot driver = (TakesScreenshot) webDriverFactory.getDriver();
        
        verify(driver, times(3)).getScreenshotAs((OutputType<?>) anyObject());
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
