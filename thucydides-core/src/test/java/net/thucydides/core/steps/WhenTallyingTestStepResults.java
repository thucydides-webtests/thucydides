package net.thucydides.core.steps;

import net.thucydides.core.annotations.StepProvider;
import net.thucydides.core.steps.samples.SimpleScenarioSteps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class WhenTallyingTestStepResults {

    TestStepResult testStepResult;

    @Mock
    StepFailure stepFailure1;

    @Mock
    StepFailure stepFailure2;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        testStepResult = new TestStepResult();
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
}
