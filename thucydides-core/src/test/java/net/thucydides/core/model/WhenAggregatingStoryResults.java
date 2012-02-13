package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class WhenAggregatingStoryResults {

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    List<StoryTestResults> storyResults;

    @Before
    public void prepareMockStoryResults() {

        storyResults = new ArrayList<StoryTestResults>();
        storyResults.add(mockStoryTestResults(WidgetFeature.PurchaseNewWidget.class, 30, 10, 5, 3, 2));
        storyResults.add(mockStoryTestResults(WidgetFeature.SearchWidgets.class, 30, 10, 5, 3, 2));
        storyResults.add(mockStoryTestResults(WidgetFeature.DisplayWidgets.class, 30, 10, 5, 3, 2));

    }

    @Test
    public void a_story_can_have_an_associated_feature_class() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        assertThat(story.getFeature().getFeatureClass().getName(), is(WidgetFeature.class.getName()));
    }

    @Test
    public void a_story_can_have_an_associated_feature_by_id() {
        Story story = Story.withId("storyId","storyName","featureId","featureName");
        assertThat(story.getFeature(), is(notNullValue()));
    }

    @Test
    public void a_story_does_not_have_to_have_an_associated_feature() {
        Story story = Story.withId("storyId","storyName");
        assertThat(story.getFeature(), is(nullValue()));
    }

    @Test
    public void should_count_stories() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getStoryCount(), is(3));
    }

    @Test
    public void should_list_stories() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getStories().size(), is(3));
    }

    @Test
    public void should_count_total_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getTotalTestCount(), is(30));
    }

    @Test
    public void should_count_passing_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getSuccessCount(), is(15));
    }
    @Test
    public void should_count_pending_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getPendingCount(), is(9));
    }
    @Test
    public void should_count_failing_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getFailureCount(), is(6));
    }

    @Test
    public void should_count_overall_steps() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getTotalStepCount(), is(300));
    }

    @Test
    public void should_count_steps_in_passing_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getPassingStepCount(), is(195));
    }

    @Test
    public void should_count_percentage_steps_in_failing_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getPercentageFailingStepCount(), is(0.2));
    }

    @Test
    public void should_count_percentage_steps_in_passing_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getPercentagePassingStepCount(), is(0.65));
    }

    @Test
    public void should_count_percentage_steps_in_pending_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getPercentagePendingStepCount(), is(0.15));
    }

    @Test
    public void should_format_percentage_steps() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getFormatted().getPercentFailingCoverage(), is("20%"));
    }

    @Test
    public void should_count_steps_in_failing_tests() {
        UserStoriesResultSet userStoriesResultSet = new UserStoriesResultSet(storyResults);

        assertThat(userStoriesResultSet.getFailingStepCount(), is(60));
    }

    private StoryTestResults mockStoryTestResults(Class<?> storyClass,
                                              Integer stepCount,
                                              Integer testCount,
                                              Integer passingCount,
                                              Integer pendingCount,
                                              Integer failingCount) {
        StoryTestResults story = mock(StoryTestResults.class);
        when(story.getStory()).thenReturn(Story.from(storyClass));
        when(story.getStepCount()).thenReturn(stepCount);
        when(story.getTotal()).thenReturn(testCount);
        when(story.getSuccessCount()).thenReturn(passingCount);
        when(story.getFailureCount()).thenReturn(failingCount);
        when(story.getPendingCount()).thenReturn(pendingCount);
        when(story.getEstimatedTotalStepCount()).thenReturn(100);
        when(story.countStepsInFailingTests()).thenReturn(20);
        when(story.countStepsInSuccessfulTests()).thenReturn(65);
        when(story.countStepsInSkippedTests()).thenReturn(10);


        List<TestOutcome> mockOutcomes = mockSomeTestOutcomes(passingCount,pendingCount,failingCount);

        when(story.getTestOutcomes()).thenReturn(mockOutcomes);
        return story;
    }

    private List<TestOutcome> mockSomeTestOutcomes(int passingCount, int pendingCount, int failingCount) {

        List<TestOutcome> outcomes = new ArrayList<TestOutcome>();
        addOutomesOfType(passingCount, outcomes, TestResult.SUCCESS);
        addOutomesOfType(pendingCount, outcomes, TestResult.PENDING);
        addOutomesOfType(failingCount, outcomes, TestResult.FAILURE);

        return outcomes;
    }

    private void addOutomesOfType(int count, List<TestOutcome> outcomes, TestResult result) {
        for(int i = 0; i < count; i++) {
            TestOutcome outcome = mock(TestOutcome.class);
            when(outcome.getResult()).thenReturn(result);
            outcomes.add(outcome);
        }
    }

}
