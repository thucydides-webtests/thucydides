package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.List;

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
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.getTotal();
        }
        return sum;
    }

    public int getSuccessCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.getSuccessCount();
        }
        return sum;
    }

    public int getFailureCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.getFailureCount();
        }
        return sum;
    }

    public int getPendingCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.getPendingCount();
        }
        return sum;
    }

    public Integer getTotalStepCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.getEstimatedTotalStepCount();
        }
        return sum;
    }

    public Integer getPassingStepCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.countStepsInSuccessfulTests();
        }
        return sum;
    }

    public Integer getFailingStepCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.countStepsInFailingTests();
        }
        return sum;
    }

    public Integer getErrorStepCount() {
        int sum = 0;
        for(StoryTestResults story : stories) {
            sum += story.countStepsInErrorTests();
        }
        return sum;
    }

    public Double getPercentageFailingStepCount() {
        return asPercentage(getFailingStepCount());
    }

    public Double getPercentageErrorStepCount() {
        return asPercentage(getErrorStepCount());
    }

    public Double getPercentagePassingStepCount() {
        return asPercentage(getPassingStepCount());
    }

    private Double asPercentage(double stepCount) {
        if (getTotalStepCount() > 0) {
            return roundedTo1DecimalPlace((stepCount) / ((double)getTotalStepCount()));
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
                getPercentageFailingStepCount(),
                getPercentageErrorStepCount());
    }

}
