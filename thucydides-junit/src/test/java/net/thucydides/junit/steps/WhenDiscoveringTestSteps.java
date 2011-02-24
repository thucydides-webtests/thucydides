package net.thucydides.junit.steps;

import static net.thucydides.junit.hamcrest.ThucydidesMatchers.withName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.List;

import net.thucydides.core.annotations.StepProvider;
import net.thucydides.junit.integration.pages.ApacheHomePage;
import net.thucydides.junit.integration.samples.ApacheScenarioSteps;

import org.junit.Test;

public class WhenDiscoveringTestSteps {

    public class ApacheStepIndex extends StepIndex {

        @StepProvider
        public Class<?>[] stepClasses = {ApacheScenarioSteps.class};

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void client_app_can_discover_available_test_step_classes() {
        StepIndex index = new ApacheStepIndex();
        List stepClasses = index.getStepClasses();
        assertThat(stepClasses, hasItem(ApacheScenarioSteps.class));
    }

    @Test
    public void client_app_can_discover_available_test_steps() {
        StepIndex index = new ApacheStepIndex();
        List stepMethods = index.getStepsFor(ApacheScenarioSteps.class);
        assertThat(stepMethods, hasItem(withName(is("clickOnProjects"))));
    }
    
    public class ApacheStepIndexWithoutAnnotation extends StepIndex {

        public Class<?>[] stepClasses = {ApacheScenarioSteps.class};

    }
    
    @Test(expected=IllegalArgumentException.class)
    public void step_index_must_have_an_annotated_step_provided() {
        StepIndex index = new ApacheStepIndexWithoutAnnotation();
        index.getStepClasses();
    }

    public class ApacheStepIndexWithWrongTypes extends StepIndex {

        @StepProvider
        public Class<?>[] stepClasses = {ApacheScenarioSteps.class, ApacheHomePage.class};

    }

    @Test(expected=IllegalArgumentException.class)
    public void step_classes_must_be_derived_from_ScenarioSteps() {
        StepIndex index = new ApacheStepIndexWithWrongTypes();
        index.getStepClasses();
    }
    
}
