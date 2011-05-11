package net.thucydides.easyb;


import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.easyb.domain.Behavior;
import org.easyb.domain.Story;
import org.easyb.result.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.mockito.Mock;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mockito.Mockito;
import org.openqa.selenium.firefox.FirefoxDriver;
import net.thucydides.core.steps.StepListener;
import org.mockito.MockitoAnnotations;
import org.easyb.BehaviorStep;

import static org.easyb.util.BehaviorStepType.*;

public class WhenUsingTheThucydidesExecutionListener {

    @Mock
    private StepListener stepListener;

    @Mock
    private Behavior behavior;

    private ThucydidesExecutionListener executionListener;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        executionListener = new ThucydidesExecutionListener(stepListener);
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
        executionListener.stopStep();

        executionListener.startStep(and);
        executionListener.gotResult(new Result(Result.SUCCEEDED));
        executionListener.stopStep();

        executionListener.startStep(when);
        executionListener.gotResult(new Result(Result.SUCCEEDED));
        executionListener.stopStep();

        executionListener.startStep(then);
        executionListener.gotResult(new Result(Result.SUCCEEDED));
        executionListener.stopStep();

        executionListener.stopStep();

        executionListener.stopBehavior(story, behavior);

    }

    @Test
    public void when_an_easyb_scenario_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);

        verify(stepListener).stepGroupStarted("Test Scenario");
    }

    @Test
    public void when_an_easyb_scenario_finishes_the_group_is_finished() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);
        executionListener.stopStep();

        verify(stepListener).stepGroupStarted("Test Scenario");
        verify(stepListener).stepGroupFinished();
    }

    @Test
    public void when_an_easyb_given_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(GIVEN, "some condition");

        executionListener.startStep(step);

        verify(stepListener).stepGroupStarted("Given some condition");
    }

    @Test
    public void when_an_easyb_when_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(WHEN, "some action");

        executionListener.startStep(step);

        verify(stepListener).stepGroupStarted("When some action");
    }

    @Test
    public void when_an_easyb_then_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(THEN, "some outcome");

        executionListener.startStep(step);

        verify(stepListener).stepGroupStarted("Then some outcome");
    }

    @Test
    public void when_an_easyb_and_starts_a_group_is_started_in_the_listener() {
        BehaviorStep step = new BehaviorStep(AND, "something else");

        executionListener.startStep(step);

        verify(stepListener).stepGroupStarted("And something else");
    }

    @Test
    public void other_easyb_step_types_do_not_create_new_groups() {
        BehaviorStep step = new BehaviorStep(BEFORE, "Other clause");

        executionListener.startStep(step);

        verify(stepListener, never()).stepGroupStarted("Other clause");
    }

    @Test
    public void successful_steps_are_marked_as_a_success() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);

        Result succeeded = new Result(Result.SUCCEEDED);
        executionListener.gotResult(succeeded);

        verify(stepListener).stepSucceeded();

    }

    @Test
    public void failed_steps_are_marked_as_failures() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);

        Result failed = new Result(new NullPointerException("Oh crap!"));
        executionListener.gotResult(failed);

        verify(stepListener).stepFailed(any(StepFailure.class));
    }

    @Test
    public void pending_steps_are_marked_as_ignored() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);

        Result pending = new Result(Result.PENDING);
        executionListener.gotResult(pending);

        verify(stepListener).stepIgnored(any(ExecutedStepDescription.class));

    }

    @Test
    public void ignored_steps_are_marked_as_ignored() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);

        Result ignored = new Result(Result.IGNORED);
        executionListener.gotResult(ignored);

        verify(stepListener).stepIgnored(any(ExecutedStepDescription.class));

    }

    @Test
    public void failed_steps_are_marked_as_failing() {
        BehaviorStep step = new BehaviorStep(SCENARIO, "Test Scenario");

        executionListener.startStep(step);

        Result failure = new Result(new Exception("Step failed"));
        executionListener.gotResult(failure);

        verify(stepListener).stepFailed(any(StepFailure.class));
    }

    @Test
    public void steps_directly_following_a_failing_step_are_skipped() {
        BehaviorStep scenario = new BehaviorStep(SCENARIO, "Test Scenario");
        BehaviorStep given = new BehaviorStep(GIVEN, "a condition");
        BehaviorStep when = new BehaviorStep(WHEN, "an action");
        BehaviorStep then = new BehaviorStep(THEN, "an outcome");

        executionListener.startStep(scenario);

        executionListener.startStep(given);
        Result failure = new Result(new Exception("Step failed"));
        executionListener.gotResult(failure);
        executionListener.stopStep();

        executionListener.startStep(when);
        Result succeeded = new Result(Result.SUCCEEDED);
        executionListener.gotResult(succeeded);
        executionListener.stopStep();

        verify(stepListener).stepFailed(any(StepFailure.class));
        verify(stepListener).stepIgnored(any(ExecutedStepDescription.class));
    }

    @Test
    public void steps_in_a_new_scenario_following_a_failing_step_are_not_skipped() {
        BehaviorStep scenario1 = new BehaviorStep(SCENARIO, "Test Scenario");
        BehaviorStep scenario2 = new BehaviorStep(SCENARIO, "Test Scenario");
        BehaviorStep given = new BehaviorStep(GIVEN, "a condition");
        BehaviorStep when = new BehaviorStep(WHEN, "an action");

        executionListener.startStep(scenario1);

        executionListener.startStep(given);
        Result failure = new Result(new Exception("Step failed"));
        executionListener.gotResult(failure);
        executionListener.stopStep();

        executionListener.stopStep();

        executionListener.startStep(scenario2);
        executionListener.startStep(when);
        Result succeeded = new Result(Result.SUCCEEDED);
        executionListener.gotResult(succeeded);
        executionListener.stopStep();

        verify(stepListener).stepFailed(any(StepFailure.class));
        verify(stepListener).stepSucceeded();
    }

}
