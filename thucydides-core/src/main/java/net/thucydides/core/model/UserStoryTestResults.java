package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.function.convert.Converter;

import com.google.common.collect.ImmutableList;

/**
 * A collection of test results, corresponding to a the acceptance tests for a single user story.
 * 
 * @author johnsmart
 * 
 */
public class UserStoryTestResults {

    private List<AcceptanceTestRun> testRuns;
    
    private final String title;
    
    private UserStory userStory;
    
    /**
     * Create a new acceptance test run instance.
     */
    public UserStoryTestResults(final UserStory someUserStory) {
        testRuns = new ArrayList<AcceptanceTestRun>();
        this.title = someUserStory.getName();
        this.userStory = someUserStory;
    }

    public UserStory getUserStory() {
        return userStory;
    }
    
    public void setUserStory(final UserStory userStory) {
        this.userStory = userStory;
    }

    public String getReportName(final ReportNamer.ReportType type) {
        ReportNamer reportNamer = new ReportNamer(type);
        return reportNamer.getNormalizedTestNameFor(getUserStory());
    }

    public String getReportName() {
        return getReportName(ROOT);
    }
    
    /**
     * Add a test run result to the aggregate set of results.
     */
    public void recordTestRun(final AcceptanceTestRun testRun) {
        testRuns.add(testRun);
    }

    /**
     * How many test runs in total have been recorded.
     *
     */
    public int getTotal() {
       return testRuns.size();
    }

    /**
     * How many test cases contain at least one failing test.
     */
    public int getFailureCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isFailure())).size();
    }

    /**
     * How many test cases contain only successful or ignored tests.
     */
    public int getSuccessCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isSuccess())).size();
    }

    public Integer getPendingCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isPending())).size();
    }

    public List<AcceptanceTestRun> getTestRuns() {
        return ImmutableList.copyOf(testRuns);
    }

    public String getTitle() {
        return title;
    }

    private static class ExtractTestResultsConverter implements Converter<AcceptanceTestRun, TestResult> {
        public TestResult convert(final AcceptanceTestRun step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(getTestRuns(), new ExtractTestResultsConverter());
    }

    
    public TestResult getResult() {
        List<TestResult> allTestResults = getCurrentTestResults();

        if (allTestResults.contains(FAILURE)) {
            return FAILURE;
        }

        if (allTestResults.contains(PENDING)) {
            return PENDING;
        }

        if (containsOnly(allTestResults, IGNORED)) {
            return IGNORED;
        }

        return SUCCESS;
    }

    private boolean containsOnly(final List<TestResult> testResults, final TestResult value) {
        for (TestResult result : testResults) {
            if (result != value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Does this set of test results correspond to a specified user story?
     */
    public boolean containsResultsFor(final UserStory aUserStory) {
        if (getUserStory() != null) {
            return getUserStory().equals(aUserStory);
        } else {
            return false;
        }
    }
}
