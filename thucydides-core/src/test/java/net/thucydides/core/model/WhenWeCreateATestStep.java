package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.io.File;
import java.io.IOException;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenWeCreateATestStep {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    @Test
    public void the_test_step_has_a_description() {
        TestStep step = new TestStep("a narrative description");
        assertThat(step.getDescription(), is("a narrative description"));
    }
    
    @Test
    public void the_test_step_can_have_an_illustration() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        File screenshot = temporaryFolder.newFile("screenshot.png");
        step.setScreenshot(screenshot);
        
        assertThat(step.getScreenshot(), is(screenshot));
    }
    
    @Test
    public void when_a_step_fails_the_error_message_can_be_recorded() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        step.setResult(TestResult.FAILURE);
        Exception e = new IllegalStateException();
        step.failedWith("Oh nose!",e);
        assertThat(step.getErrorMessage(), is("Oh nose!"));
    }
    
    @Test
    public void when_a_step_fails_the_stack_trace_is_also_recorded() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException();
        step.failedWith("Oh nose!",e);
        assertThat(step.getException(), is(e));
    }
    
    @Test
    public void we_can_record_the_lifetime_of_a_test_step() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        Thread.sleep(10);
        step.recordDuration();        
        assertThat(step.getDuration(), is(greaterThanOrEqualTo(10L)));
        assertThat(step.getDuration(), is(lessThan(100L)));
    }
    
    @Test
    public void a_test_result_can_be_defined_for_a_step() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.SUCCESS);

        assertThat(step.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_test_step_with_empty_child_steps_is_pending() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.addChildStep(new TestStep("child step 1"));
        step.addChildStep(new TestStep("child step 2"));
        step.addChildStep(new TestStep("child step 3"));

        assertThat(step.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void an_empty_step_is_pending() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        assertThat(step.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_test_step_with_successful_child_steps_is_successful() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_ignored_test_step_with_successful_child_steps_is_still_ignored() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.IGNORED);
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_skipped_test_step_with_successful_child_steps_is_still_ignored() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.SKIPPED);
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void a_pending_test_step_with_successful_child_steps_is_still_pending() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.PENDING);
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.PENDING));
    }

    private TestStep successfulTestStepCalled(String stepName) {
        TestStep step = new TestStep(stepName);
        step.setResult(TestResult.SUCCESS);
        return step;
    }

}
