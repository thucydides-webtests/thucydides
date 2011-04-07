package net.thucydides.core.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.annotations.StepProvider;
import net.thucydides.core.model.ScenarioSteps;
import net.thucydides.core.model.StepIndex;

import org.junit.Test;

public class WhenDiscoveringTestSteps {

    public class SimpleStepIndex extends StepIndex {

        @StepProvider
        public Class<?>[] stepClasses = {SimpleScenarioSteps.class};

    }

    @Test
    public void client_app_can_discover_available_test_step_classes() {
        StepIndex index = new SimpleStepIndex();
        List<Class<? extends ScenarioSteps>> stepClasses = index.getStepClasses();
        assertThat(stepClasses.contains(SimpleScenarioSteps.class), is(true));
    }

    @Test
    public void client_app_can_discover_available_test_steps() {
        StepIndex index = new SimpleStepIndex();
        List<Method> stepMethods = index.getStepsFor(SimpleScenarioSteps.class);
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

        public Class<?>[] stepClasses = {SimpleScenarioSteps.class};

    }
    
    @Test(expected=IllegalArgumentException.class)
    public void step_index_must_have_an_annotated_step_provided() {
        StepIndex index = new ApacheStepIndexWithoutAnnotation();
        index.getStepClasses();
    }

    public class BadlyTypedScenarioSteps {};
    
    public class ApacheStepIndexWithWrongTypes extends StepIndex {

        @StepProvider
        public Class<?>[] stepClasses = {SimpleScenarioSteps.class,  BadlyTypedScenarioSteps.class};

    }

    @Test(expected=IllegalArgumentException.class)
    public void step_classes_must_be_derived_from_ScenarioSteps() {
        StepIndex index = new ApacheStepIndexWithWrongTypes();
        index.getStepClasses();
    }
    
}
