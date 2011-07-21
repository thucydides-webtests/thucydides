package net.thucydides.core.model;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class WhenInstanciatingStepLibraries {

    @Mock
    WebDriver driver;

    StepFactory stepFactory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Pages pages = new Pages(driver);
        stepFactory = new StepFactory(pages);

    }

    public static class AStepLibrary extends ScenarioSteps {
        AStepLibrary(Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ANestedStepLibrary extends ScenarioSteps {
        ANestedStepLibrary(Pages pages) {
            super(pages);
        }

        @Steps
        public AStepLibrary aStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ARecursiveNestedStepLibrary extends ScenarioSteps {
        ARecursiveNestedStepLibrary(Pages pages) {
            super(pages);
        }

        @Steps
        public AStepLibrary aStepLibrary;

        @Steps
        public ARecursiveNestedStepLibrary aRecursiveNestedStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ACyclicNestedStepLibrary extends ScenarioSteps {
        ACyclicNestedStepLibrary(Pages pages) {
            super(pages);
        }

        @Steps
        public AStepLibrary aStepLibrary;

        @Steps
        public ARecursiveNestedStepLibrary aRecursiveNestedStepLibrary;

        @Steps
        public ACyclicNestedStepLibrary aCyclicNestedStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    @Test
    public void should_instanciate_step_library_instance() {
        AStepLibrary steps = stepFactory.getStepLibraryFor(AStepLibrary.class);

        assertThat(steps, is(notNullValue()));
    }

    @Test
    public void should_instanciate_nested_step_library_instances() {
        ANestedStepLibrary steps = stepFactory.getStepLibraryFor(ANestedStepLibrary.class);

        assertThat(steps, is(notNullValue()));
        assertThat(steps.aStepLibrary, is(notNullValue()));
    }

    @Test
    public void should_correctly_instanciate_recursive_nested_step_library_instances() {
        ARecursiveNestedStepLibrary steps = stepFactory.getStepLibraryFor(ARecursiveNestedStepLibrary.class);

        assertThat(steps, notNullValue());
        assertThat(steps.aStepLibrary, is(notNullValue()));
        assertThat(steps.aRecursiveNestedStepLibrary, is(notNullValue()));
    }

    @Test
    public void should_correctly_instanciate_cyclic_nested_step_library_instances() {
        ACyclicNestedStepLibrary steps = stepFactory.getStepLibraryFor(ACyclicNestedStepLibrary.class);

        assertThat(steps, notNullValue());
        assertThat(steps.aStepLibrary, is(notNullValue()));
        assertThat(steps.aCyclicNestedStepLibrary, is(notNullValue()));
        assertThat(steps.aRecursiveNestedStepLibrary, is(notNullValue()));
    }

}
