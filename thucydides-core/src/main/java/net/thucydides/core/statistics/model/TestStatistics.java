package net.thucydides.core.statistics.model;

import ch.lambdaj.Lambda;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.model.TestResult;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.is;

public class TestStatistics {

    private final Long totalTestRuns;
    private final Long passingTestRuns;
    private final Long failingTestRuns;
    private final List<TestResult> testResults;
    private final List<TestRunTag> tags;
    private static final int OVERALL = Integer.MAX_VALUE;

    public TestStatistics(Long totalTestRuns,
                          Long passingTestRuns,
                          Long failingTestRuns,
                          List<TestResult> testResults,
                          List<TestRunTag> tags) {
        this.totalTestRuns = totalTestRuns;
        this.passingTestRuns = passingTestRuns;
        this.failingTestRuns = failingTestRuns;
        this.tags = ImmutableList.copyOf(tags);
        this.testResults = ImmutableList.copyOf(testResults);
    }

    public Long getTotalTestRuns() {
        return totalTestRuns;
    }

    public Long getPassingTestRuns() {
        return passingTestRuns;
    }

    public Long getFailingTestRuns() {
        return failingTestRuns;
    }

    public Double getOverallPassRate() {
        if (totalTestRuns > 0) {
            return (double) passingTestRuns / (double) totalTestRuns;
        } else {
            return 0.0;
        }
    }

    public List<TestRunTag> getTags() {
        return tags;
    }

    public PassRateBuilder getPassRate() {
        return new PassRateBuilder(OVERALL);
    }

    public class PassRateBuilder {

        int testRunsOverPeriod;

        public PassRateBuilder(int testRunsOverPeriod) {
            this.testRunsOverPeriod = testRunsOverPeriod;
        }

        public PassRateBuilder overTheLast(int number) {
            return new PassRateBuilder(number);
        }

        public Double testRuns() {
            int successfulRecentTestRuns = countSuccessfulTestRunsInLast(testRunsOverPeriod, testResults);
            int eligableTestRunCount = (testResults.size() < testRunsOverPeriod) ? testResults.size() : testRunsOverPeriod;
            return (successfulRecentTestRuns * 1.0) / (eligableTestRunCount * 1.0);
        }

        private int countSuccessfulTestRunsInLast(int testRunCount, List<TestResult> testResults) {
            List<TestResult> eligableTestResults = mostRecent(testRunCount, testResults);
            List<TestResult> successfulTestResults = select(eligableTestResults, is(TestResult.SUCCESS));
            return successfulTestResults.size();
        }

        private List<TestResult> mostRecent(int testRunsOverPeriod, List<TestResult> testResults) {
            int eligableCount = eligableTestResultSize(testResults, testRunsOverPeriod);
            return testResults.subList(0, eligableCount);
        }

        private int eligableTestResultSize(List<TestResult> testResults, int testRunsOverPeriod) {
            return (testRunsOverPeriod > testResults.size()) ? testResults.size() : testRunsOverPeriod;
        }

    }
}
