package net.thucydides.junit.runners;

import static net.thucydides.junit.hamcrest.ThucydidesMatchers.hasDescriptionMethodName;
import static net.thucydides.junit.hamcrest.ThucydidesMatchers.hasMessage;
import static net.thucydides.junit.hamcrest.ThucydidesMatchers.hasMethodName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import net.thucydides.core.pages.Pages;
import net.thucydides.junit.samples.SampleScenarioSteps;
import net.thucydides.junit.steps.StepFactory;

public class WhenUsingStepLibraries {

    StepFactory stepFactory;
    
    @Mock
    Pages pages;
    
    @Mock
    RunListener listener;
    
    SampleScenarioSteps steps;
    
    @Before
    public void initStepFactory() {
        MockitoAnnotations.initMocks(this);
        stepFactory = new StepFactory(pages);
        stepFactory.addListener(listener);

        steps = (SampleScenarioSteps) stepFactory.newSteps(SampleScenarioSteps.class);        
    }
        
    @Test
    public void the_factory_should_return_a_proxy_for_the_test_library() {
        assertThat(steps, is(not(nullValue())));
    }
    
    @Test
    public void the_proxy_should_notify_listeners_when_a_step_passes() throws Exception {
        steps.stepThatSucceeds();
        
        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("stepThatSucceeds"))));
    }

    @Test
    public void the_proxy_should_notify_listeners_of_each_step_when_several_steps_passes() throws Exception {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        
        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("stepThatSucceeds"))));
        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("anotherStepThatSucceeds"))));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_a_step_fails() throws Exception {
        steps.stepThatFails();
        
        verify(listener).testFailure(argThat(hasMessage(containsString("Expected: is <2>"))));
    }

    @Test
    public void the_proxy_should_report_the_step_name_when_a_step_fails() throws Exception {
        steps.stepThatFails();
        
        verify(listener).testFailure(argThat(hasMethodName(containsString("stepThatFails"))));
    }
    
    @Test
    public void the_proxy_should_notify_listeners_of_skipped_steps_following_a_test_failure() throws Exception {
        steps.stepThatSucceeds();
        steps.stepThatFails();
        steps.stepThatShouldBeSkipped();
        
        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("stepThatSucceeds"))));
        verify(listener).testFailure(argThat(hasMethodName(containsString("stepThatFails"))));
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatShouldBeSkipped"))));
    }

    @Test
    public void the_proxy_should_notify_listeners_of_a_skipped_step_if_the_step_is_pending() throws Exception {
        steps.stepThatIsPending();
        
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatIsPending"))));
    }
    
    @Test
    public void the_proxy_should_notify_listeners_of_a_skipped_step_if_the_step_is_ignored() throws Exception {
        steps.stepThatIsIgnored();
        
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatIsIgnored"))));
    }

    @Test
    public void ignored_and_pending_steps_can_be_invoked_at_any_stage() throws Exception {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
        steps.stepThatFails();
        steps.stepThatShouldBeSkipped();
        
        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("stepThatSucceeds"))));
        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("anotherStepThatSucceeds"))));

        verify(listener).testFailure(argThat(hasMethodName(containsString("stepThatFails"))));
        
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatShouldBeSkipped"))));        
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatIsIgnored"))));
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatIsPending"))));
    }
    
    @Test
    public void the_proxy_should_only_notify_for_methods_annotated_with_Step() throws Exception {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.anUnannotatedMethod();
        
        verify(listener,never()).testFinished(argThat(hasDescriptionMethodName(containsString("anUnannotatedMethod"))));
    }
    
    final class ResultStoreListener extends RunListener {
        public Result savedResult;
        
        @Override
        public void testRunFinished(Result result) throws Exception {
            this.savedResult = result;
        }
    }
    
    @Test
    public void the_proxy_should_notify_of_overall_test_results() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatShouldBeSkipped();
        steps.done();
        
        Result result = resultlistener.savedResult;
        
        assertThat(result.wasSuccessful(), is(true));
    }
    
    @Test
    public void the_proxy_should_notify_of_test_count_in_result() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatShouldBeSkipped();
        steps.done();
        
        Result result = resultlistener.savedResult;
        
        assertThat(result.getRunCount(), is(3));
    }

    @Test
    public void the_proxy_should_notify_of_skipped_count_in_result() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
        steps.stepThatIsIgnored();
        steps.done();
        
        Result result = resultlistener.savedResult;
        
        assertThat(result.getIgnoreCount(), is(2));
    }
    
    @Test(expected=AssertionError.class)
    public void the_proxy_should_notify_of_failure_count_in_result() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatFails();        
        steps.stepThatIsPending();
        steps.done();
        
        Result result = resultlistener.savedResult;
        
        assertThat(result.getFailureCount(), is(1));
    }
    
    @Test
    public void the_proxy_should_notify_of_failure_details_in_result() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatFails();        
        steps.stepThatIsPending();

        try {
            steps.done();
            fail("Test failure should have caused an exception");
        } catch(AssertionError e) {
            Result result = resultlistener.savedResult;
            assertThat(result.getFailures().size(), is(1));
            assertThat(result.getRunCount(), is(3));
        }
    }
    
    @Test
    public void test_steps_can_be_organized_in_groups_using_the_TestGroup_annotation() throws Exception {
        
        steps.groupOfSteps();

        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("stepThatSucceeds"))));
        verify(listener).testFailure(argThat(hasMethodName(containsString("stepThatFails"))));
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatShouldBeSkipped"))));

    }

    @Test
    public void test_groups_do_not_affect_test_results() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.groupOfSteps();

        verify(listener).testFinished(argThat(hasDescriptionMethodName(containsString("stepThatSucceeds"))));
        verify(listener).testFailure(argThat(hasMethodName(containsString("stepThatFails"))));
        verify(listener).testIgnored(argThat(hasDescriptionMethodName(containsString("stepThatShouldBeSkipped"))));

        try {
            steps.done();
            fail("Test failure should have caused an exception");
        } catch(AssertionError e) {
            Result result = resultlistener.savedResult;
            assertThat(result.wasSuccessful(), is(false));
            assertThat(result.getIgnoreCount(), is(1));
            assertThat(result.getFailureCount(), is(1));
            assertThat(result.getRunCount(), is(2));
        }
        
    }
    
    @Test(expected=NullPointerException.class)
    public void errors_within_a_test_group_should_throw_an_exception() throws Exception {
        
        ResultStoreListener resultlistener = new ResultStoreListener();
        
        stepFactory.addListener(resultlistener);
        steps.groupOfStepsContainingAnError();
        steps.done();
    }
    
    
    final class TestGroupListener extends RunListener {
        @Override
        public void testFinished(Description description) throws Exception {
            super.testFinished(description);
        }
    }
}
