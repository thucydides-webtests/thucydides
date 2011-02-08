package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class WhenMonitoringTestFailures {

    private FailureListener listener;
    
    @Mock
    private Description description;
    
    @Mock
    private Failure failure;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    
    @Before
    public void setupListener() {
        listener = new FailureListener();
    }

    @Test
    public void when_the_test_starts_there_should_be_no_previous_failures_logged() throws Exception {

        listener.testRunStarted(description);

        assertThat(listener.aPreviousTestHasFailed(), is(false));
    }
    
    @Test
    public void when_the_test_starts_the_current_test_is_flagged_as_successful() throws Exception {

        listener.testRunStarted(description);
        assertThat(listener.theCurrentTestHasFailed(), is(false));
    }

    @Test
    public void when_a_test_failure_occurs_the_current_test_is_flagged_as_failing() throws Exception {

        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFailure(failure);

        assertThat(listener.theCurrentTestHasFailed(), is(true));
    }

    @Test
    public void when_a_test_failure_occurs_the_previous_tests_are_still_marked_as_successful() throws Exception {

        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFailure(failure);
        assertThat(listener.aPreviousTestHasFailed(), is(false));
    }

    @Test
    public void when_a_test_starts_after_a_test_failure_the_current_test_is_flagged_as_successul() throws Exception {

        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFailure(failure);
        listener.testFinished(description);
        listener.testStarted(description);
        assertThat(listener.theCurrentTestHasFailed(), is(false));
    }

    @Test
    public void when_a_test_starts_after_a_test_failure_the_previous_tests_are_marked_as_failing() throws Exception {

        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFailure(failure);
        listener.testFinished(description);
        listener.testStarted(description);
        assertThat(listener.aPreviousTestHasFailed(), is(true));
    }

    @Test
    public void when_a_test_failure_occurs_the_failure_listener_notifies_() throws Exception {

        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFailure(failure);
        listener.testFinished(description);
        listener.testStarted(description);
        assertThat(listener.aPreviousTestHasFailed(), is(true));
    }

}
