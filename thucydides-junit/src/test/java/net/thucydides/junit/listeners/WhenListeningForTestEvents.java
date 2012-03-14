package net.thucydides.junit.listeners;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class WhenListeningForTestEvents {

    @Mock
    Description testDescription;

    @Mock
    Description failureDescription;


    @Mock
    File outputDirectory;

    @Mock
    Pages pages;

    StepFactory stepFactory;

    class SampleFailingScenario {
        public void failingTest() {}
    }

    class MyStory {}

    @Story(MyStory.class)
    final static class MyTestCase {
        public void app_should_work() {}
    }

    static class MyTestSteps extends ScenarioSteps {
        public MyTestSteps(final Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}
        @Step
        public void step2() {}
        @Step
        public void failingStep() { throw new AssertionError("Step failed");}

        public void failingNormalMethod() { throw new AssertionError("Method failed");}
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private JUnitStepListener listener;

    @Before
    public void setupListener() throws Exception {
        listener = JUnitStepListener.withOutputDirectory(outputDirectory)
                                    .and().withPageFactory(pages).build();
        stepFactory = new StepFactory(pages);
        listener.testRunStarted(Description.createSuiteDescription(MyTestCase.class));
        listener.testStarted(Description.createTestDescription(MyTestCase.class,"app_should_work"));
    }

    @Test
    public void there_should_be_no_failing_steps_at_the_start_of_the_test() throws Exception {
        assertThat(listener.hasRecordedFailures(), is(false));
    }


    @Test
    public void a_junit_listener_should_record_test_results() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError("Test failed."));
        listener.testRunStarted(Description.createSuiteDescription(SampleFailingScenario.class));
        listener.testStarted(Description.createTestDescription(SampleFailingScenario.class, "failingTest"));

        listener.testFailure(failure);

        assertThat(listener.hasRecordedFailures(), is(true));
        assertThat(listener.getError().getMessage(), is("Test failed."));
    }

    @Test
    public void a_junit_listener_should_keep_track_of_failed_test_steps() throws Exception {

        MyTestSteps steps =  stepFactory.getStepLibraryFor(MyTestSteps.class);

        steps.step1();
        steps.failingStep();

        assertThat(listener.hasRecordedFailures(), is(true));
        assertThat(listener.getError().getMessage(), is("Step failed"));
    }

    @Test
    public void a_junit_listener_should_keep_track_of_failed_non_step_methods() throws Exception {

        MyTestSteps steps =  stepFactory.getStepLibraryFor(MyTestSteps.class);

        steps.failingNormalMethod();

        assertThat(listener.hasRecordedFailures(), is(true));
        assertThat(listener.getError().getMessage(), is("Method failed"));
    }


    @Test
    public void a_junit_listener_should_keep_track_of_failure_exceptions() throws Exception {

        Throwable cause = new AssertionError("Test failed");
        Failure failure = new Failure(failureDescription, cause);

        listener.testFailure(failure);

        assertThat(listener.getError(), is(cause));
    }

    @Test
    public void any_failing_test_steps_should_be_cleared_at_the_start_of_each_new_test() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError("Test failed"));

        listener.testFailure(failure);

        assertThat(listener.hasRecordedFailures(), is(true));

        listener.testStarted(Description.createTestDescription(MyTestCase.class,"app_should_still_work"));

        assertThat(listener.hasRecordedFailures(), is(false));
    }

    @Test
    public void any_failing_exceptions_should_be_cleared_at_the_start_of_each_new_test() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError("Test failed"));

        listener.testFailure(failure);

        assertThat(listener.getError(), is(not(nullValue())));

        listener.testStarted(Description.createTestDescription(MyTestCase.class,"app_should_still_work"));

        assertThat(listener.getError(), is(nullValue()));
    }

}
