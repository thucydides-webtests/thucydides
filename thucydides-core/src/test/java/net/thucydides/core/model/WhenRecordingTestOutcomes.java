package net.thucydides.core.model;

import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.annotations.TestsRequirements;
import net.thucydides.core.annotations.TestsStory;
import net.thucydides.core.annotations.Title;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.fail;

public class WhenRecordingTestOutcomes {

    TestOutcome testOutcome;

    class AUserStory {};

    @TestsStory(AUserStory.class)
    class SomeTestScenario {
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @TestsStory(AUserStory.class)
    class SomeAnnotatedTestScenario {
        @Title("Really should do this!")
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Before
    public void prepareAcceptanceTestRun() {
        testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
    }

    @Test
    public void a_test_outcome_should_record_the_tested_method_name() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getMethodName(), is("should_do_this"));
    }

    /**
     * Case for JUnit integration, where a test case is present.
     */
    @Test
    public void a_test_outcome_title_should_be_based_on_the_tested_method_name_if_defined() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getTitle(), is("Should do this"));
    }

    @Test
    public void a_test_outcome_title_can_be_overriden_using_the_Title_annotation() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);

        assertThat(outcome.getTitle(), is("Really should do this!"));
    }

    /**
     * Case for easyb integration, where we use a Story class directly.
     */
    @Test
    public void a_test_outcome_title_should_be_the_method_name_if_no_test_class_is_defined() {
        Story story = Story.from(AUserStory.class);

        TestOutcome outcome = TestOutcome.forTestInStory("Some scenario", story);

        assertThat(outcome.getTitle(), is("Some scenario"));
    }


    @Test
    public void the_acceptance_test_run_should_record_test_steps() {

        assertThat(testOutcome.getTestSteps().size(), is(0));

        testOutcome.recordStep(successfulTestStepCalled("The user opens the Google search page"));
        testOutcome.recordStep(successfulTestStepCalled("The searchs for Cats"));
        
        assertThat(testOutcome.getTestSteps().size(), is(2));
    }

    
    @Test
    public void the_returned_test_steps_list_should_be_read_only() {
        testOutcome.recordStep(successfulTestStepCalled("The user opens the Google search page"));

        List<TestStep> testSteps = testOutcome.getTestSteps();
        assertThat(testSteps.size(), is(1));

        try {
            testSteps.add(new ConcreteTestStep("The user opens the Google search page"));
            fail("An UnsupportedOperationException exception should have been thrown");
        } catch (UnsupportedOperationException e) {
            assertThat(testOutcome.getTestSteps().size(), is(1));
        }
    }
    
    @Test
    public void the_acceptance_test_case_is_successful_if_all_the_tests_are_successful() {
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(SUCCESS));
        assertThat(testOutcome.isSuccess(), is(true));
    }
 
    @Test
    public void the_acceptance_test_case_is_a_failure_if_one_test_has_failed() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(FAILURE));
        assertThat(testOutcome.isFailure(), is(true));
    }

    @Test
    public void if_a_step_fails_the_error_message_should_be_returned_with_the_result() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 3"));


        ConcreteTestStep failingStep = (ConcreteTestStep) testOutcome.getTestSteps().get(1);
        assertThat(failingStep.getErrorMessage(), is("Oh bother!"));
    }

    @Test
    public void the_acceptance_test_case_is_failing_if_multiple_tests_have_failed() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_pending_if_at_least_one_test_is_pending_and_none_have_failed() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(pendingTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(PENDING));
    }
    
    @Test
    public void the_acceptance_test_case_is_failing_if_there_is_a_failure_even_with_pending_test_cases() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(pendingTestStepCalled("Step 2"));
        testOutcome.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_ignored_if_all_test_cases_are_ignored() {

        testOutcome.recordStep(ignoredTestStepCalled("Step 1"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(IGNORED));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_failing_tests() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(pendingTestStepCalled("Step 2"));
        testOutcome.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(ignoredTestStepCalled("Step 4"));
        testOutcome.recordStep(successfulTestStepCalled("Step 5"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_pending_tests() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(pendingTestStepCalled("Step 2"));
        testOutcome.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(ignoredTestStepCalled("Step 4"));
        testOutcome.recordStep(successfulTestStepCalled("Step 5"));
        testOutcome.recordStep(pendingTestStepCalled("Step 6"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_successful_tests() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(SUCCESS));
    }
    
    @Test
    public void the_model_should_provide_the_number_of_successful_test_steps() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getSuccessCount(), is(3));
    }

    @Test
    public void the_model_should_provide_the_number_of_successful_test_steps_in_presence_of_other_outcomes() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 5"));

        assertThat(testOutcome.getSuccessCount(), is(2));
    }
  
    @Test
    public void the_model_should_provide_the_number_of_failed_test_steps() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 6"));

        assertThat(testOutcome.getFailureCount(), is(2));
    }

    @Test
    public void the_model_should_provide_the_number_of_ignored_test_steps() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 6"));
        testOutcome.recordStep(skippedTestStepCalled("Step 7"));
        testOutcome.recordStep(skippedTestStepCalled("Step 8"));
        testOutcome.recordStep(skippedTestStepCalled("Step 9"));

        assertThat(testOutcome.getIgnoredCount(), is(1));
    }

    @Test
    public void the_model_should_provide_the_number_of_skipped_test_steps() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 6"));
        testOutcome.recordStep(skippedTestStepCalled("Step 7"));
        testOutcome.recordStep(skippedTestStepCalled("Step 8"));
        testOutcome.recordStep(skippedTestStepCalled("Step 9"));

        assertThat(testOutcome.getSkippedCount(), is(4));
    }

    @Test
    public void the_model_should_provide_the_number_of_pending_test_steps() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.recordStep(failingTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 6"));
        testOutcome.recordStep(pendingTestStepCalled("Step 7"));
        testOutcome.recordStep(pendingTestStepCalled("Step 8"));
        testOutcome.recordStep(pendingTestStepCalled("Step 9"));

        assertThat(testOutcome.getPendingCount(), is(3));
    }
    

    @Test
    public void a_test_run_with_only_successful_tests_is_successful() {

        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_acceptance_test_run_can_contain_steps_nested_in_step_groups() {
        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.endGroup();
        
        assertThat(testOutcome.getTestSteps().size(), is(1));
    }


    private void createNestedTestSteps() {
        testOutcome.recordStep(successfulTestStepCalled("Step 0"));
        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.recordStep(successfulTestStepCalled("Step 5"));
        testOutcome.endGroup();
        testOutcome.endGroup();
    }

    @Test
    public void an_acceptance_test_run_can_contain_step_groups_nested_in_step_groups() {
        createNestedTestSteps();
        assertThat(testOutcome.getTestSteps().size(), is(2));
    }
    
    @Test
    public void when_test_steps_are_nested_step_count_should_include_all_steps() {
        createNestedTestSteps();
        assertThat(testOutcome.countTestSteps(), is(6));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_successful_nested_test_steps() {
        createNestedTestRun();
        assertThat(testOutcome.getSuccessCount(), is(6));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_failing_nested_test_steps() {
        createNestedTestRun();
        
        assertThat(testOutcome.getFailureCount(), is(3));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_pending_nested_test_steps() {
        createNestedTestRun();
        
        assertThat(testOutcome.getPendingCount(), is(4));
    }
    
    @Test
    public void an_acceptance_test_run_can_count_all_the_ignored_nested_test_steps() {
        createNestedTestRun();
        
        assertThat(testOutcome.getIgnoredCount(), is(1));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_skipped_test_steps() {
        createNestedTestRun();
        
        assertThat(testOutcome.getSkippedCount(), is(1));
    }

    private void createNestedTestRun() {
        testOutcome.recordStep(successfulTestStepCalled("Step 0"));
        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.recordStep(failingTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testOutcome.recordStep(pendingTestStepCalled("Step 10"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.recordStep(successfulTestStepCalled("Step 5"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 6"));
        testOutcome.recordStep(failingTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 8", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 9"));
        testOutcome.recordStep(pendingTestStepCalled("Step 10"));
        testOutcome.recordStep(pendingTestStepCalled("Step 11"));
        testOutcome.recordStep(pendingTestStepCalled("Step 12"));
        testOutcome.endGroup();
        testOutcome.endGroup();
    }

    @Test
    public void a_test_group_with_only_successful_tests_is_successful() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void a_test_group_with_a_failing_test_fails() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(failingTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 4"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 5"));
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.FAILURE));
    }

    
    @Test
    public void a_test_group_with_a_pending_test_is_pending() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(pendingTestStepCalled("Step 3"));
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.PENDING));
    }
    
    @Test
    public void a_test_group_with_only_ignored_tests_is_ignored() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(ignoredTestStepCalled("Step 1"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        testOutcome.endGroup();
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_test_run_with_a_nested_group_containing_a_failure_is_a_failure() {
        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.endGroup();
        testOutcome.endGroup();
        
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_test_group_with_a_nested_group_containing_a_failure_is_a_failure() {
        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.recordStep(failingTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.endGroup();
        testOutcome.endGroup();
       
        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.FAILURE));
    }

    class MyApp {
        class MyUserStory {}
    }

    @Test
    public void an_acceptance_test_relates_to_a_user_story() {
        Story story = Story.from(MyApp.MyUserStory.class);
        TestOutcome testOutcome = TestOutcome.forTestInStory("some_test", story);

        assertThat(testOutcome.getUserStory().getName(), is("My user story"));
    }
    
    @Test
    public void an_acceptance_test_records_the_original_story_class() {
        Story story = Story.from(MyApp.MyUserStory.class);
        TestOutcome testOutcome = TestOutcome.forTestInStory("some_test", story);
        assertThat(testOutcome.getUserStory().getUserStoryClass().getName(), is(MyApp.MyUserStory.class.getName()));
    }

    @Test
    public void we_can_record_the_lifetime_of_a_test_run() throws InterruptedException {
        Thread.sleep(10);
        testOutcome.recordDuration();
        assertThat(testOutcome.getDuration(), is(greaterThanOrEqualTo(10L)));
        assertThat(testOutcome.getDuration(), is(lessThan(100L)));
    }

    @TestsStory(AUserStory.class)
    class TestScenarioWithRequirements {
        @TestsRequirement("SOME_BUSINESS_RULE_1")
        public void should_do_this() {};
        @TestsRequirements({"SOME_BUSINESS_RULE_1","SOME_BUSINESS_RULE_2"})
        public void should_do_that() {};
    }

    @Test
    public void should_automatically_record_a_requirement_declared_for_the_test() {

        testOutcome = TestOutcome.forTest("should_do_this", TestScenarioWithRequirements.class);

        Assert.assertThat(testOutcome.getAllTestedRequirements(), hasItem("SOME_BUSINESS_RULE_1"));

    }

    @Test
    public void should_automatically_record_all_requirements_declared_for_the_test() {

        testOutcome = TestOutcome.forTest("should_do_that", TestScenarioWithRequirements.class);

        Assert.assertThat(testOutcome.getAllTestedRequirements(),
                          hasItems("SOME_BUSINESS_RULE_1", "SOME_BUSINESS_RULE_2"));

    }

    @Test
    public void should_get_report_filename_from_the_story_name_and_method_name() {
        testOutcome = TestOutcome.forTest("should_do_that", TestScenarioWithRequirements.class);

        assertThat(testOutcome.getReportName(), is("a_user_story_should_do_that"));
    }

    @Test
    public void parametrized_test_report_names_should_strip_any_indexes() {
        testOutcome = TestOutcome.forTest("should_do_that[0]", TestScenarioWithRequirements.class);

        assertThat(testOutcome.getReportName(), is("a_user_story_should_do_that"));
    }

}
