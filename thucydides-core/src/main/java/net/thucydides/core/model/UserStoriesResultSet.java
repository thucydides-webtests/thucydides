package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A collection of user story test results
 */
public class UserStoriesResultSet {

    private final List<UserStoryTestResults> stories;

    public UserStoriesResultSet(List<UserStoryTestResults> stories) {
        this.stories = stories;
    }

    public List<UserStoryTestResults> getStories() {
        return ImmutableList.copyOf(stories);
    }

    public int getStoryCount() {
        return stories.size();
    }

    public int getAcceptanceTestCount() {
        int count = 0;
        for(UserStoryTestResults story : stories) {
            count += story.getTotal();
        }
        return count;
    }

    public int getSuccessCount() {
        int count = 0;
        for(UserStoryTestResults story : stories) {
            count += story.getSuccessCount();
        }
        return count;
    }

    public int getFailureCount() {
        int count = 0;
        for(UserStoryTestResults story : stories) {
            count += story.getFailureCount();
        }
        return count;
    }

    public int getPendingCount() {
        int count = 0;
        for(UserStoryTestResults story : stories) {
            count += story.getPendingCount();
        }
        return count;
    }
}
