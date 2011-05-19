package net.thucydides.core.steps;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.hamcrest.Matchers.containsInOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
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
        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.setDriver(driver);
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
    public void the_executed_step_description_should_describe_a_named_executed_step_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"step1");

        assertThat(executedStepDescription.getTitle(), is("Step1"));
    }

    @Test
    public void the_executed_step_description_should_return_a_human_readable_name() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"stepWithLongName");

        assertThat(executedStepDescription.getTitle(), is("Step with long name"));
    }

    @Test
    public void the_executed_step_description_for_underscored_methods_should_return_a_human_readable_name() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"step_with_long_name");

        assertThat(executedStepDescription.getTitle(), is("Step with long name"));
    }

    @Test
    public void the_executed_step_description_should_allow_a_step_without_a_test_class() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription("An easyb clause");

        assertThat(executedStepDescription.getTitle(), is("An easyb clause"));
    }

    @Test
    public void the_executed_step_description_should_return_the_corresponding_test_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"stepWithLongName");

        assertThat(executedStepDescription.getTestMethod().getName(), is("stepWithLongName"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void the_executed_step_description_should_fail_if_no_test_method_exists() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"stepWithoutMethod");

        executedStepDescription.getTestMethod();
    }

    @Test
    public void the_executed_step_description_including_parameters_should_return_the_corresponding_test_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"stepWithParameters: tom");

        assertThat(executedStepDescription.getTestMethod().getName(), is("stepWithParameters"));
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

        stepListener.testStarted(ExecutedStepDescription.withTitle("Test Run"));
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getTitle(), is("Test Run"));
    }

    @Test
    public void the_step_listener_should_be_informed_of_the_test_name_if_known() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        ExecutedStepDescription.of(FlatScenarioSteps.class,"step1");
        stepListener.testStarted(ExecutedStepDescription.of(FlatScenarioSteps.class,"step1"));
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

        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testStarted(stepDescription);

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

        assertThat(executedStepNames, containsInOrder("Step1", "Step2"));

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
        assertThat(executedSteps.size(), is(2));

        TestStepGroup stepGroup1 = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNamesInGroup1 = namesFrom(stepGroup1.getSteps());
        assertThat(executedStepNamesInGroup1, containsInOrder("Step1", "Step2", "Step3"));


        TestStepGroup stepGroup2 = (TestStepGroup) executedSteps.get(1);
        List<String> executedStepNamesInGroup2 = namesFrom(stepGroup2.getSteps());
        assertThat(executedStepNamesInGroup2, containsInOrder("Step1", "Step3"));

        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void steps_with_failing_nested_steps_should_record_the_step_failure() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step_with_nested_failure();


        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        assertThat(executedSteps.size(), is(2));

        TestStepGroup stepGroup1 = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNamesInGroup1 = namesFrom(stepGroup1.getSteps());
        assertThat(executedStepNamesInGroup1, containsInOrder("Step1", "Step2", "Step3"));


        TestStepGroup stepGroup2 = (TestStepGroup) executedSteps.get(1);
        List<String> executedStepNamesInGroup2 = namesFrom(stepGroup2.getSteps());
        assertThat(executedStepNamesInGroup2, containsInOrder("Step1", "Failing step"));

        assertThat(testRun.getResult(), is(TestResult.FAILURE));
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

        assertThat(executedStepNames, containsInOrder("Step1", "Step2"));
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
    public void if_configured_should_pause_after_step() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "100");

        long startTime = System.currentTimeMillis();
        stepListener.stepGroupStarted("Main group");
        steps.step1();
        stepListener.stepGroupFinished();
        long stepDuration = System.currentTimeMillis() - startTime;

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "");

        assertThat((int)stepDuration, greaterThanOrEqualTo(100));
    }

    @Test
    public void if_configured_should_pause_after_step_group() {
        ExecutedStepDescription group = ExecutedStepDescription.withTitle("Main group");
        group.setAGroup(true);

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "100");

        long startTime = System.currentTimeMillis();
        stepListener.stepStarted(group);
        stepListener.stepFinished(group);
        long stepDuration = System.currentTimeMillis() - startTime;

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "");

        assertThat((int)stepDuration, greaterThanOrEqualTo(100));
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

        assertThat(executedStepNames, containsInOrder("Step1", "Step2"));
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

        assertThat(executedStepNames, containsInOrder("Step1", "Step2", "Step3"));
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
    public void screenshots_should_be_taken_after_steps() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void screenshots_should_be_taken_after_groups_and_nested_steps() {

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
