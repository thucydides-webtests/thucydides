package net.thucydides.core.steps;

import net.thucydides.core.ListenerInWrongPackage;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.screenshots.ScreenshotException;
import net.thucydides.core.steps.samples.FlatScenarioSteps;
import net.thucydides.core.steps.samples.NestedScenarioSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import sample.listeners.SampleStepListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingStepExecutionResults {

    BaseStepListener stepListener;

    StepFactory stepFactory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    File outputDirectory;

    File screenshot;

    @Mock
    FirefoxDriver driver;

    @Mock
    Pages pages;

    @Mock
    TestOutcome testOutcome;
    
    class AStory {}

    @Story(AStory.class)
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
        stepFactory = new StepFactory(pages);

        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.setDriver(driver);
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().registerListener(stepListener);
    }

    @After
    public void dropListener() {
        StepEventBus.getEventBus().dropListener(stepListener);
    }


    class MyStory {}

    @Story(MyStory.class)
    class MyTestCase {
        public void app_should_work() {}
    }

    class MyTestCaseWithoutAStory {
        public void app_should_work() {}
    }

    @Test
    public void the_listener_can_derive_the_driver_from_a_provided_pages_factory() {

        when(pages.getDriver()).thenReturn(driver);

        BaseStepListener listener = new BaseStepListener(outputDirectory, pages);

        assertThat(listener.getDriver(), is((WebDriver)driver));
    }

    @Test
    public void the_listener_can_create_a_new_driver_if_the_pages_factory_driver_is_not_defined() {

        when(pages.getDriver()).thenReturn(null);

        BaseStepListener listener = new BaseStepListener(outputDirectory, pages);

        assertThat(listener.getDriver(), is(notNullValue()));
    }

    @Test
    public void the_listener_can_create_a_new_driver_if_the_pages_factory_is_not_defined() {

        BaseStepListener listener = new BaseStepListener(outputDirectory, null);

        assertThat(listener.getDriver(), is(notNullValue()));
    }

    @Test
    public void the_listener_should_record_basic_step_execution() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(1));
        assertThat(results.get(0).toString(), is("Step one, Step two"));
    }

    @Test
    public void the_listener_should_record_the_tested_story() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("My story"));
    }

    @Test
    public void the_listener_should_record_the_tested_story_without_a_class() {

        StepEventBus.getEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getEventBus().testStarted("app should work");

        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("My story"));
    }

    @Test
    public void the_listener_should_record_the_tested_story_instance_without_a_class() {

        StepEventBus.getEventBus().testSuiteStarted(net.thucydides.core.model.Story.from(MyStory.class));
        StepEventBus.getEventBus().testStarted("app should work");

        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("My story"));
    }

    @Test
    public void if_no_user_story_is_specified_the_test_case_name_should_be_used_instead() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCaseWithoutAStory.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("My test case without a story"));
    }


    @Test
    public void you_can_also_specify_the_story_class_directly() {
        StepEventBus.getEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        net.thucydides.core.model.Story story = outcome.getUserStory();
        assertThat(story.getUserStoryClass().getName(), is(MyStory.class.getName()));
    }


    @Feature
    class MyFeature {
        class MyStoryInAFeature {}
    }

    @Story(MyFeature.MyStoryInAFeature.class)
    class MyTestCaseForAFeature {}

    @Test
    public void the_test_result_should_record_the_tested_feature() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCaseForAFeature.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        ApplicationFeature feature = outcome.getFeature();
        assertThat(feature.getFeatureClass().getName(), is(MyFeature.class.getName()));
    }

    @Test
    public void the_name_of_the_tested_feature_should_match_the_feature_class() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCaseForAFeature.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        ApplicationFeature feature = outcome.getFeature();
        assertThat(feature.getName(), is("My feature"));
    }

    @Test
    public void the_executed_step_description_should_describe_a_named_executed_step_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class,"Step one");

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
    public void the_test_outcome_title_should_come_from_the_user_story() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTitle(), is("App should work"));
    }

    @Test
    public void when_the_user_story_is_undefined_the_test_outcome_title_should_come_from_the_test_case() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCaseWithoutAStory.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTitle(), is("App should work"));
    }

    @Test
    public void the_step_listener_records_the_test_method_name_if_available() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCaseWithoutAStory.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        List<TestOutcome> results = stepListener.getTestOutcomes();
        StepEventBus.getEventBus().testFinished(testOutcome);

        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getMethodName(), is("app_should_work"));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_failing_step_should_record_the_failure() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_failing_step_should_record_the_failure_cause() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestFailureCause().getMessage(), is("Step failed"));
    }

    @Test
    public void a_failing_step_should_record_the_failure_even_outside_of_a_step() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        StepEventBus.getEventBus().testFailed(new AssertionError("Test failed"));
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_failing_step_should_record_the_cause_of_a_test_failure() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        StepEventBus.getEventBus().testFailed(new AssertionError("Test failed"));
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestFailureCause().getMessage(), is("Test failed"));
    }

    @Test
    public void a_failing_step_should_record_the_failure_details_with_the_step() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome.getTestSteps().get(1).getException(), instanceOf(StepFailureException.class));
        assertThat(testOutcome.getTestSteps().get(1).getErrorMessage(), is("Step failed"));
    }

    @Test
    public void ignored_tests_should_be_reported() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.ignoredStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void grouped_test_steps_should_appear_as_nested() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.grouped_steps();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two]"));
    }

    @Test
    public void a_single_group_should_appear_with_nested_steps() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.grouped_steps();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two]"));
    }

    @Test
    public void deeply_grouped_test_steps_should_appear_as_deeply_nested() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.deeply_grouped_steps();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Deeply grouped steps [Step one, Step two, Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two], Step two, Step one]"));
    }

    @Test
    public void pending_tests_should_be_reported() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.pendingStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Pending step"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }



    @Test
    public void pending_test_groups_should_be_reported() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.pending_group();

        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Pending group [Step three, Step two, Step one]"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void ignored_test_groups_should_be_skipped() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.ignored_group();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Ignored group [Step three, Step two, Step one]"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void a_test_group_with_an_annotated_title_should_record_the_title() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.step_with_title();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("A step with a title"));
    }

    @Test
    public void a_test_group_without_an_annotated_title_should_record_the_humanized_group_name() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.a_plain_step_group();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("A plain step group"));
    }

    @Test
    public void succeeding_test_groups_should_be_marked_as_successful_by_default() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        steps.grouped_steps();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Failing step, Step two"));
        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure_in_a_nested_step() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.nestedFailingStep();
        steps.step2();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.toString(), is("Step1 [Step one, Step two, Step three], "
                                             +"Nested failing step [Failing step], Step2 [Step one, Step three]"));

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_not_be_skipped_after_an_ignored_test() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.ignoredStep();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step one, Ignored step, Step two"));
        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void steps_with_failing_nested_steps_should_record_the_step_failure() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step_with_nested_failure();


        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Step1 [Step one, Step two, Step three], Step with nested failure [Step one, Failing step]"));
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }


    @Test
    public void should_return_failing_test_exception() {
        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step_with_nested_failure();

        assertThat(stepListener.getTestFailureCause().getMessage(), is("Step failed"));

    }

    @Test
    public void the_legacy_step_group_annotation_can_also_be_used() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.a_step_group();


        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("Annotated step group title [Step with long name, Step with long name and underscores]"));
    }

    @Test
    public void if_configured_should_pause_after_step() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "100");

        long startTime = System.currentTimeMillis();
        steps.step_one();
        steps.step_two();
        long stepDuration = System.currentTimeMillis() - startTime;

        System.setProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName(), "");

        assertThat((int)stepDuration, greaterThanOrEqualTo(100));
    }



    @Test
    public void the_result_of_a_step_group_with_no_children_should_be_the_group_default_result() {
        TestStep group = new TestStep("Test Group");
        group.setResult(TestResult.SUCCESS);

        assertThat(group.getResult(), is(TestResult.SUCCESS));

    }

    @Test
    public void the_result_of_a_step_group_with_an_undefined_result_is_pending() {
        TestStep group = new TestStep("Test Group");
        group.addChildStep(new TestStep("Child step"));
        assertThat(group.getResult(), is(TestResult.PENDING));

    }

    @Test
    public void the_result_of_a_step_group_with_children_should_be_the_result_of_the_children() {
        TestStep group = new TestStep("Test Group");

        group.setResult(TestResult.SUCCESS);


        TestStep testStep = new TestStep();
        testStep.setResult(TestResult.FAILURE);
        group.addChildStep(testStep);

        assertThat(group.getResult(), is(TestResult.FAILURE));

    }

    @Test
    public void screenshots_should_be_taken_after_steps() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }
    @Test
    public void screenshots_should_have_corresponding_html_pages() {

        TestStep step = new TestStep();
        step.setDescription("step");
        step.setScreenshot(new File("step.png"));

        assertThat(step.getScreenshotPage(), is("screenshot_step.html"));
    }

    @Test
    public void if_there_is_no_screenshot_the_html_page_reference_is_empty() {

        TestStep step = new TestStep("step");

        assertThat(step.getScreenshotPage(), is(""));
    }

    @Test
    public void screenshots_should_not_be_taken_after_steps_if_screenshots_disabled() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        System.setProperty(ThucydidesSystemProperty.ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), "true");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        verify(driver, never()).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void screenshots_should_not_be_taken_for_pending_steps() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.pendingStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        verify(driver, never()).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void screenshots_should_not_be_taken_for_pending_steps_among_implemented_stepd() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.pendingStep();
        steps.step_two();
        StepEventBus.getEventBus().testFinished(testOutcome);

        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void screenshots_should_still_be_taken_after_failing_steps_if_screenshots_disabled() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        System.setProperty(ThucydidesSystemProperty.ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), "true");

        FlatScenarioSteps steps =  stepFactory.getStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        steps.failingStep();
        StepEventBus.getEventBus().testFinished(testOutcome);

        verify(driver, times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void screenshots_should_be_taken_after_nested_steps() {

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();
        StepEventBus.getEventBus().testFinished(testOutcome);

        verify(driver, times(7)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void screenshots_will_be_ignored_if_they_cannot_be_taken() {

        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot)
                                                           .thenThrow(new ScreenshotException("Screenshot failed",null));

        StepEventBus.getEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();
        StepEventBus.getEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().get(0).getChildren().get(0).getScreenshot(), notNullValue());
        assertThat(testOutcome.getTestSteps().get(0).getChildren().get(1).getScreenshot(), nullValue());

    }

    @Test
    public void custom_listeners_on_the_classpath_are_registered_automatically() {
        List listeners = StepEventBus.getEventBus().getAllListeners();
        assertThat(containsAnInstanceOf(listeners, SampleStepListener.class), is(true));
    }

    @Test
    public void custom_listeners_in_the_core_thucydides_packages_should_not_be_included() {
        List listeners = StepEventBus.getEventBus().getAllListeners();
        assertThat(containsAnInstanceOf(listeners, ListenerInWrongPackage.class), is(false));
    }

    public boolean containsAnInstanceOf(List<StepListener> listeners, Class listenerClass) {
        for(StepListener listener : listeners) {
           if (listener.getClass() == listenerClass) {
               return true;
           }
        }
        return false;

    }

}
