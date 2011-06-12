package net.thucydides.core.steps;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.TestsStory;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.model.features.ApplicationFeature;
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

    class AStory {}

    @TestsStory(AStory.class)
    class ATestCase {
        public void app_should_work() {}
    }

    class AStepLibrary extends ScenarioSteps {
        AStepLibrary(Pages pages) {
            super(pages);
        }
    }

    @Before
    public void createStepListenerAndFactory() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputDirectory = temporaryFolder.newFolder("thucydides");
        screenshot = temporaryFolder.newFile("screenshot.jpg");
        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.setDriver(driver);
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);
    }

    class MyStory {}

    @TestsStory(MyStory.class)
    class MyTestCase {
        public void app_should_work() {}
    }

    class MyTestCaseWithoutAStory {
        public void app_should_work() {}
    }

    @Test
    public void before_starting_a_test_run_you_need_to_specify_what_user_story_is_being_tested() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        Story story = results.get(0).getUserStory();
        assertThat(story.getUserStoryClass().getName(), is(MyStory.class.getName()));
    }


    @Test
    public void if_no_user_story_is_specified_the_test_case_name_should_be_used_instead() {

        stepListener.testRunStartedFor(MyTestCaseWithoutAStory.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        Story story = results.get(0).getUserStory();
        assertThat(story.getUserStoryClass().getName(), is(MyTestCaseWithoutAStory.class.getName()));
    }

    @Test
    public void you_can_also_specify_the_story_class_directly() {
        stepListener.testRunStartedFor(MyStory.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        Story story = results.get(0).getUserStory();
        assertThat(story.getUserStoryClass().getName(), is(MyStory.class.getName()));
    }

    @Test
    public void the_test_result_should_store_a_story_with_steps_for_each_executed_step() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome outcome = results.get(0);
        assertThat(outcome.getTestSteps().size(), is(2));
    }

    @Feature
    class MyFeature {
        class MyStoryInAFeature {}
    }

    @TestsStory(MyFeature.MyStoryInAFeature.class)
    class MyTestCaseForAFeature {}

    @Test
    public void the_test_result_should_record_the_tested_feature() {

        stepListener.testRunStartedFor(MyTestCaseForAFeature.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        ApplicationFeature feature = results.get(0).getFeature();
        assertThat(feature.getFeatureClass().getName(), is(MyFeature.class.getName()));
    }

    @Test
    public void the_name_of_the_tested_feature_should_match_the_feature_class() {

        stepListener.testRunStartedFor(MyTestCaseForAFeature.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        ApplicationFeature feature = results.get(0).getFeature();
        assertThat(feature.getName(), is("My feature"));
    }

    @Test
    public void to_use_a_step_listener_you_need_to_instantiate_a_step_library_using_the_step_factory() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();

        List<TestOutcome> results = stepListener.getTestRunResults();

        assertThat(results.size(), is(1));
    }

    @Test
    public void the_executed_step_description_should_describe_a_named_executed_step_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"step_one");

        assertThat(executedStepDescription.getTitle(), is("Step one"));
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

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getStepCount(), is(2));
    }

    @Test
    public void the_test_outcome_title_should_come_from_the_user_story() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTitle(), is("App should work"));
    }

    @Test
    public void when_the_user_story_is_undefined_the_test_outcome_title_should_come_from_the_test_case() {

        stepListener.testRunStartedFor(MyTestCaseWithoutAStory.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTitle(), is("App should work"));
    }

    @Test
    public void the_step_listener_records_the_test_method_name_if_available() {

        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getMethodName(), is("app_should_work"));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_failing_step_should_record_the_failure() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();


        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_failing_step_should_record_the_failure_details_with_the_step() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome.getTestSteps().get(1).getException(), instanceOf(AssertionError.class));
        assertThat(testOutcome.getTestSteps().get(1).getErrorMessage(), is("Step failed"));
    }

    @Test
    public void ignored_tests_should_be_reported() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.ignoredStep();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void pending_tests_should_be_reported() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.pendingStep();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void pending_test_groups_should_be_reported() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("pending group");
        stepListener.updateCurrentStepStatus(TestResult.PENDING);

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void ignored_test_groups_should_be_skipped() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("ignored group");
        stepListener.stepIgnored(ExecutedStepDescription.withTitle("Ignore this step"));

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void succeeding_test_groups_should_be_marked_as_successful_by_default() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("successful group");
        stepListener.stepSucceeded();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure_in_a_nested_step() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.nestedFailingStep();
        steps.step2();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_not_be_skipped_after_an_ignored_test() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.ignoredStep();
        steps.step_two();

        List<TestOutcome> results = stepListener.getTestRunResults();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void the_step_listener_should_record_each_step_executed_in_order() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        List<String> executedStepNames = namesFrom(executedSteps);

        assertThat(executedStepNames, containsInOrder("Step one", "Step two"));

    }

    @Test
    public void any_nested_steps_should_also_be_executed() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();

        List<TestOutcome> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        TestOutcome outcome = results.get(0);
        assertThat(outcome.getTestSteps().size(), is(1));

    }

    @Test
    public void steps_with_nested_steps_should_be_recorded_as_step_groups() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<TestOutcome> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        assertThat(executedSteps.size(), is(2));

        TestStepGroup stepGroup1 = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNamesInGroup1 = namesFrom(stepGroup1.getSteps());
        assertThat(executedStepNamesInGroup1, containsInOrder("Step one", "Step two", "Step three"));


        TestStepGroup stepGroup2 = (TestStepGroup) executedSteps.get(1);
        List<String> executedStepNamesInGroup2 = namesFrom(stepGroup2.getSteps());
        assertThat(executedStepNamesInGroup2, containsInOrder("Step one", "Step three"));

        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void steps_with_failing_nested_steps_should_record_the_step_failure() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step_with_nested_failure();


        List<TestOutcome> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        assertThat(executedSteps.size(), is(2));

        TestStepGroup stepGroup1 = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNamesInGroup1 = namesFrom(stepGroup1.getSteps());
        assertThat(executedStepNamesInGroup1, containsInOrder("Step one", "Step two", "Step three"));


        TestStepGroup stepGroup2 = (TestStepGroup) executedSteps.get(1);
        List<String> executedStepNamesInGroup2 = namesFrom(stepGroup2.getSteps());
        assertThat(executedStepNamesInGroup2, containsInOrder("Step one", "Failing step"));

        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }


    @Test
    public void starting_a_group_should_create_a_new_group_step() {
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("Main group");
        steps.step_one();
        steps.step_two();
        stepListener.stepGroupFinished();

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getDescription(), is("Main group"));
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("Step one", "Step two"));
    }

    @Test
    public void starting_and_ending_a_group_without_steps_should_result_in_success() {
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        ExecutedStepDescription group = ExecutedStepDescription.withTitle("Main group");
        group.setAGroup(true);

        stepListener.stepStarted(group);
        stepListener.updateCurrentStepStatus(TestResult.SUCCESS);
        stepListener.stepFinished(group);

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void if_configured_should_pause_after_step() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "100");

        long startTime = System.currentTimeMillis();
        stepListener.stepGroupStarted("Main group");
        steps.step_one();
        stepListener.stepGroupFinished();
        long stepDuration = System.currentTimeMillis() - startTime;

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "");

        assertThat((int)stepDuration, greaterThanOrEqualTo(100));
    }

    @Test
    public void if_configured_should_pause_after_step_group() {
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

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
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        ExecutedStepDescription group = ExecutedStepDescription.withTitle("Main group");
        group.setAGroup(true);

        stepListener.stepGroupStarted(group);
        steps.step_one();
        steps.step_two();
        stepListener.stepGroupFinished();

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getDescription(), is("Main group"));
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("Step one", "Step two"));
    }

    @Test
    public void a_group_should_be_able_to_have_its_own_result() {
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("Main group");
        stepListener.stepGroupFinished(TestResult.SUCCESS);

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);

        assertThat(topLevelStepGroup.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_step_result_can_be_updated_after_the_step_execution() {
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        stepListener.updateCurrentStepStatus(TestResult.SKIPPED);
        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        TestStep step2 = executedSteps.get(1);

        assertThat(step2.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void a_step_group_result_can_be_updated_after_the_step_execution() {
        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.stepGroupStarted("New group");
        stepListener.updateCurrentStepStatus(TestResult.PENDING);

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();
        TestStep stepGroup = executedSteps.get(0);

        assertThat(stepGroup.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void nested_steps_should_be_recorded_inside_step_groups() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<TestOutcome> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        TestOutcome testOutcome = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testOutcome.getTestSteps();

        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("Step one", "Step two", "Step three"));
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

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();

        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void screenshots_should_be_taken_after_groups_and_nested_steps() {

        stepListener.testRunStartedFor(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        verify(driver, times(7)).getScreenshotAs((OutputType<?>) anyObject());
    }

    private TestOutcome firstTestResultRecordedIn(List<TestOutcome> testOutcomeResults) {
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
