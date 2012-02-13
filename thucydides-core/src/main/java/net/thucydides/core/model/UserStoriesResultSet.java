package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

/**
 * A collection of user story test results.
 */
public class UserStoriesResultSet {

    private final List<StoryTestResults> stories;

    public UserStoriesResultSet(final List<StoryTestResults> stories) {
        this.stories = stories;
    }

    public List<StoryTestResults> getStories() {
        return ImmutableList.copyOf(stories);
    }
    
    public int getStoryCount() {
        return stories.size();
    }

    public int getTotalTestCount() {
        return sum(stories, on(StoryTestResults.class).getTotal());
    }

    public int getSuccessCount() {

        return sum(stories, on(StoryTestResults.class).getSuccessCount());
    }

    public int getFailureCount() {
        return sum(stories, on(StoryTestResults.class).getFailureCount());
    }

    public int getPendingCount() {
        return sum(stories, on(StoryTestResults.class).getPendingCount());
    }

    public Integer getTotalStepCount() {
        return sum(stories, on(StoryTestResults.class).getEstimatedTotalStepCount());
    }

    public Integer getPassingStepCount() {
        return sum(stories, on(StoryTestResults.class).countStepsInSuccessfulTests());
    }

    public Integer getFailingStepCount() {
        return sum(stories, on(StoryTestResults.class).countStepsInFailingTests());
    }

    public Double getPercentageFailingStepCount() {
        if (getTotalStepCount() > 0) {
            return roundedTo1DecimalPlace(((double)getFailingStepCount()) / ((double)getTotalStepCount()));
        } else {
            return 0.0;
        }
    }

    public Double getPercentagePassingStepCount() {
        if (getTotalStepCount() > 0) {
            return roundedTo1DecimalPlace(((double)getPassingStepCount()) / ((double)getTotalStepCount()));
        } else {
            return 0.0;
        }
    }

    public Double getPercentagePendingStepCount() {
        
        return roundedTo1DecimalPlace(1 - getPercentageFailingStepCount() - getPercentagePassingStepCount());
    }

    private Double roundedTo1DecimalPlace(double value) {
        return BigDecimal.valueOf(value).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public CoverageFormatter getFormatted() {
        return new CoverageFormatter(getPercentagePassingStepCount(),
                getPercentagePendingStepCount(),
                getPercentageFailingStepCount());
    }

}
