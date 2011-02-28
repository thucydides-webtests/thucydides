package net.thucydides.core.model;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WhenRecordingAggregateTestResults {

    private AggregateTestResults aggregateTestResults;

    @Before
    public void init() {
        aggregateTestResults = new AggregateTestResults("User Story 1");
    }

    @Test
    public void an_aggregate_test_result_contain_a_set_of_test_runs() {
        AcceptanceTestRun testRun1 = thatFailsCalled("Test Run 1");
        AcceptanceTestRun testRun2 = thatSucceedsCalled("Test Run 2");

        aggregateTestResults.recordTestRun(testRun1);
        aggregateTestResults.recordTestRun(testRun2);

        Assert.assertThat(aggregateTestResults.getTotal(), is(2));
        Assert.assertThat(aggregateTestResults.getTestRuns(), allOf(hasItem(testRun1), hasItem(testRun2)));
    }
    

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_failed_test_runs() {

        aggregateTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        aggregateTestResults.recordTestRun(thatFailsCalled("Test Run 2"));

        Assert.assertThat(aggregateTestResults.getFailureCount(), is(2));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_successful_test_runs() {

        aggregateTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        aggregateTestResults.recordTestRun(thatSucceedsCalled("Test Run 2"));
        aggregateTestResults.recordTestRun(thatFailsCalled("Test Run 3"));

        Assert.assertThat(aggregateTestResults.getFailureCount(), is(2));
        Assert.assertThat(aggregateTestResults.getSuccessCount(), is(1));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_pending_test_runs() {

        aggregateTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        aggregateTestResults.recordTestRun(thatSucceedsCalled("Test Run 2"));
        aggregateTestResults.recordTestRun(thatIsPendingCalled("Test Run 3"));
        aggregateTestResults.recordTestRun(thatIsPendingCalled("Test Run 4"));
        aggregateTestResults.recordTestRun(thatIsPendingCalled("Test Run 5"));

        Assert.assertThat(aggregateTestResults.getPendingCount(), is(3));
    }

    private AcceptanceTestRun thatFailsCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2"));
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
}
