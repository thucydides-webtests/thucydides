package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.features.ApplicationFeature;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * A set of test results related to a given feature.
 */
public class FeatureResults {
    private final ApplicationFeature feature;

    private List<StoryTestResults> storyTestResultsList;

    private ReportNamer namer;

    public FeatureResults(final ApplicationFeature feature) {
        this.feature = feature;
        this.namer = ReportNamer.forReportType(ReportType.HTML);
        storyTestResultsList = new ArrayList<StoryTestResults>();
    }

    public ApplicationFeature getFeature() {
        return feature;
    }

    public void recordStoryResults(final StoryTestResults storyResults) {
        storyTestResultsList.add(storyResults);
    }

    public Integer getTotalTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getTotal())).intValue();
    }

    public Integer getPassingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getSuccessCount())).intValue();
    }

    public Integer getFailingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getFailureCount())).intValue();
    }

//    public Integer getPassingSteps() {
//        return sum(extract(storyTestResultsList, on(StoryTestResults.class).countStepsInSuccessfulTests())).intValue();
//    }
//
//    public Integer getFailingSteps() {
//        return sum(extract(storyTestResultsList, on(StoryTestResults.class).countStepsInFailingTests())).intValue();
//    }
//
//    public Integer getSkippedSteps() {
//        return sum(extract(storyTestResultsList, on(StoryTestResults.class).countStepsInSkippedTests())).intValue();
//    }

    public Integer getSkippedTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getSkipCount())).intValue();
    }

    public Integer getPendingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getPendingCount())).intValue();
    }

    public Integer getTotalSteps() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getStepCount())).intValue();
    }

    public Integer getEstimatedTotalSteps() {
        List<Integer> stepCounts = extract(storyTestResultsList, on(StoryTestResults.class).getEstimatedTotalStepCount());
        return sum(stepCounts, on(Integer.class).intValue());
    }

    public double getCoverage() {
        if (getEstimatedTotalSteps() == 0) {
            return 0.0;
        }

        int coveredStepCount = 0;
        for(StoryTestResults story : storyTestResultsList) {
            coveredStepCount += (story.getCoverage() * story.getEstimatedTotalStepCount());
        }
        return ((double) coveredStepCount) / getEstimatedTotalSteps();
    }

    public Integer getTotalStories() {
        return storyTestResultsList.size();
    }

    public List<StoryTestResults> getStoryResults() {
        return ImmutableList.copyOf(storyTestResultsList);
    }

//    public String getStoryReportName() {
//        return "stories_" + namer.getNormalizedTestNameFor(feature);
//    }

    public int countStepsInSuccessfulTests() {
        if (storyTestResultsList.size() == 0) {
            return 0;
        }
        return sum(storyTestResultsList, on(StoryTestResults.class).countStepsInSuccessfulTests());
    }

    public Double getPercentPassingCoverage() {
        if (getEstimatedTotalSteps() == 0) {
            return 0.0;
        }

        int stepCount = 0;
        for(StoryTestResults story : storyTestResultsList) {
            stepCount += (story.getPercentPassingCoverage() * story.getEstimatedTotalStepCount());
        }
        return ((double) stepCount) / getEstimatedTotalSteps();
    }

    public Double getPercentFailingCoverage() {
        if (getEstimatedTotalSteps() == 0) {
            return 0.0;
        }

        int stepCount = 0;
        for(StoryTestResults story : storyTestResultsList) {
            stepCount += (story.getPercentFailingCoverage() * story.getEstimatedTotalStepCount());
        }
        return ((double) stepCount) / getEstimatedTotalSteps();
    }

    public Double getPercentErrorCoverage() {
        if (getEstimatedTotalSteps() == 0) {
            return 0.0;
        }

        int stepCount = 0;
        for(StoryTestResults story : storyTestResultsList) {
            stepCount += (story.getPercentErrorCoverage() * story.getEstimatedTotalStepCount());
        }
        return ((double) stepCount) / getEstimatedTotalSteps();
    }

    public Double getPercentPendingCoverage() {
        if (getEstimatedTotalSteps() == 0) {
            return 0.0;
        }

        int stepCount = 0;
        for(StoryTestResults story : storyTestResultsList) {
            stepCount += (story.getPercentPendingCoverage() * story.getEstimatedTotalStepCount());
        }
        return ((double) stepCount) / getEstimatedTotalSteps();
    }

    public CoverageFormatter getFormatted() {
        return new CoverageFormatter(getPercentPassingCoverage(),
                                     getPercentPendingCoverage(),
                                     getPercentFailingCoverage(),
                                     getPercentErrorCoverage());
    }

    public TestResult getResult() {
        TestResultList testResults = TestResultList.of(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    private List<TestResult> getCurrentTestResults() {
        return extract(storyTestResultsList, on(StoryTestResults.class).getResult());
    }

}
