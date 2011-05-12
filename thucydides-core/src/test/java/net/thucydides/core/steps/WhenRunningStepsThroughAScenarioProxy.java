package net.thucydides.core.steps;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WhenRunningStepsThroughAScenarioProxy {

    static class NestedTestScenarioSteps extends ScenarioSteps {
        public NestedTestScenarioSteps(Pages pages) {
            super(pages);
        }

        @Steps
        NestedNestedTestScenarioSteps nestedNestedTestScenarioSteps;

        @StepGroup("Step group 1")
        public void step_group1(){
            step1();
            step2();
            step3();
        }

        @Step
        public void step1(){
            getDriver().get("nested.step1");
        }

        @Step
        public void step2(){
            getDriver().get("nested.step2");
        }

        @Step
        public void step3(){
            getDriver().get("nested.step3");
        }

        @Ignore
        @Step
        public void ignored_step() {
            getDriver().get("nested.ignored_step");
        }

        @Step
        public void nested_steps() {
            nestedNestedTestScenarioSteps.step1();
        }
    }

    static class NestedNestedTestScenarioSteps extends ScenarioSteps {
         public NestedNestedTestScenarioSteps(Pages pages) {
             super(pages);
         }

         @Step
         public void step1(){
             getDriver().get("nested.nested.step1");
         }
    }

    static class SimpleTestScenarioSteps extends ScenarioSteps {

        @Steps
        public NestedTestScenarioSteps nestedSteps;

        public SimpleTestScenarioSteps(Pages pages) {
            super(pages);
        }

        @StepGroup("Step group 1")
        public void step_group1(){
            step1();
            step2();
            step3();
        }

        @Step
        public void step1(){
            getDriver().get("step1");
        }

        @Step
        public void step2(){
            getDriver().get("step2");
        }

        @Step
        public void step3(){
            getDriver().get("step3");
        }

        @Step
        public void step_with_parameter(String name){
            getDriver().get("step_with_parameter");
        }

        @Step
        public void step_with_parameters(String name, int age){
            getDriver().get("step_with_parameters");
        }

        @Ignore
        @Step
        public void ignored_step(){
            getDriver().get("ignored_step");
        }

        @Pending
        @Step
        public void pending_step(){
            getDriver().get("pending_step");
        }

        @Step
        public void failing_step() {
            getDriver().get("failing_step");
            throw new AssertionError("Oops!");
        }

        @Step
        public void nested_steps() {
            getDriver().get("nested_steps");
            nestedSteps.step1();
            nestedSteps.step2();
            nestedSteps.step3();
            nestedSteps.nested_steps();
        }

        @Step
        public void nested_steps_with_ignored_steps() {
            getDriver().get("nested_steps_with_ignored_steps");
            nestedSteps.step1();
            nestedSteps.step2();
            nestedSteps.step3();
            nestedSteps.ignored_step();
        }

    }


    @Mock
    WebDriver driver;

    @Mock
    StepListener listener;

    private StepFactory factory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        factory = new StepFactory(new Pages(driver));
        factory.addListener(listener);
    }

    @Test
    public void the_proxy_should_execute_steps_transparently() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        verify(driver).get("step1");
        verify(driver).get("step2");
        verify(driver).get("step3");
    }

    @Test
    public void the_proxy_should_store_step_method_parameters() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step_with_parameter("Joe");

        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        verify(listener).stepStarted(argument.capture());

        assertThat(argument.getValue().getName(), is("step_with_parameter: <span class='single-parameter'>Joe</span>"));
    }

    @Test
    public void the_proxy_should_store_multiple_step_method_parameters() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step_with_parameters("Joe", 10);

        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        verify(listener).stepStarted(argument.capture());

        assertThat(argument.getValue().getName(), is("step_with_parameters: <span class='parameters'>Joe, 10</span>"));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_are_starting() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        verify(listener, times(3)).stepStarted(any(ExecutedStepDescription.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_are_starting_with_details_about_step_name_and_class() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();

        verify(listener).stepStarted(argument.capture());
        assertThat(argument.getValue().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getName(), is("step1"));
    }


    @Test
    public void the_proxy_should_notify_listeners_when_tests_have_finished() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        verify(listener, times(3)).stepFinished(any(ExecutedStepDescription.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_have_finished_with_description_details() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();

        verify(listener).stepFinished(argument.capture());
        assertThat(argument.getValue().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getName(), is("step1"));
    }


    @Test
    public void the_proxy_should_notify_listeners_when_test_groups_start_and_finish() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step_group1();

        verify(listener).stepGroupStarted(any(ExecutedStepDescription.class));
        verify(listener, times(3)).stepStarted(any(ExecutedStepDescription.class));
        verify(listener, times(3)).stepFinished(any(ExecutedStepDescription.class));
        verify(listener).stepGroupFinished();
    }

    @Test
    public void the_proxy_should_skip_ignored_tests() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.ignored_step();
        steps.step3();

        verify(driver).get("step1");
        verify(driver, never()).get("ignored_step");
        verify(driver).get("step3");

    }

    @Test
    public void the_proxy_should_skip_pending_tests() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.ignored_step();
        steps.step3();

        verify(driver).get("step1");
        verify(driver, never()).get("pending_step");
        verify(driver).get("step3");

    }

    @Test
    public void the_proxy_should_notify_listeners_of_ignored_tests_as_skipped() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.ignored_step();

        verify(listener, times(1)).stepIgnored(any(ExecutedStepDescription.class));

    }

    @Test
    public void the_proxy_should_notify_listeners_of_pending_tests_as_skipped() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.pending_step();

        verify(listener, times(1)).stepIgnored(any(ExecutedStepDescription.class));

    }

    @Test
    public void the_proxy_should_notify_listeners_when_a_step_fails() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.failing_step();

        verify(listener, times(1)).stepFailed(any(StepFailure.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_with_a_description_and_a_cause_when_a_step_fails() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.failing_step();

        ArgumentCaptor<StepFailure> argument = ArgumentCaptor.forClass(StepFailure.class);
        verify(listener).stepFailed(argument.capture());
        assertThat(argument.getValue().getDescription().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getDescription().getName(), is("failing_step"));
        assertThat(argument.getValue().getException().getClass().getName(), is(AssertionError.class.getName()));

        verify(listener, times(1)).stepFailed(any(StepFailure.class));

    }

    @Test
    public void the_proxy_should_skip_steps_following_a_step_failure() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.failing_step();
        steps.step2();
        steps.step3();

        verify(driver).get("step1");
        verify(driver).get("failing_step");
        verify(driver, never()).get("step2");
        verify(driver, never()).get("step3");
    }

    @Test
    public void the_proxy_records_the_total_number_of_test_steps_executed() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();
        steps.done();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testRunFinished(argument.capture());

        assertThat(argument.getValue().getRunCount(), is(3));
    }

    @Test
    public void the_proxy_records_the_number_of_ignored_test_steps() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();
        steps.ignored_step();
        steps.done();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testRunFinished(argument.capture());

        assertThat(argument.getValue().getIgnoreCount(), is(1));
    }

    @Test
    public void the_proxy_records_the_number_of_pending_test_steps() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();
        steps.pending_step();
        steps.done();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testRunFinished(argument.capture());

        assertThat(argument.getValue().getIgnoreCount(), is(1));
    }

    @Test
    public void the_proxy_records_the_number_of_failing_test_steps() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();
        steps.failing_step();
        steps.done();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testRunFinished(argument.capture());

        assertThat(argument.getValue().getFailureCount(), is(1));
    }

    @Test(expected = AssertionError.class)
    public void the_proxy_throws_the_original_exception_if_configured() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class, true);

        steps.step1();
        steps.step2();
        steps.step3();
        steps.failing_step();
        steps.done();
    }

    @Test
    public void the_proxy_calls_nested_step_methods() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.nested_steps();

        verify(driver).get("nested_steps");
        verify(driver).get("nested.step1");
        verify(driver).get("nested.step2");
        verify(driver).get("nested.step3");
        verify(driver).get("nested.nested.step1");
    }

    @Test
    public void the_proxy_skiped_ignored_nested_steps() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        steps.nested_steps_with_ignored_steps();
        steps.done();

        verify(driver).get("nested_steps_with_ignored_steps");
        verify(driver).get("nested.step1");
        verify(driver).get("nested.step2");
        verify(driver).get("nested.step3");
        verify(driver,never()).get("nested.ignored_step");
    }

    @Test
    public void listeners_are_notified_at_the_end_of_a_test() {
        SimpleTestScenarioSteps steps = (SimpleTestScenarioSteps) factory.newSteps(SimpleTestScenarioSteps.class);

        factory.notifyStepFinished();

        steps.done();
    }

}
