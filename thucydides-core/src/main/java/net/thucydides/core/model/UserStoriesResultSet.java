package net.thucydides.core.model;

import java.util.List;

/**
 * A collection of user story test results.
 */
public class UserStoriesResultSet {

    private final List<StoryTestResults> stories;

    public UserStoriesResultSet(final List<StoryTestResults> stories) {
        this.stories = stories;
    }

    public int getStoryCount() {
        return stories.size();
    }

    public int getTotalTestCount() {

        int count = 0;
        for(StoryTestResults story : stories) {
            count += story.getTotal();
        }
        return count;
    }

    public int getSuccessCount() {

        int count = 0;
        for(StoryTestResults story : stories) {
            count += story.getSuccessCount();
        }
        return count;
    }

    public int getFailureCount() {
        int count = 0;
        for(StoryTestResults story : stories) {
            count += story.getFailureCount();
        }
        return count;
    }

    public int getPendingCount() {
        int count = 0;
        for(StoryTestResults story : stories) {
            count += story.getPendingCount();
        }
        return count;
    }
}
