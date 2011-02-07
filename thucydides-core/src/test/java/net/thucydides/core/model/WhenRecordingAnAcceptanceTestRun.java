package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WhenRecordingAnAcceptanceTestRun {

    @Test
    public void the_acceptance_test_run_should_have_a_title() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Searching on Google");
        assertThat(testRun.getTitle(), is("Searching on Google"));
    }

    @Test
    public void the_title_can_only_be_written_once() {
        AcceptanceTestRun testRun = new AcceptanceTestRun();
        testRun.setTitle("the proper title");
        try {
            testRun.setTitle("a different title");
            fail("We shouldn't be able to change the title once set");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(),
                    is("Test runs are immutable - the title can only be defined once."));
        }
        assertThat(testRun.getTitle(), is("the proper title"));

    }

    @Test
    public void the_acceptance_test_run_should_record_test_steps() {

        AcceptanceTestRun testRun = new AcceptanceTestRun("Searching on Google");
        assertThat(testRun.getTestSteps().size(), is(0));

        testRun.recordStep(successfulTestStepCalled("The user opens the Google search page"));
        testRun.recordStep(successfulTestStepCalled("The searchs for Cats"));
        
        assertThat(testRun.getTestSteps().size(), is(2));
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void the_recorded_test_steps_must_have_a_description_and_a_result() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Searching on Google");
        
        exception.expect(NullPointerException.class);
        exception.expectMessage("The test step result was not defined");
        testRun.recordStep(new TestStep("The user opens the Google search page"));
    }
    
    public void the_returned_test_steps_list_should_be_read_only() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Searching on Google");

        testRun.recordStep(successfulTestStepCalled("The user opens the Google search page"));

        List<TestStep> testSteps = testRun.getTestSteps();
        assertThat(testSteps.size(), is(1));

        try {
            testSteps.add(new TestStep("The user opens the Google search page"));
            fail("An UnsupportedOperationException exception should have been thrown");
        } catch (UnsupportedOperationException e) {
            assertThat(testRun.getTestSteps().size(), is(1));
        }
    }
    
    private TestStep successfulTestStepCalled(String description) {
        TestStep step = new TestStep(description);
        step.setResult(TestResult.SUCCESS);
        return step;
    }

}
