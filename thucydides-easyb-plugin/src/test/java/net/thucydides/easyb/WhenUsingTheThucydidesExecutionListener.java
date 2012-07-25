package net.thucydides.easyb;


import net.thucydides.core.steps.ConsoleStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.result.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.easyb.util.BehaviorStepType.AND;
import static org.easyb.util.BehaviorStepType.BEFORE;
import static org.easyb.util.BehaviorStepType.GIVEN;
import static org.easyb.util.BehaviorStepType.SCENARIO;
import static org.easyb.util.BehaviorStepType.STORY;
import static org.easyb.util.BehaviorStepType.THEN;
import static org.easyb.util.BehaviorStepType.WHEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheThucydidesExecutionListener {

    @Mock
    private Behavior behavior;

    private ThucydidesExecutionListener executionListener;

    private ConsoleStepListener stepListener;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        executionListener = new ThucydidesExecutionListener();
        stepListener = new ConsoleStepListener();
        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().dropAllListeners();
        StepEventBus.getEventBus().registerListener(stepListener);
    }

    @After
    public void unregisterListener() {
        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().dropAllListeners();
    }

    @Test
    public void an_easyb_scenario_should_run_the_steps() {

        BehaviorStep story = new BehaviorStep(STORY, "A User Story");
        BehaviorStep scenario = new BehaviorStep(SCENARIO, "Test Scenario");
        BehaviorStep given = new BehaviorStep(GIVEN, "a condition");
        BehaviorStep and = new BehaviorStep(AND, "another condition");
        BehaviorStep when = new BehaviorStep(WHEN, "an action");
        BehaviorStep then = new BehaviorStep(THEN, "an outcome");

        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        executionListener.gotResult(new Result(Result.SUCCEEDED));

        executionListener.startStep(and);
        executionListener.gotResult(new Result(Result.SUCCEEDED));

        executionListener.startStep(when);
        executionListener.gotResult(new Result(Result.SUCCEEDED));

        executionListener.startStep(then);
        executionListener.gotResult(new Result(Result.SUCCEEDED));

        executionListener.stopBehavior(story, behavior);
    }

    @Test
    public void when_an_easyb_given_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(GIVEN, "some condition");

        executionListener.startStep(step);

        assertThat(stepListener.toString(), is("Given some condition\n"));
    }

    @Test
    public void when_an_easyb_when_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(WHEN, "some action");

        executionListener.startStep(step);

        assertThat(stepListener.toString(), is("When some action\n"));
    }

    @Test
    public void when_an_easyb_then_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(THEN, "some outcome");

        executionListener.startStep(step);

        assertThat(stepListener.toString(), is("Then some outcome\n"));
    }

    @Test
    public void when_an_easyb_and_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(AND, "something else");

        executionListener.startStep(step);

        assertThat(stepListener.toString(), is("And something else\n"));
    }

    @Test
    public void other_easyb_step_types_do_not_create_new_groups() {
        BehaviorStep before = new BehaviorStep(BEFORE, "Other clause");
        BehaviorStep given = new BehaviorStep(GIVEN, "some condition");

        executionListener.startStep(before);
        executionListener.startStep(given);

        assertThat(stepListener.toString(), is("Given some condition\n"));
    }

    @Test
    public void successful_steps_are_marked_as_a_success() {

        BehaviorStep scenario = new BehaviorStep(SCENARIO, "A test scenario");
        BehaviorStep given = new BehaviorStep(GIVEN, "some condition");

        executionListener.startStep(scenario);
        executionListener.startStep(given);

        Result succeeded = new Result(Result.SUCCEEDED);
        executionListener.gotResult(succeeded);
        executionListener.stopStep();

        assertThat(stepListener.toString(), containsString("--> STEP DONE"));

    }

    @Test
    public void failed_steps_are_marked_as_failures() {
        BehaviorStep step = new BehaviorStep(THEN, "Test Scenario");

        executionListener.startStep(step);

        Result failed = new Result(new NullPointerException("Oh crap!"));
        executionListener.gotResult(failed);

        assertThat(stepListener.toString(), containsString("--> STEP FAILED"));
    }

    @Test
    public void pending_steps_are_marked_as_ignored() {
        BehaviorStep step = new BehaviorStep(THEN, "Test Scenario");

        executionListener.startStep(step);

        Result pending = new Result(Result.PENDING);
        executionListener.gotResult(pending);

        assertThat(stepListener.toString(), containsString("--> STEP PENDING"));

    }

    @Test
    public void ignored_steps_are_marked_as_ignored() {
        BehaviorStep step = new BehaviorStep(THEN, "Test Scenario");

        executionListener.startStep(step);

        Result ignored = new Result(Result.IGNORED);
        executionListener.gotResult(ignored);

        assertThat(stepListener.toString(), containsString("--> STEP IGNORED"));

    }

    @Test
    public void failures_at_the_scenario_level_should_be_reported() {
        BehaviorStep step = new BehaviorStep(THEN, "Test Scenario");

        executionListener.startStep(step);

        Result failed = new Result(new NullPointerException("Oh crap!"));
        executionListener.gotResult(failed);

        assertThat(stepListener.toString(), containsString("--> STEP FAILED"));

    }

    @Test
    public void success_results_at_the_scenario_level_should_be_ignored() {
        BehaviorStep step = new BehaviorStep(THEN, "Test Scenario");

        executionListener.startStep(step);

        Result failed = new Result(Result.SUCCEEDED);
        executionListener.gotResult(failed);
        executionListener.stopStep();

        assertThat(stepListener.toString(), is("Then Test Scenario\n--> STEP DONE\n"));

    }

    @Test
    public void failed_steps_are_marked_as_failing() {
        BehaviorStep step = new BehaviorStep(THEN, "Test Scenario");

        executionListener.startStep(step);

        Result failure = new Result(new Exception("Step failed"));
        executionListener.gotResult(failure);

        assertThat(stepListener.toString(), containsString("--> STEP FAILED"));
    }

    @Test
    // TODO: Check this test
    public void steps_directly_following_a_failing_step_are_skipped() {
        BehaviorStep when = new BehaviorStep(WHEN, "an action");
        BehaviorStep then = new BehaviorStep(THEN, "an action");
        BehaviorStep and = new BehaviorStep(AND, "an action");

        executionListener.startStep(when);
        Result success = new Result(Result.SUCCEEDED);
        executionListener.gotResult(success);
        executionListener.startStep(then);
        executionListener.gotResult(new Result(new Exception("Failed")));
        executionListener.startStep(and);
        executionListener.stopStep();
        executionListener.completeTesting();
    }

}
