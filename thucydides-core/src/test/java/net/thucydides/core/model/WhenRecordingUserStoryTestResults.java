package net.thucydides.core.model;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WhenRecordingUserStoryTestResults {

    private UserStory userStory = new UserStory("Some user story","","");
    private UserStoryTestResults userStoryTestResults;

    @Before
    public void init() {
        userStoryTestResults = new UserStoryTestResults(userStory);
    }

    @Test
    public void a_user_story_test_result_contain_a_set_of_test_runs() {
        AcceptanceTestRun testRun1 = thatFailsCalled("Test Run 1");
        AcceptanceTestRun testRun2 = thatSucceedsCalled("Test Run 2");

        userStoryTestResults.recordTestRun(testRun1);
        userStoryTestResults.recordTestRun(testRun2);

        Assert.assertThat(userStoryTestResults.getTotal(), is(2));
        Assert.assertThat(userStoryTestResults.getTestRuns(), allOf(hasItem(testRun1), hasItem(testRun2)));
    }
    
    @Test
    public void a_user_story_is_successful_if_all_tests_are_successful() {
        AcceptanceTestRun testRun1 = thatSucceedsCalled("Test Run 1");
        AcceptanceTestRun testRun2 = thatSucceedsCalled("Test Run 2");

        userStoryTestResults.recordTestRun(testRun1);
        userStoryTestResults.recordTestRun(testRun2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.SUCCESS));
    }

    
    @Test
    public void a_user_story_fails_if_at_least_one_test_fails() {
        AcceptanceTestRun testRun1 = thatSucceedsCalled("Test Run 1");
        AcceptanceTestRun testRun2 = thatFailsCalled("Test Run 2");

        userStoryTestResults.recordTestRun(testRun1);
        userStoryTestResults.recordTestRun(testRun2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_user_story_is_pending_if_at_least_one_test_is_pending() {
        AcceptanceTestRun testRun1 = thatSucceedsCalled("Test Run 1");
        AcceptanceTestRun testRun2 = thatIsPendingCalled("Test Run 2");

        userStoryTestResults.recordTestRun(testRun1);
        userStoryTestResults.recordTestRun(testRun2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_user_story_is_ignored_if_all_tests_are_ignored() {
        AcceptanceTestRun testRun1 = thatIsIgnoredCalled("Test Run 2");
        AcceptanceTestRun testRun2 = thatIsIgnoredCalled("Test Run 2");

        userStoryTestResults.recordTestRun(testRun1);
        userStoryTestResults.recordTestRun(testRun2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_failed_test_runs() {

        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 2"));

        Assert.assertThat(userStoryTestResults.getFailureCount(), is(2));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_successful_test_runs() {

        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        userStoryTestResults.recordTestRun(thatSucceedsCalled("Test Run 2"));
        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 3"));

        Assert.assertThat(userStoryTestResults.getFailureCount(), is(2));
        Assert.assertThat(userStoryTestResults.getSuccessCount(), is(1));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_pending_test_runs() {

        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        userStoryTestResults.recordTestRun(thatSucceedsCalled("Test Run 2"));
        userStoryTestResults.recordTestRun(thatIsPendingCalled("Test Run 3"));
        userStoryTestResults.recordTestRun(thatIsPendingCalled("Test Run 4"));
        userStoryTestResults.recordTestRun(thatIsPendingCalled("Test Run 5"));

        Assert.assertThat(userStoryTestResults.getPendingCount(), is(3));
    }
    
    private AcceptanceTestRun thatFailsCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 3"));
        return testRun;
    }
    
    private AcceptanceTestRun thatSucceedsCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        return testRun;
    }
    
    private AcceptanceTestRun thatIsPendingCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(pendingTestStepCalled("Step 2"));
        testRun.recordStep(pendingTestStepCalled("Step 3"));
        testRun.recordStep(pendingTestStepCalled("Step 4"));
        testRun.recordStep(pendingTestStepCalled("Step 5"));
        return testRun;
    }
    
    private AcceptanceTestRun thatIsIgnoredCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(ignoredTestStepCalled("Step 1"));
        testRun.recordStep(ignoredTestStepCalled("Step 2"));
        testRun.recordStep(ignoredTestStepCalled("Step 3"));
        return testRun;
    }

}
