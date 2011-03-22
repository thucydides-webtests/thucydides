package net.thucydides.core.model;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

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

    
    @Test
    public void the_returned_test_steps_list_should_be_read_only() {
        testRun.recordStep(successfulTestStepCalled("The user opens the Google search page"));

        List<TestStep> testSteps = testRun.getTestSteps();
        assertThat(testSteps.size(), is(1));

        try {
            testSteps.add(new ConcreteTestStep("The user opens the Google search page"));
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
        testRun.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 3"));

        assertThat(testRun.getResult(), is(FAILURE));
        assertThat(testRun.isFailure(), is(true));
    }

    @Test
    public void if_a_step_fails_the_error_message_should_be_returned_with_the_result() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 3"));


        ConcreteTestStep failingStep = (ConcreteTestStep) testRun.getTestSteps().get(1);
        assertThat(failingStep.getErrorMessage(), is("Oh bother!"));
    }

    @Test
    public void the_acceptance_test_case_is_failing_if_multiple_tests_have_failed() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
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
        testRun.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
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
        testRun.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testRun.recordStep(ignoredTestStepCalled("Step 4"));
        testRun.recordStep(successfulTestStepCalled("Step 5"));

        assertThat(testRun.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_pending_tests() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(pendingTestStepCalled("Step 2"));
        testRun.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
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
        testRun.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 5"));

        assertThat(testRun.getSuccessCount(), is(2));
    }
  
    @Test
    public void the_model_should_provide_the_number_of_failed_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 6"));

        assertThat(testRun.getFailureCount(), is(2));
    }

    @Test
    public void the_model_should_provide_the_number_of_ignored_test_steps() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
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
        testRun.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
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
        testRun.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 6"));
        testRun.recordStep(pendingTestStepCalled("Step 7"));
        testRun.recordStep(pendingTestStepCalled("Step 8"));
        testRun.recordStep(pendingTestStepCalled("Step 9"));

        assertThat(testRun.getPendingCount(), is(3));
    }
    

    @Test
    public void a_test_run_with_only_successful_tests_is_successful() {

        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_acceptance_test_run_can_contain_steps_nested_in_step_groups() {
        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.endGroup();
        
        assertThat(testRun.getTestSteps().size(), is(1));
    }


    private void createNestedTestSteps() {
        testRun.recordStep(successfulTestStepCalled("Step 0"));
        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.startGroup("Another group");
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.recordStep(successfulTestStepCalled("Step 5"));
        testRun.endGroup();
        testRun.endGroup();
    }

    @Test
    public void an_acceptance_test_run_can_contain_step_groups_nested_in_step_groups() {
        createNestedTestSteps();
        assertThat(testRun.getTestSteps().size(), is(2));
    }
    
    @Test
    public void when_test_steps_are_nested_step_count_should_include_all_steps() {
        createNestedTestSteps();
        assertThat(testRun.countTestSteps(), is(6));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_successful_nested_test_steps() {
        createNestedTestRun();
        assertThat(testRun.getSuccessCount(), is(6));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_failing_nested_test_steps() {
        createNestedTestRun();
        
        assertThat(testRun.getFailureCount(), is(3));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_pending_nested_test_steps() {
        createNestedTestRun();
        
        assertThat(testRun.getPendingCount(), is(4));
    }
    
    @Test
    public void an_acceptance_test_run_can_count_all_the_ignored_nested_test_steps() {
        createNestedTestRun();
        
        assertThat(testRun.getIgnoredCount(), is(1));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_skipped_test_steps() {
        createNestedTestRun();
        
        assertThat(testRun.getSkippedCount(), is(1));
    }

    private void createNestedTestRun() {
        testRun.recordStep(successfulTestStepCalled("Step 0"));
        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.recordStep(failingTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testRun.recordStep(pendingTestStepCalled("Step 10"));
        testRun.startGroup("Another group");
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.recordStep(successfulTestStepCalled("Step 5"));
        testRun.recordStep(ignoredTestStepCalled("Step 6"));
        testRun.recordStep(failingTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 8", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 9"));
        testRun.recordStep(pendingTestStepCalled("Step 10"));
        testRun.recordStep(pendingTestStepCalled("Step 11"));
        testRun.recordStep(pendingTestStepCalled("Step 12"));
        testRun.endGroup();
        testRun.endGroup();
    }

    @Test
    public void a_test_group_with_only_successful_tests_is_successful() {

        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.endGroup();

        TestStep aGroup = testRun.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void a_test_group_with_a_failing_test_fails() {

        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 4"));
        testRun.recordStep(ignoredTestStepCalled("Step 5"));
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.endGroup();

        TestStep aGroup = testRun.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.FAILURE));
    }

    
    @Test
    public void a_test_group_with_a_pending_test_is_pending() {

        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(pendingTestStepCalled("Step 3"));
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.endGroup();

        TestStep aGroup = testRun.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.PENDING));
    }
    
    @Test
    public void a_test_group_with_only_ignored_tests_is_ignored() {

        testRun.startGroup("A group");
        testRun.recordStep(ignoredTestStepCalled("Step 1"));
        testRun.recordStep(ignoredTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        testRun.endGroup();
        testRun.recordStep(successfulTestStepCalled("Step 4"));

        TestStep aGroup = testRun.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_test_run_with_a_nested_group_containing_a_failure_is_a_failure() {
        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.startGroup("Another group");
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testRun.endGroup();
        testRun.endGroup();
        
        assertThat(testRun.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_test_group_with_a_nested_group_containing_a_failure_is_a_failure() {
        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.startGroup("Another group");
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testRun.endGroup();
        testRun.endGroup();
       
        TestStep aGroup = testRun.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.FAILURE));
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
