package net.thucydides.core.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.webdriver.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sort;
import static ch.lambdaj.Lambda.sum;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import static org.apache.commons.lang3.StringUtils.capitalize;
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

    private final Configuration configuration;

    private final Formatter formatter;
    /**
     * Create a new acceptance test run instance.
     */
    public StoryTestResults(final Story story) {
        this(story,
             Injectors.getInjector().getInstance(Configuration.class),
             Injectors.getInjector().getInstance(IssueTracking.class));
    }

    /**
     * Create a new acceptance test run instance.
     */
    public StoryTestResults(final Story story,
                            Configuration configuration,
                            IssueTracking issueTracking) {
        this.configuration = configuration;
        testOutcomes = new ArrayList<TestOutcome>();
        this.title = story.getName();
        this.story = story;
        formatter = new Formatter(issueTracking);
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


    public Integer getSkipCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isSkipped())).size();
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

    public int countStepsInFailingTests() {
        List<TestOutcome> tests = select(testOutcomes, having(on(TestOutcome.class).isFailure()));
        try {
            return (tests.isEmpty()) ? 0 : sum(tests, on(TestOutcome.class).getNestedStepCount());
        } catch(Exception e) {
            return 0;
        }
    }

    public int countStepsInSkippedTests() {
        List<TestOutcome> tests = select(testOutcomes, having(on(TestOutcome.class).isSkipped()));
        try {
            return (tests.isEmpty()) ? 0 : sum(tests, on(TestOutcome.class).getNestedStepCount());
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

    public String getTitleWithLinks() {
        return getFormatter().addLinks(getTitle());
    }

    public String getFormattedIssues() {
        if (!getIssues().isEmpty()) {
           List<String> orderedIssues =  sort(getIssues(), on(String.class));
           return "(" + getFormatter().addLinks(StringUtils.join(orderedIssues, ", ")) + ")";
        } else {
            return "";
        }
    }

    public Set<String> getIssues() {
        List<String> allIssues = flatten(extract(testOutcomes, on(TestOutcome.class).getIssues()));
        return new HashSet<String>(allIssues);
    }

    private Formatter getFormatter() {
        return formatter;
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

    public Double getPercentPassingCoverage() {
        if (getEstimatedTotalStepCount() == 0) {
            return 0.0;
        }
        return (countStepsInSuccessfulTests() / (double) getEstimatedTotalStepCount());
    }

    public Double getPercentFailingCoverage() {
        if (getEstimatedTotalStepCount() == 0) {
            return 0.0;
        }
        return (countStepsInFailingTests() / (double) getEstimatedTotalStepCount());
    }

    public Double getPercentPendingCoverage() {
        if (getEstimatedTotalStepCount() == 0) {
            return 0.0;
        }
        return ((getEstimatedTotalStepCount() - passingOrFailingSteps()) / (double) getEstimatedTotalStepCount());
    }

    public Integer getEstimatedTotalStepCount() {
        return (getStepCount() + estimatedUnimplementedStepCount());
    }

    private Integer estimatedUnimplementedStepCount() {
        return (int) (Math.round(getAverageTestSize() * totalUnimplementedTests()));
    }

    private int passingOrFailingSteps() {
        return countStepsInSuccessfulTests() + countStepsInFailingTests();
    }

    public double getAverageTestSize() {
        if (totalImplementedTests() > 0) {
            return ((double) getStepCount()) / totalImplementedTests();
        } else {
            return configuration.getEstimatedAverageStepCount();
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

    private static class ExtractTestResultsConverter implements Converter<TestOutcome, TestResult> {
        public TestResult convert(final TestOutcome step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(getTestOutcomes(), new ExtractTestResultsConverter());
    }

    
    public TestResult getResult() {
        TestResultList testResults = TestResultList.of(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    /**
     * Does this set of test results correspond to a specified user story?
     */
    public boolean containsResultsFor(final Story aUserStory) {
        return getStory().equals(aUserStory);
    }

    public CoverageFormatter getFormatted() {
        return new CoverageFormatter(getPercentPassingCoverage(),
                                     getPercentPendingCoverage(),
                                     getPercentFailingCoverage());
    }

}
