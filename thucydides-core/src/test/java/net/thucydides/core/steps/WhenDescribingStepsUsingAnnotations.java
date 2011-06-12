package net.thucydides.core.steps;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.annotations.TestsRequirements;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.core.pages.Pages;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;


public class WhenDescribingStepsUsingAnnotations {

    @UserStoryCode("U2")
    class SampleTestSteps extends ScenarioSteps {

        public SampleTestSteps(final Pages pages) {
            super(pages);
        }

        @Step
        public void a_step() {}

        @Step
        @Pending
        public void a_pending_step() {}

        @Step
        @Ignore
        public void an_ignored_step() {}

        @Title("A step with an annotation")
        @Step
        public void an_annotated_step_with_a_title() {}

        @Step("A step with an annotation")
        public void an_annotated_step() {}

        @StepGroup
        public void a_step_group() {}

        @StepGroup("A step group with an annotation")
        public void an_annotated_step_group() {}

        public void a_step_with_parameters(String name) {}

        @TestsRequirement("REQ-1")
        @Step
        public void a_step_testing_a_requirement() {}

        @TestsRequirements({"REQ-1","REQ-2"})
        @Step
        public void a_step_testing_several_requirements() {}
    }

    @Test
    public void the_default_step_name_should_be_a_human_readable_version_of_the_method_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_annotated_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step with an annotation"));
    }

    @Test
    public void a_title_annotation_can_also_be_used_to_provide_a_more_readable_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_annotated_step_with_a_title");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step with an annotation"));
    }


    @Test
    public void a_step_group_name_should_be_a_human_readable_version_of_the_method_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_group");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step group"));
    }

    @Test
    public void a_step_group_can_be_annotated_to_provide_a_more_readable_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_annotated_step_group");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step group with an annotation"));
    }

    @Test
    public void should_identify_pending_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_pending_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isPending(), is(true));
    }

    @Test
    public void should_identify_non_pending_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isPending(), is(false));
    }

    @Test
    public void should_identify_ignored_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_ignored_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isIgnored(), is(true));
    }


    @Test
    public void should_identify_user_story_code() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getUserStoryCode(), is("U2"));
    }

    @Test
    public void should_identify_unignored_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isIgnored(), is(false));
    }

    @Test
    public void should_let_the_user_indicate_what_requirement_is_being_tested_by_a_step() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_testing_a_requirement");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getAnnotatedRequirements(), hasItem("REQ-1"));
    }

    @Test
    public void should_let_the_user_indicate_multiple_requirements() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_testing_several_requirements");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getAnnotatedRequirements(), hasItems("REQ-1", "REQ-2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_an_exception_if_no_matching_step_exists() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_that_does_not_exist");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        annotatedStepDescription.getName();

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_an_exception_if_you_ask_for_a_method_where_no_matching_step_exists() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_that_does_not_exist");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        annotatedStepDescription.getTestMethod();

    }

    @Test
    public void the_description_should_return_the_corresponding_step_method() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getTestMethod().getName(), is("a_step"));
    }

    @Test
    public void the_description_should_return_the_corresponding_step_method_with_parameters() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_with_parameters: Joe");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getTestMethod().getName(), is("a_step_with_parameters"));
    }

    @Test
    public void should_find_the_specified_title_if_no_class_is_specified() {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle("a step with no class");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with no class"));
    }
}
