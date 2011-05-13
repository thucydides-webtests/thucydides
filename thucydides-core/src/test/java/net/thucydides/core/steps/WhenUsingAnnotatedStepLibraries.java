package net.thucydides.core.steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepProvider;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
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

public class WhenUsingAnnotatedStepLibraries {

    class StepLibrary extends ScenarioSteps {

        public StepLibrary(final Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    class UserStory {

        @Steps
        public StepLibrary stepLibrary;

        public StepLibrary unannotatedStepLibrary;

    }

    class UserStoryWithWrongStepType {

        @Steps
        public List stepLibrary;

        public StepLibrary unannotatedStepLibrary;

    }

    @Test
    public void should_find_annotated_step_library() {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(UserStory.class);

        assertThat(stepsFields.size(), is(1));
    }


    @Test
    public void annotated_step_library_should_be_of_the_right_type() {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(UserStoryWithWrongStepType.class);

        assertThat(stepsFields.isEmpty(), is(true));
    }

}
