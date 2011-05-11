package net.thucydides.easyb;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;


import static net.thucydides.core.hamcrest.Matchers.containsInOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when
import net.thucydides.easyb.samples.SampleSteps
import net.thucydides.easyb.samples.NestedScenarioSteps;
import static net.thucydides.core.model.TestResult.*
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Matchers.anyObject;

/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingEasybStepExecutionResults {

    StepListener stepListener;

    StepFactory stepFactory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File outputDirectory;

    File screenshot;

    @Mock
    FirefoxDriver driver;

    @Mock
    Pages pages;


    @Before
    public void createStepListenerAndFactory() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputDirectory = temporaryFolder.newFolder("thucydides");
        screenshot = temporaryFolder.newFile("screenshot.jpg");
        stepListener = new BaseStepListener(driver, outputDirectory);

        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);
    }

    @Test
    public void to_use_a_step_listener_you_need_to_instantiate_a_step_library_using_the_step_factory() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();

        assert results.size() == 1
    }

    @Test
    public void the_step_listener_should_record_each_step_called() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.stepCount == 2
    }


    @Test
    public void screenshots_should_be_taken_after_each_step() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.result == SUCCESS
    }

    @Test
    public void a_failing_step_should_record_the_failure() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.failingStep();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.result == FAILURE
    }

    @Test
    public void a_failing_step_should_record_the_failure_details_with_the_step() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.failingStep();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.testSteps[1].result == FAILURE
    }

    @Test
    public void ignored_tests_should_be_reported() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.ignoredStep();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.testSteps[1].result == IGNORED
    }

    @Test
    public void steps_should_be_skipped_after_a_failure() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.failingStep();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.testSteps[2].result == SKIPPED
    }

    @Test
    public void steps_should_not_be_skipped_after_an_ignored_test() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.ignoredStep()
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = results.get(0);
        assert testRun.testSteps[2].result == SUCCESS
    }
    @Test
    public void the_step_listener_should_record_each_step_executed_in_order() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        List<String> executedStepNames = namesFrom(executedSteps);

        assertThat(executedStepNames, containsInOrder("step1", "step2"));

    }

    @Test
    public void any_nested_steps_should_also_be_executed() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();

        assert results.size() == 1

    }

    @Test
    public void steps_with_nested_steps_should_be_recorded_as_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        assertThat(executedSteps.get(0), instanceOf(TestStepGroup.class));
        assertThat(executedSteps.get(1), instanceOf(TestStepGroup.class));
    }


    @Test
    public void nested_steps_should_be_recorded_inside_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assert results.size() == 1

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();

        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("step1", "step2", "step3"));
    }

    @Test
    public void screenshots_should_be_taken_after_nested_steps() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        verify(driver, times(7)).getScreenshotAs((OutputType<?>) anyObject());
    }

    private AcceptanceTestRun firstTestResultRecordedIn(List<AcceptanceTestRun> testRunResults) {
        return stepListener.getTestRunResults().get(0);
    }

    private List<String> namesFrom(List<TestStep> testSteps) {
        List<String> descriptions = new ArrayList<String>();
        for (TestStep step : testSteps) {
            descriptions.add(step.getDescription());
        }
        return descriptions;
    }


}
