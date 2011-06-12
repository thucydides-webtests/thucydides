package net.thucydides.junit.listeners;

import net.thucydides.core.annotations.TestsStory;
import net.thucydides.core.pages.Pages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class WhenListeningForTestEvents {


    @Mock
    Description failureDescription;

    @Mock
    File outputDirectory;

    @Mock
    Pages pages;


    class MyStory {}

    @TestsStory(MyStory.class)
    final static class MyTestCase {
        public void app_should_work() {}
    }


    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private JUnitStepListener listener;

    @Before
    public void setupListener() throws Exception {
        listener = new JUnitStepListener(outputDirectory, pages);

        listener.testRunStarted(Description.createSuiteDescription(MyTestCase.class));
        listener.testStarted(Description.createTestDescription(MyTestCase.class,"app_should_work"));

    }

    @Test
    public void there_should_be_no_failing_steps_at_the_start_of_the_test() throws Exception {
        assertThat(listener.hasRecordedFailures(), is(false));
    }


    @Test
    public void a_junit_listener_should_keep_track_of_failed_test_steps() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError());

        listener.testFailure(failure);

        assertThat(listener.hasRecordedFailures(), is(true));
    }

    @Test
    public void a_junit_listener_should_keep_track_of_failure_exceptions() throws Exception {

        Throwable cause = new AssertionError();
        Failure failure = new Failure(failureDescription, cause);

        listener.testFailure(failure);

        assertThat(listener.getError(), is(cause));
    }

    @Test
    public void any_failing_test_steps_should_be_cleared_at_the_start_of_each_new_test() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError());

        listener.testFailure(failure);

        assertThat(listener.hasRecordedFailures(), is(true));

        listener.resetStepFailures();

        assertThat(listener.hasRecordedFailures(), is(false));
    }

    @Test
    public void any_failing_exceptions_should_be_cleared_at_the_start_of_each_new_test() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError());

        listener.testFailure(failure);

        assertThat(listener.hasRecordedFailures(), is(true));

        listener.resetStepFailures();

        assertThat(listener.getError(), is(nullValue()));
    }

}
