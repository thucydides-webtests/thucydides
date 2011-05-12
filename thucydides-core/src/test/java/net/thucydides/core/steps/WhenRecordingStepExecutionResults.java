package net.thucydides.core.steps;

import net.thucydides.core.model.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.samples.FlatScenarioSteps;
import net.thucydides.core.steps.samples.NestedScenarioSteps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;

import static net.thucydides.core.hamcrest.Matchers.containsInOrder;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingStepExecutionResults {

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
        stepListener.testRunStarted("Test Run");
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);
    }

    @Test
    public void to_use_a_step_listener_you_need_to_instantiate_a_step_library_using_the_step_factory() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();

        assertThat(results.size(), is(1));
    }

    @Test
    public void the_step_listener_should_record_each_step_called() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getStepCount(), is(2));
    }

    @Test
    public void the_step_listener_should_be_informed_of_the_story_name() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.testRunStarted(ExecutedStepDescription.withTitle("Test Run"));
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getTitle(), is("Test Run"));
    }

    @Test
    public void the_step_listener_records_the_test_method_name_if_available() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        ExecutedStepDescription stepDescription = ExecutedStepDescription.of(FlatScenarioSteps.class, "step1");

        stepListener = new BaseStepListener(driver, outputDirectory);
        stepListener.testRunStarted(stepDescription);

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getMethodName(), is("step1"));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_failing_step_should_record_the_failure() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.failingStep();


        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_failing_step_should_record_the_failure_details_with_the_step() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.failingStep();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testRun.getTestSteps().get(1).getException(), instanceOf(AssertionError.class));
        assertThat(testRun.getTestSteps().get(1).getErrorMessage(), is("Step failed"));
    }

    @Test
    public void ignored_tests_should_be_reported() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.ignoredStep();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void pending_tests_should_be_reported() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.pendingStep();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void pending_test_groups_should_be_reported() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("pending group");
        stepListener.updateCurrentStepStatus(TestResult.PENDING);

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void ignored_test_groups_should_be_skipped() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("ignored group");
        stepListener.stepIgnored(ExecutedStepDescription.withTitle("Ignore this step"));

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void succeeding_test_groups_should_be_marked_as_successful_by_default() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("successful group");
        stepListener.stepSucceeded();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.failingStep();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_not_be_skipped_after_an_ignored_test() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.ignoredStep();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);

        assertThat(testRun.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void the_step_listener_should_record_each_step_executed_in_order() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
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
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

    }

    @Test
    public void steps_with_nested_steps_should_be_recorded_as_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        assertThat(executedSteps.get(0), instanceOf(TestStepGroup.class));
        assertThat(executedSteps.get(1), instanceOf(TestStepGroup.class));
    }


    @Test
    public void starting_a_group_should_create_a_new_group_step() {
        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("Main group");
        steps.step1();
        steps.step2();
        stepListener.stepGroupFinished();

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getDescription(), is("Main group"));
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("step1", "step2"));
    }

    @Test
    public void starting_and_ending_a_group_without_steps_should_result_in_success() {
        ExecutedStepDescription group = ExecutedStepDescription.withTitle("Main group");
        group.setAGroup(true);

        stepListener.stepStarted(group);
        stepListener.updateCurrentStepStatus(TestResult.SUCCESS);
        stepListener.stepFinished(group);

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void starting_a_group_using_an_execution_step_object_should_create_a_new_group_step() {
        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        ExecutedStepDescription group = ExecutedStepDescription.withTitle("Main group");
        group.setAGroup(true);

        stepListener.stepGroupStarted(group);
        steps.step1();
        steps.step2();
        stepListener.stepGroupFinished();

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getDescription(), is("Main group"));
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("step1", "step2"));
    }

    @Test
    public void a_group_should_be_able_to_have_its_own_result() {
        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("Main group");
        stepListener.stepGroupFinished(TestResult.SUCCESS);

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_step_result_can_be_updated_after_the_step_execution() {
        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        stepListener.updateCurrentStepStatus(TestResult.SKIPPED);
        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        TestStep step2 = executedSteps.get(1);

        assertThat(step2.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void a_step_group_result_can_be_updated_after_the_step_execution() {
        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("New group");
        stepListener.updateCurrentStepStatus(TestResult.PENDING);

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        TestStep stepGroup = executedSteps.get(0);

        assertThat(stepGroup.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void nested_steps_should_be_recorded_inside_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();

        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("step1", "step2", "step3"));
    }

    @Test
    public void the_result_of_a_step_group_with_no_children_should_be_the_group_default_result() {
        TestStepGroup group = new TestStepGroup("Test Group");
        group.setDefaultResult(TestResult.SUCCESS);

        assertThat(group.getResult(), is(TestResult.SUCCESS));

    }

    @Test
    public void the_result_of_a_step_group_with_an_undefined_result_is_skipped() {
        TestStepGroup group = new TestStepGroup("Test Group");

        assertThat(group.getResult(), is(TestResult.SKIPPED));

    }

    @Test
    public void the_result_of_a_step_group_with_children_should_be_the_result_of_the_children() {
        TestStepGroup group = new TestStepGroup("Test Group");

        group.setDefaultResult(TestResult.SUCCESS);


        ConcreteTestStep testStep = new ConcreteTestStep();
        testStep.setResult(TestResult.FAILURE);
        group.addTestStep(testStep);

        assertThat(group.getResult(), is(TestResult.FAILURE));

    }

    @Test
    public void screenshots_should_be_taken_after_nested_steps() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
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
