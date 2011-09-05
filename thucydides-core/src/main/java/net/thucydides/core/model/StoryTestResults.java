package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sum;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.function.convert.Converter;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.ThucydidesSystemProperty;

/**
 * A collection of test results, corresponding to a the acceptance tests for a single user story.
 * User stories can in turn belong to Features.
 * 
 * @author johnsmart
 * 
 */
public class StoryTestResults {

    private List<TestOutcome> testOutcomes;
    
    private final String title;
    
    private final Story story;

    public final Integer DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT = 5;

    /**
     * Create a new acceptance test run instance.
     */
    public StoryTestResults(final Story story) {
        testOutcomes = new ArrayList<TestOutcome>();
        this.title = story.getName();
        this.story = story;
    }

    public long getDuration() {
        return sum(testOutcomes, on(TestOutcome.class).getDuration());
    }

    public Story getStory() {
        return story;
    }

    public String getReportName(final ReportNamer.ReportType type) {
        ReportNamer reportNamer = new ReportNamer(type);
        return reportNamer.getNormalizedTestNameFor(getStory());
    }

    public String getReportName() {
        return getReportName(ROOT);
    }

    /**
     * Add a test run result to the aggregate set of results.
     */
    public void recordTestRun(final TestOutcome testOutcome) {
        testOutcomes.add(testOutcome);
    }

    /**
     * How many test runs in total have been recorded.
     *
     */
    public int getTotal() {
       return testOutcomes.size();
    }

    /**
     * How many test cases contain at least one failing test.
     */
    public int getFailureCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isFailure())).size();
    }

    /**
     * How many test cases contain only successful or ignored tests.
     */
    public int getSuccessCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isSuccess())).size();
    }

    /**
     * How many steps make up the successful tests?
     */
    public int countStepsInSuccessfulTests() {
        List<TestOutcome> successfulTests = select(testOutcomes, having(on(TestOutcome.class).isSuccess()));
        try {
            return (successfulTests.isEmpty()) ? 0 : sum(successfulTests, on(TestOutcome.class).getNestedStepCount());
        } catch(Exception e) {
            return 0;
        }
    }

    /**
     * How steps make up the pending tests
     */
    public int countStepsInFailingTests() {
        List<TestOutcome> pendingTests = select(testOutcomes, having(on(TestOutcome.class).isFailure()));
        try {
            return (pendingTests.isEmpty()) ? 0 : sum(pendingTests, on(TestOutcome.class).getNestedStepCount());
        } catch(Exception e) {
            return 0;
        }
    }

    public int getPendingCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isPending())).size();
    }

    public List<TestOutcome> getTestOutcomes() {
        return ImmutableList.copyOf(testOutcomes);
    }

    public String getTitle() {
        return capitalize(title);
    }

    public int getStepCount() {
        return sum(extract(testOutcomes, on(TestOutcome.class).getNestedStepCount())).intValue();
    }

    public Double getCoverage() {
        if (getEstimatedTotalStepCount() == 0) {
            return 0.0;
        }
        return passingOrFailingSteps() / (double) getEstimatedTotalStepCount();
    }

    public int getEstimatedTotalStepCount() {
        return (getStepCount() + estimatedUnimplementedStepCount());
    }

    private int estimatedUnimplementedStepCount() {
        return (int) (getAverageTestSize() * totalUnimplementedTests());
    }

    private int passingOrFailingSteps() {
        return countStepsInSuccessfulTests() + countStepsInFailingTests();
    }

    public double getAverageTestSize() {
        if (totalImplementedTests() > 0) {
            return ((double) getStepCount()) / totalImplementedTests();
        } else {
            return ThucydidesSystemProperty.getIntegerValue(ThucydidesSystemProperty.ESTIMATED_AVERAGE_STEP_COUNT,
                    DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT);
        }
    }

    private int totalUnimplementedTests() {
        return getTotal() - totalImplementedTests();
    }

    private int totalImplementedTests() {
       int testCount = 0;
       for(TestOutcome testOutcome : testOutcomes) {
           if (!testOutcome.getTestSteps().isEmpty()) {
               testCount++;
           }
       }
       return testCount;
    }

    public Double getPercentCoverage() {
        return getCoverage() * 100;
    }

    private static class ExtractTestResultsConverter implements Converter<TestOutcome, TestResult> {
        public TestResult convert(final TestOutcome step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(getTestOutcomes(), new ExtractTestResultsConverter());
    }

    
    public TestResult getResult() {
        TestResultList testResults = new TestResultList(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    /**
     * Does this set of test results correspond to a specified user story?
     */
    public boolean containsResultsFor(final Story aUserStory) {
        return getStory().equals(aUserStory);
    }
}
