package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.fail;
import static net.thucydides.core.model.TestResult.*;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static net.thucydides.core.model.TestStepFactory.*;

public class WhenRecordingAnAcceptanceTestRun {

    AcceptanceTestRun testRun;
    
    @Before
    public void prepareAcceptanceTestRun() {
        testRun = new AcceptanceTestRun("Searching on Google");
    }
    
    @Test
    public void the_acceptance_test_run_should_record_test_steps() {

        assertThat(testRun.getTestSteps().size(), is(0));

        testRun.recordStep(successfulTestStepCalled("The user opens the Google search page"));
        testRun.recordStep(successfulTestStepCalled("The searchs for Cats"));
        
        assertThat(testRun.getTestSteps().size(), is(2));
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void the_recorded_test_steps_must_have_a_description_and_a_result() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("The test step result was not defined");
        testRun.recordStep(new TestStep("The user opens the Google search page"));
    }
    
    @Test
    public void the_returned_test_steps_list_should_be_read_only() {
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
    
    @Test
    public void the_acceptance_test_case_is_successful_if_all_the_tests_are_successful() {
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testRun.getResult(), is(SUCCESS));
        assertThat(testRun.isSuccess(), is(true));
    }
 
    @Test
    public void the_acceptance_test_case_is_a_failure_if_one_test_has_failed() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2"));
        testRun.recordStep(skippedTestStepCalled("Step 3"));

        assertThat(testRun.getResult(), is(FAILURE));
        assertThat(testRun.isFailure(), is(true));
    }
    
    @Test
    public void the_acceptance_test_case_is_failing_if_multiple_tests_have_failed() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2"));
        testRun.recordStep(failingTestStepCalled("Step 3"));
        testRun.recordStep(successfulTestStepCalled("Step 4"));

        assertThat(testRun.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_pending_if_at_least_one_test_is_pending_and_none_have_failed() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(pendingTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.recordStep(successfulTestStepCalled("Step 4"));

        assertThat(testRun.getResult(), is(PENDING));
    }
    
    @Test
    public void the_acceptance_test_case_is_failing_if_there_is_a_failure_even_with_pending_test_cases() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(pendingTestStepCalled("Step 2"));
        testRun.recordStep(failingTestStepCalled("Step 3"));
        testRun.recordStep(successfulTestStepCalled("Step 4"));

        assertThat(testRun.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_ignored_if_all_test_cases_are_ignored() {

        testRun.recordStep(ignoredTestStepCalled("Step 1"));
        testRun.recordStep(ignoredTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(ignoredTestStepCalled("Step 4"));

        assertThat(testRun.getResult(), is(IGNORED));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_failing_tests() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(pendingTestStepCalled("Step 2"));
        testRun.recordStep(failingTestStepCalled("Step 3"));
        testRun.recordStep(ignoredTestStepCalled("Step 4"));
        testRun.recordStep(successfulTestStepCalled("Step 5"));

        assertThat(testRun.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_pending_tests() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(pendingTestStepCalled("Step 2"));
        testRun.recordStep(failingTestStepCalled("Step 3"));
        testRun.recordStep(ignoredTestStepCalled("Step 4"));
        testRun.recordStep(successfulTestStepCalled("Step 5"));
        testRun.recordStep(pendingTestStepCalled("Step 6"));

        assertThat(testRun.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_successful_tests() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.recordStep(ignoredTestStepCalled("Step 4"));

        assertThat(testRun.getResult(), is(SUCCESS));
    }
    
    @Test
    public void the_model_should_provide_the_number_of_successful_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testRun.getSuccessCount(), is(3));
    }

    @Test
    public void the_model_should_provide_the_number_of_successful_test_steps_in_presence_of_other_outcomes() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4"));
        testRun.recordStep(skippedTestStepCalled("Step 5"));

        assertThat(testRun.getSuccessCount(), is(2));
    }
  
    @Test
    public void the_model_should_provide_the_number_of_failed_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4"));
        testRun.recordStep(failingTestStepCalled("Step 5"));
        testRun.recordStep(skippedTestStepCalled("Step 6"));

        assertThat(testRun.getFailureCount(), is(2));
    }

    @Test
    public void the_model_should_provide_the_number_of_ignored_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4"));
        testRun.recordStep(failingTestStepCalled("Step 5"));
        testRun.recordStep(skippedTestStepCalled("Step 6"));
        testRun.recordStep(skippedTestStepCalled("Step 7"));
        testRun.recordStep(skippedTestStepCalled("Step 8"));
        testRun.recordStep(skippedTestStepCalled("Step 9"));

        assertThat(testRun.getIgnoredCount(), is(1));
    }

    @Test
    public void the_model_should_provide_the_number_of_skipped_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4"));
        testRun.recordStep(failingTestStepCalled("Step 5"));
        testRun.recordStep(skippedTestStepCalled("Step 6"));
        testRun.recordStep(skippedTestStepCalled("Step 7"));
        testRun.recordStep(skippedTestStepCalled("Step 8"));
        testRun.recordStep(skippedTestStepCalled("Step 9"));

        assertThat(testRun.getSkippedCount(), is(4));
    }

    @Test
    public void the_model_should_provide_the_number_of_pending_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4"));
        testRun.recordStep(failingTestStepCalled("Step 5"));
        testRun.recordStep(skippedTestStepCalled("Step 6"));
        testRun.recordStep(pendingTestStepCalled("Step 7"));
        testRun.recordStep(pendingTestStepCalled("Step 8"));
        testRun.recordStep(pendingTestStepCalled("Step 9"));

        assertThat(testRun.getPendingCount(), is(3));
    }
    

    
    @Test
    public void a_test_group_with_only_successful_tests_is_successful() {

        testRun.recordStep(successfulTestStepCalled("Step 1", "Group 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2", "Group 1"));
        testRun.recordStep(failingTestStepCalled("Step 3", "Group 2"));

        assertThat(testRun.getResultForGroup("Group 1"), is(TestResult.SUCCESS));
    }

    @Test
    public void a_test_group_with_a_failing_test_fails() {

        testRun.recordStep(successfulTestStepCalled("Step 1", "Group 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2", "Group 1"));
        testRun.recordStep(failingTestStepCalled("Step 3", "Group 1"));
        testRun.recordStep(skippedTestStepCalled("Step 4", "Group 1"));
        testRun.recordStep(ignoredTestStepCalled("Step 5", "Group 1"));
        testRun.recordStep(successfulTestStepCalled("Step 4", "Group 2"));

        assertThat(testRun.getResultForGroup("Group 1"), is(TestResult.FAILURE));
    }
    
    @Test
    public void a_test_group_with_a_pending_test_is_pending() {

        testRun.recordStep(successfulTestStepCalled("Step 1", "Group 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2", "Group 1"));
        testRun.recordStep(pendingTestStepCalled("Step 3", "Group 1"));
        testRun.recordStep(successfulTestStepCalled("Step 4", "Group 2"));

        assertThat(testRun.getResultForGroup("Group 1"), is(TestResult.PENDING));
    }

    @Test
    public void a_test_group_with_only_ignored_tests_is_ignored() {

        testRun.recordStep(ignoredTestStepCalled("Step 1", "Group 1"));
        testRun.recordStep(ignoredTestStepCalled("Step 2", "Group 1"));
        testRun.recordStep(ignoredTestStepCalled("Step 3", "Group 1"));
        testRun.recordStep(successfulTestStepCalled("Step 4", "Group 2"));

        assertThat(testRun.getResultForGroup("Group 1"), is(TestResult.IGNORED));
    }


    @Test
    public void a_test_run_with_only_successful_tests_is_successful() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void the_model_records_a_human_readable_title_for_the_test_case() {

        testRun.setTitle("A test case");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));

        assertThat(testRun.getTitle(), is("A test case"));
    }    

    @Test
    public void an_acceptance_test_relates_to_a_user_story() {
        testRun.setTitle("A test case");
        testRun.setUserStory(new UserStory("A user story", "US1", "UserStory"));
        
        assertThat(testRun.getUserStory().getName(), is("A user story"));
        assertThat(testRun.getUserStory().getCode(), is("US1"));
        assertThat(testRun.getUserStory().getSource(), is("UserStory"));
    }
    
    @Test
    public void we_can_record_the_lifetime_of_a_test_run() throws InterruptedException {
        Thread.sleep(10);
        testRun.recordDuration();        
        assertThat(testRun.getDuration(), is(greaterThanOrEqualTo(10L)));
        assertThat(testRun.getDuration(), is(lessThan(100L)));
    }
    

    
    
}
