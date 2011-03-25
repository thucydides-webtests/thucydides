package net.thucydides.junit.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import net.thucydides.core.pages.Pages;
import net.thucydides.samples.SampleScenarioSteps;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WhenCreatingNewStepLibraries {

    @Mock
    Pages pages;
    
    @Mock
    ScenarioStepListener mockListener;
    
    StepFactory factory;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        factory = new StepFactory(pages);
    }
    
    @Test
    public void the_step_factory_notifies_listeners_when_a_test_starts_and_finishes() throws Exception {
        factory.addListener(mockListener);
        SampleScenarioSteps steps = (SampleScenarioSteps) factory.newSteps(SampleScenarioSteps.class);
        
        steps.stepThatSucceeds();
        
        verify(mockListener).testStarted(any(Description.class));
        verify(mockListener).testFinished(any(Description.class));
        
    }
    
    @Test
    public void the_step_factory_notifies_listeners_when_a_failing_test_starts_and_fails() throws Exception {
        factory.addListener(mockListener);
        SampleScenarioSteps steps = (SampleScenarioSteps) factory.newSteps(SampleScenarioSteps.class);
        
        steps.stepThatFails();
        
        verify(mockListener).testStarted(any(Description.class));
        verify(mockListener).testFailure(any(Failure.class));      
    }

    @Test
    public void the_step_factory_notifies_listeners_when_a_test_is_skipped() throws Exception {
        factory.addListener(mockListener);
        SampleScenarioSteps steps = (SampleScenarioSteps) factory.newSteps(SampleScenarioSteps.class);
        
        steps.stepThatFails();
        steps.stepThatShouldBeSkipped();
        
        verify(mockListener,times(2)).testStarted(any(Description.class));
        verify(mockListener).testFailure(any(Failure.class));      
        verify(mockListener).testIgnored(any(Description.class));
    }
    
    @Test
    public void the_step_factory_notifies_listeners_of_tests_when_a_test_group_is_run() throws Exception {
        factory.addListener(mockListener);
        SampleScenarioSteps steps = (SampleScenarioSteps) factory.newSteps(SampleScenarioSteps.class);
        
        steps.groupOfStepsContainingAFailure();
        
        verify(mockListener,times(4)).testStarted(any(Description.class));
        verify(mockListener,times(2)).testFinished(any(Description.class));
        verify(mockListener).testFailure(any(Failure.class));      
        verify(mockListener).testIgnored(any(Description.class));
    }

    @Test
    public void the_step_factory_should_also_instanciate_any_nested_step_libraries() throws Exception {
        factory.addListener(mockListener);
        SampleScenarioSteps steps = (SampleScenarioSteps) factory.newSteps(SampleScenarioSteps.class);
        
        steps.stepThatCallsNestedSteps();
        
        verify(mockListener,times(3)).testStarted(any(Description.class));
        verify(mockListener,times(3)).testFinished(any(Description.class));
    }

}
