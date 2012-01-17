package net.thucydides.plugins.jira;

import net.thucydides.core.model.TestResult;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenTrackingTestResultsForAnIssue {

    @Test
    public void should_record_the_result_for_a_given_test() {
        TestResultTally resultTally = new TestResultTally();

        resultTally.recordResult("ISSUE-1", TestResult.SUCCESS);

        TestResult recordedResult = resultTally.getResultForIssue("ISSUE-1");

        assertThat(recordedResult, is(TestResult.SUCCESS));
    }

    @Test
    public void should_record_the_overall_result_for_a_given_test() {
        TestResultTally resultTally = new TestResultTally();

        resultTally.recordResult("ISSUE-1", TestResult.SUCCESS);
        resultTally.recordResult("ISSUE-1", TestResult.FAILURE);
        resultTally.recordResult("ISSUE-1", TestResult.SUCCESS);

        TestResult recordedResult = resultTally.getResultForIssue("ISSUE-1");

        assertThat(recordedResult, is(TestResult.FAILURE));
    }
    
    @Test
    public void should_list_tallied_issues() {
        TestResultTally resultTally = new TestResultTally();

        resultTally.recordResult("ISSUE-1", TestResult.SUCCESS);
        resultTally.recordResult("ISSUE-2", TestResult.FAILURE);
        resultTally.recordResult("ISSUE-1", TestResult.SUCCESS);

        assertThat(resultTally.getIssues(), hasItems("ISSUE-1", "ISSUE-2"));
    }

}
