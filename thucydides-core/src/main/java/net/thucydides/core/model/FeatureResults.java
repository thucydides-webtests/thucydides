package net.thucydides.core.model;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.model.features.ApplicationFeature;

import com.google.common.collect.ImmutableList;

/**
 * A set of test results related to a given feature.
 */
public class FeatureResults {
    private final ApplicationFeature feature;

    private List<StoryTestResults> storyTestResultsList;

    private ReportNamer namer;

    public FeatureResults(final ApplicationFeature feature) {
        this.feature = feature;
        this.namer = new ReportNamer(ReportNamer.ReportType.HTML);
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

    public Integer getPendingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getPendingCount())).intValue();
    }

    public Integer getTotalSteps() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getStepCount())).intValue();
    }

    public Integer getEstimatedTotalSteps() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getEstimatedTotalStepCount())).intValue();
    }

    public double getCoverage() {
        if (getEstimatedTotalSteps() == 0) {
            return 0.0;
        }

        int coveredStepCount = 0;
        for(StoryTestResults story : storyTestResultsList) {
            double storyCoverage = story.getCoverage();
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

    public String getStoryReportName() {
        return "stories_" + namer.getNormalizedTestNameFor(feature);
    }

    public int countStepsInSuccessfulTests() {
        if (storyTestResultsList.size() == 0) {
            return 0;
        }
        return sum(storyTestResultsList, on(StoryTestResults.class).countStepsInSuccessfulTests());
    }

    public Double getPercentCoverage() {
        return getCoverage() * 100;
    }
}
