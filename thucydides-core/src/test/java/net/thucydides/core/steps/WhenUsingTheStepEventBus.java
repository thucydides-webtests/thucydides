package net.thucydides.core.steps;

import com.sun.jdi.event.StepEvent;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.csv.Person;
import net.thucydides.core.model.Story;
import net.thucydides.core.pages.Pages;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class WhenUsingTheStepEventBus {

    static class SimpleTestScenarioSteps extends ScenarioSteps {

        public SimpleTestScenarioSteps(Pages pages) {
            super(pages);
        }

        @Step
        public void step1(){
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
    }

    static class SampleTestScenario {

        @Steps
        SimpleTestScenarioSteps steps;

        public void sampleTest() {
            steps.step1();
            steps.step2();
            steps.step3();
        }
    }

    @Mock
    WebDriver driver;

    @Mock
    StepListener listener;

    SampleStepListener sampleStepListener;

    private StepFactory factory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        factory = new StepFactory(new Pages(driver));

        sampleStepListener = new SampleStepListener();
    }

    @Test
    public void should_execute_steps_transparently() {
        SimpleTestScenarioSteps steps = factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        verify(driver).get("step_one");
        verify(driver).get("step_two");
        verify(driver).get("step_three");
    }

    @Test
    public void the_step_event_bus_can_be_used_to_sent_notification_events_about_steps() {
        SimpleTestScenarioSteps steps = factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().registerListener(listener);

        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));

        verify(listener).stepStarted(any(ExecutedStepDescription.class));
    }

    @Test
    public void should_record_when_a_test_starts_and_finishes() {
        SimpleTestScenarioSteps steps = factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().registerListener(sampleStepListener);

        StepEventBus.getEventBus().testStarted("a_test");
        steps.step1();
        StepEventBus.getEventBus().testFinished();

        System.out.println(sampleStepListener.toString());
    }

    @Test
    public void should_notify_listeners_when_a_step_starts() {
        SimpleTestScenarioSteps steps = factory.getStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getEventBus().registerListener(listener);

        steps.step1();

        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        verify(listener).stepStarted(argument.capture());

        assertThat(argument.getValue().getName(), is("step1"));
    }

}
