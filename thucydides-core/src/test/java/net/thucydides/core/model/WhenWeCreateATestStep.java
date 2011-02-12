package net.thucydides.core.model;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

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
    public void an_illustration_can_only_be_assigned_once() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        File screenshot = temporaryFolder.newFile("screenshot.png");
        step.setScreenshot(screenshot);
        
        File aSecondScreenshot = temporaryFolder.newFile("screenshot2.png");

        try {
            step.setScreenshot(aSecondScreenshot);
            fail("We shouldn't be able to reassign a screenshot");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("Test steps are immutable - a screenshot can only be assigned once."));
        }
        assertThat(step.getScreenshot(), is(screenshot));
    }
    
    @Test
    public void the_test_result_can_only_be_assigned_once() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        step.setResult(TestResult.FAILURE);
        
        try {
            step.setResult(TestResult.SUCCESS);
            fail("We shouldn't be able to reassign the test result");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("Test steps are immutable - the test result can only be assigned once."));
        }
        assertThat(step.getResult(), is(TestResult.FAILURE));
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
    
}
