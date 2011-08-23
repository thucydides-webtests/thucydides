package net.thucydides.core.steps;

import net.thucydides.core.annotations.Story;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenTallyingTestStepResults {

    TestStepResult testStepResult;

    @Mock
    StepFailure stepFailure1;

    @Mock
    StepFailure stepFailure2;

    @Mock
    ExecutedStepDescription description;

    @Mock
    File outputDirectory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        testStepResult = new TestStepResult();
        when(description.getName()).thenReturn("Some test");

        when(stepFailure1.getDescription()).thenReturn(description);
        when(stepFailure1.getException()).thenReturn(new AssertionError("Oops!"));
        when(stepFailure2.getDescription()).thenReturn(description);
        when(stepFailure2.getException()).thenReturn(new AssertionError("Oops!"));
    }

    @Test
    public void should_be_able_to_log_test_failures() {
        testStepResult.logFailure(stepFailure1);

        assertThat(testStepResult.getFailures(), hasItem(stepFailure1));
    }
    
    @Test
    public void should_be_able_to_count_step_failures() {
        testStepResult.logFailure(stepFailure1);
        testStepResult.logFailure(stepFailure2);

        assertThat(testStepResult.getFailureCount(), is(2));
    }


    @Test
    public void should_be_able_to_count_executed_steps() {
        testStepResult.logExecutedTest();
        testStepResult.logExecutedTest();

        assertThat(testStepResult.getRunCount(), is(2));
    }

    @Test
    public void should_be_able_to_count_ignored_steps() {
        testStepResult.logIgnoredTest();
        testStepResult.logIgnoredTest();
        testStepResult.logIgnoredTest();

        assertThat(testStepResult.getIgnoreCount(), is(3));
    }

    @Test
    public void a_test_run_succeeds_if_there_is_a_step_failure() {
        testStepResult.logExecutedTest();
        assertThat(testStepResult.wasSuccessful(), is(true));
    }

    @Test
    public void a_test_run_succeeds_if_all_steps_are_ignored() {
        testStepResult.logExecutedTest();
        assertThat(testStepResult.wasSuccessful(), is(true));
    }

    @Test
    public void a_test_run_succeeds_if_no_steps_are_executed() {
        assertThat(testStepResult.wasSuccessful(), is(true));
    }

    @Test
    public void a_test_run_fails_if_there_is_a_step_failure() {
        testStepResult.logFailure(stepFailure1);

        assertThat(testStepResult.wasSuccessful(), is(false));
    }

    @Test
    public void no_tests_should_have_initially_failed() {
        BaseStepListener baseStepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);

        assertThat(baseStepListener.aStepHasFailed(), is(false));
    }

    class MyStory {}

    @Story(MyStory.class)
    class MyTestCase {
        public void app_should_work() {}
    }

    @Test
    public void a_test_suite_can_be_started_directly_using_a_story_class() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyStory.class);
        stepListener.testStarted("app_should_work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("My story"));
    }

    @Test
    public void a_test_suite_can_be_started_directly_using_a_story_class_without_a_test_class() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyStory.class);
        stepListener.testStarted("the app should work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("My story"));
    }

    @Test
    public void a_test_suite_can_be_started_directly_using_a_story_instance_without_a_test_class() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(MyStory.class);
        stepListener.testSuiteStarted(story);
        stepListener.testStarted("the app should work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("My story"));
    }

    @Test
    public void should_keep_track_of_when_a_test_has_failed() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        stepListener.stepStarted(ExecutedStepDescription.withTitle("A test"));

        StepFailure failure = new StepFailure(ExecutedStepDescription.withTitle("Oops!"), new AssertionError());
        stepListener.stepFailed(failure);
        assertThat(stepListener.aStepHasFailed(), is(true));
    }

    @Test
    public void test_failures_should_be_reset_at_the_start_of_each_test_case() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("app_should_work");
        stepListener.stepStarted(ExecutedStepDescription.withTitle("A test"));

        StepFailure failure = new StepFailure(ExecutedStepDescription.withTitle("Oops!"), new AssertionError());

        stepListener.stepFailed(failure);
        assertThat(stepListener.aStepHasFailed(), is(true));

        stepListener.testFinished(new TestStepResult());
        stepListener.testStarted("app_should_still_work");

        assertThat(stepListener.aStepHasFailed(), is(false));
    }

}
