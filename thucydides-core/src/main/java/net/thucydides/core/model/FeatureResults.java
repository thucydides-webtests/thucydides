package net.thucydides.core.model;

import net.thucydides.core.model.features.ApplicationFeature;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

/**
 * A set of test results related to a given feature.
 */
public class FeatureResults {
    private final ApplicationFeature feature;

    private List<StoryTestResults> storyTestResultsList;

    public FeatureResults(final ApplicationFeature feature) {
        this.feature = feature;
        storyTestResultsList = new ArrayList<StoryTestResults>();
    }

    public ApplicationFeature getFeature() {
        return feature;
    }

    public void recordStoryResults(final StoryTestResults storyResults) {
        storyTestResultsList.add(storyResults);
    }

    public Integer getTotalTests() {
        return (Integer) sum(extract(storyTestResultsList, on(StoryTestResults.class).getTotal()));
    }

    public Integer getPassingTests() {
        return (Integer) sum(extract(storyTestResultsList, on(StoryTestResults.class).getSuccessCount()));
    }

    public Integer getFailingTests() {
        return (Integer) sum(extract(storyTestResultsList, on(StoryTestResults.class).getFailureCount()));
    }

    public Integer getPendingTests() {
        return (Integer) sum(extract(storyTestResultsList, on(StoryTestResults.class).getPendingCount()));
    }

    public Integer getTotalSteps() {
        return (Integer) sum(extract(storyTestResultsList, on(StoryTestResults.class).getStepCount()));
    }

    public Integer getTotalStories() {
        return storyTestResultsList.size();
    }
}
