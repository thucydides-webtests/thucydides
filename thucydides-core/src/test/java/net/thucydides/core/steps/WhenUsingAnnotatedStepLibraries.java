package net.thucydides.core.steps;

import net.thucydides.core.annotations.InvalidStepsFieldException;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
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

   class UserStoryWithNoSteps {

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


    @Test(expected=InvalidStepsFieldException.class)
    public void step_index_must_have_an_annotated_step_provided() {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findMandatoryAnnotatedFields(UserStoryWithNoSteps.class);
    }


}
