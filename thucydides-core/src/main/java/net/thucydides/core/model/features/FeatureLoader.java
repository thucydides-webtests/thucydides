package net.thucydides.core.model.features;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.userstories.UserStoryLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Load a set of test results organized into stories and features, and return them as a list of features.
 */
public class FeatureLoader {

    public FeatureLoader() {
        this.userStoriesLoader = new UserStoryLoader();
    }

    private UserStoryLoader userStoriesLoader;

    public void setUserStoriesLoader(final UserStoryLoader userStoriesLoader) {
        this.userStoriesLoader = userStoriesLoader;
    }

    public UserStoryLoader getUserStoriesLoader() {
        return userStoriesLoader;
    }

    public List<FeatureResults> loadFrom(final File resultsDirectory) throws IOException {
        List<FeatureResults> results = new ArrayList<FeatureResults>();

        List<StoryTestResults> stories = getUserStoriesLoader().loadFrom(resultsDirectory);
        for(StoryTestResults storyResult : stories) {
            updateFeatureResults(results, storyResult);
        }

        return results;
    }

    private void updateFeatureResults(final List<FeatureResults> results, final StoryTestResults storyResult) {
        ApplicationFeature feature = storyResult.getStory().getFeature();
        if (feature != null) {
            FeatureResults featureResults = featureResultsFor(feature, results);
            featureResults.recordStoryResults(storyResult);
        }
    }

    private FeatureResults featureResultsFor(final ApplicationFeature feature, final List<FeatureResults> results) {
        FeatureResults matchingFeatureResults = null;
        for (FeatureResults featureResult : results) {
            if (featureResult.getFeature().equals(feature)) {
                matchingFeatureResults = featureResult;
                break;
            }
        }
        if (matchingFeatureResults == null) {
            matchingFeatureResults = new FeatureResults(feature);
            results.add(matchingFeatureResults);
        }
        return matchingFeatureResults;
    }
}
