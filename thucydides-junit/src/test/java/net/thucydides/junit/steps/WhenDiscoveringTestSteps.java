package net.thucydides.junit.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    @Test
    public void client_app_can_discover_available_test_step_classes() {
        StepIndex index = new ApacheStepIndex();
        List<Class<? extends ScenarioSteps>> stepClasses = index.getStepClasses();
        assertThat(stepClasses.contains(ApacheScenarioSteps.class), is(true));
    }

    @Test
    public void client_app_can_discover_available_test_steps() {
        StepIndex index = new ApacheStepIndex();
        List<Method> stepMethods = index.getStepsFor(ApacheScenarioSteps.class);
        List<String> methodNames = methodNamesFrom(stepMethods);
        assertThat(methodNames, hasItem("clickOnProjects"));
    }
    
    private List<String> methodNamesFrom(List<Method> stepMethods) {
        List<String> results = new ArrayList<String>();
        for(Method method : stepMethods) {
            results.add(method.getName());
        }
        return results;
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
