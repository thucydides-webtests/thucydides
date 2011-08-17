package net.thucydides.core.steps;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.WebdriverAssertionError;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
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
            getDriver().get("nested.step_one");
        }

        @Step
        public void step2(){
            getDriver().get("nested.step_two");
        }

        @Step
        public void step3(){
            getDriver().get("nested.step_three");
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
             getDriver().get("nested.nested.step_one");
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
            step_one();
            step2();
            step3();
        }

        @StepGroup
        public void step_group_with_failure(){
            throw new AssertionError("Oh bother!");
        }

        @Step
        public void step_one(){
            getDriver().get("step_one");
        }

        @Step
        public void step2(){
            getDriver().get("step_two");
        }

        @Step
        public void step3(){
            getDriver().get("step_three");
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
        public void failing_web_step() {
            getDriver().get("failing_step");
            throw new WebDriverException("Oops!");
        }

        @Step
        public void step_with_failing_ordinary_method() {
            failing_ordinary_method();
        }

        @Step
        public void step_with_failing_web_method() {
            failing_ordinary_web_method();
        }

        public void failing_ordinary_method() {
            throw new AssertionError("Oops!");
        }

        public void failing_ordinary_web_method() {
            throw new WebDriverException("Oops!");
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

        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().registerListener(listener);
    }

    @After
    public void deregisterListener() {
        StepEventBus.getEventBus().dropListener(listener);
    }

    @Test
    public void the_proxy_should_execute_steps_transparently() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();
        steps.step2();
        steps.step3();

        verify(driver).get("step_one");
        verify(driver).get("step_two");
        verify(driver).get("step_three");
    }

    @Test
    public void the_proxy_should_store_step_method_parameters() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_with_parameter("Joe");

        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        verify(listener).stepStarted(argument.capture());

        assertThat(argument.getValue().getName(), is("step_with_parameter: <span class='single-parameter'>Joe</span>"));
    }

    @Test
    public void the_proxy_should_store_multiple_step_method_parameters() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_with_parameters("Joe", 10);

        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        verify(listener).stepStarted(argument.capture());

        assertThat(argument.getValue().getName(), is("step_with_parameters: <span class='parameters'>Joe, 10</span>"));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_are_starting() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();
        steps.step2();
        steps.step3();

        verify(listener, times(3)).stepStarted(any(ExecutedStepDescription.class));
    }

    class AStory {}

    @Story(AStory.class)
    class ATestCase {
        public void app_should_work() {}
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_are_starting_with_details_about_step_name_and_class() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        listener.testSuiteStarted(ATestCase.class);
        listener.testStarted("app_should_work");

        steps.step_one();

        verify(listener).stepStarted(argument.capture());
        assertThat(argument.getValue().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getName(), is("step_one"));
    }


    @Test
    public void the_proxy_should_notify_listeners_when_tests_have_finished() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();
        steps.step2();
        steps.step3();

        verify(listener, times(3)).stepFinished(any(ExecutedStepDescription.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_have_finished_with_description_details() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();

        verify(listener).stepFinished(argument.capture());
        assertThat(argument.getValue().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getName(), is("step_one"));
    }


    @Test
    public void the_proxy_should_notify_listeners_when_test_groups_start_and_finish() {
        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_group1();

        verify(listener, times(4)).stepStarted(any(ExecutedStepDescription.class));
        verify(listener, times(4)).stepFinished(any(ExecutedStepDescription.class));
    }

    @Test
    public void the_proxy_should_execute_ignored_steps_but_disable_webdriver() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();
        steps.ignored_step();
        steps.step3();

        verify(driver).get("step_one");
        verify(driver).get("ignored_step");
        verify(driver).get("step_three");

    }

    @Test
    public void the_proxy_should_skip_tests_after_a_failure() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();
        steps.failing_step();
        steps.step3();

        verify(driver).get("step_one");
        verify(driver, never()).get("step4");

    }

    @Test
    public void the_proxy_should_skip_pending_tests() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_one();
        steps.ignored_step();
        steps.step3();

        verify(driver).get("step_one");
        verify(driver, never()).get("pending_step");
        verify(driver).get("step_three");

    }

    @Test
    public void the_proxy_should_notify_listeners_of_ignored_tests_as_skipped() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.ignored_step();

        verify(listener, times(1)).stepIgnored(any(ExecutedStepDescription.class));

    }

    @Test
    public void the_proxy_should_notify_listeners_of_pending_tests_as_skipped() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.pending_step();

        verify(listener, times(1)).stepIgnored(any(ExecutedStepDescription.class));

    }

    @Test
    public void the_proxy_should_notify_listeners_when_a_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.failing_step();

        verify(listener, times(1)).stepFailed(any(StepFailure.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_with_a_description_and_a_cause_when_a_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.failing_step();

        ArgumentCaptor<StepFailure> argument = ArgumentCaptor.forClass(StepFailure.class);
        verify(listener).stepFailed(argument.capture());
        assertThat(argument.getValue().getDescription().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getDescription().getName(), is("failing_step"));
        assertThat(argument.getValue().getException().getClass().getName(), is(AssertionError.class.getName()));

        verify(listener, times(1)).stepFailed(any(StepFailure.class));

    }


    @Test
    public void the_proxy_should_notify_listeners_when_a_failure_occurs_in_a_group() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_group_with_failure();

        verify(listener, times(1)).stepFailed(any(StepFailure.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_with_a_description_and_a_cause_when_a_failure_occurs_in_a_group() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_group_with_failure();

        ArgumentCaptor<StepFailure> argument = ArgumentCaptor.forClass(StepFailure.class);
        verify(listener).stepFailed(argument.capture());
        assertThat(argument.getValue().getDescription().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getDescription().getName(), is("step_group_with_failure"));
        assertThat(argument.getValue().getException().getClass().getName(), is(AssertionError.class.getName()));

        verify(listener, times(1)).stepFailed(any(StepFailure.class));

    }

    @Test
    public void the_proxy_should_notify_listeners_when_a_web_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.failing_web_step();

        verify(listener, times(1)).stepFailed(any(StepFailure.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_with_a_description_and_a_cause_when_a_web_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.failing_web_step();

        ArgumentCaptor<StepFailure> argument = ArgumentCaptor.forClass(StepFailure.class);
        verify(listener).stepFailed(argument.capture());
        assertThat(argument.getValue().getDescription().getStepClass().getName(), is(SimpleTestScenarioSteps.class.getName()));
        assertThat(argument.getValue().getDescription().getName(), is("failing_web_step"));
        assertThat(argument.getValue().getException().getClass().getName(), is(WebdriverAssertionError.class.getName()));

        verify(listener, times(1)).stepFailed(any(StepFailure.class));

    }

    @Test
    public void the_proxy_should_notify_listeners_when_a_method_that_is_not_a_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_with_failing_ordinary_method();

        verify(listener, times(1)).testFailed(any(Throwable.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_with_a_description_and_a_cause_when_a_non_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_with_failing_ordinary_method();

        ArgumentCaptor<Throwable> argument = ArgumentCaptor.forClass(Throwable.class);
        verify(listener).testFailed(argument.capture());
        assertThat(argument.getValue().getMessage(), is("Oops!"));
        assertThat(argument.getValue().getClass().getName(), is(AssertionError.class.getName()));

    }

    @Test
    public void the_proxy_should_notify_listeners_when_a_web_method_that_is_not_a_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_with_failing_web_method();

        verify(listener, times(1)).testFailed(any(Throwable.class));
    }

    @Test
    public void the_proxy_should_notify_listeners_with_a_description_and_a_cause_when_a_web_non_step_fails() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step_with_failing_web_method();

        ArgumentCaptor<Throwable> argument = ArgumentCaptor.forClass(Throwable.class);
        verify(listener).testFailed(argument.capture());
        assertThat(argument.getValue().getMessage(), containsString("Oops!"));
        assertThat(argument.getValue().getClass().getName(), is(WebDriverException.class.getName()));

    }

    @Test
    public void the_proxy_records_the_total_number_of_test_steps_executed() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().testStarted("SimpleTestScenarioSteps");
        steps.step_one();
        steps.step2();
        steps.step3();

        StepEventBus.getEventBus().testFinished();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testFinished(argument.capture());

        TestStepResult result = argument.getValue();
        assertThat(argument.getValue().getRunCount(), is(3));
    }

    @Test
    public void the_proxy_records_the_number_of_ignored_test_steps() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().testStarted("SimpleTestScenarioSteps");
        steps.step_one();
        steps.step2();
        steps.step3();
        steps.ignored_step();

        StepEventBus.getEventBus().testFinished();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testFinished(argument.capture());

        assertThat(argument.getValue().getIgnoreCount(), is(1));
    }

    @Test
    public void the_proxy_records_the_number_of_pending_test_steps() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().testStarted("SimpleTestScenarioSteps");
        steps.step_one();
        steps.step2();
        steps.step3();
        steps.pending_step();

        StepEventBus.getEventBus().testFinished();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testFinished(argument.capture());

        assertThat(argument.getValue().getIgnoreCount(), is(1));
    }

    @Test
    public void the_proxy_records_the_number_of_failing_test_steps() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().testStarted("SimpleTestScenarioSteps");
        steps.step_one();
        steps.step2();
        steps.step3();
        steps.failing_step();

        StepEventBus.getEventBus().testFinished();

        ArgumentCaptor<TestStepResult> argument = ArgumentCaptor.forClass(TestStepResult.class);
        verify(listener).testFinished(argument.capture());

        assertThat(argument.getValue().getFailureCount(), is(1));
    }

    @Test
    public void the_proxy_calls_nested_step_methods() {
        SimpleTestScenarioSteps steps =  factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().testStarted("SimpleTestScenarioSteps");
        steps.nested_steps();

        verify(driver).get("nested_steps");
        verify(driver).get("nested.step_one");
        verify(driver).get("nested.step_two");
        verify(driver).get("nested.step_three");
        verify(driver).get("nested.nested.step_one");
    }

}
