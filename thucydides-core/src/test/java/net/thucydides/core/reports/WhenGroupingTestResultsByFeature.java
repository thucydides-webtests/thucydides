package net.thucydides.core.reports;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.model.features.FeatureLoader;
import net.thucydides.core.model.userstories.UserStoryLoader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.matchers.ThucydidesMatchers.containsApplicationFeature;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class WhenGroupingTestResultsByFeature {

    @Mock
    UserStoryLoader mockUserStoryLoader;

    FeatureLoader loader;

    List<StoryTestResults> stories;

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    @Feature
    class GizmoFeature {
         class PurchaseNewGizmo{};
         class SearchGizmos{};
         class DisplayGizmos{};
    }

    ApplicationFeature widgetFeature = ApplicationFeature.from(WidgetFeature.class);
    ApplicationFeature gizmoFeature = ApplicationFeature.from(GizmoFeature.class);

    @Mock
    File reportDirectory;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        stories = createMockUserStories();
        loader = new FeatureLoader();
        loader.setUserStoriesLoader(mockUserStoryLoader);
    }

    private List<StoryTestResults> createMockUserStories() {
        List<StoryTestResults> mockStories = new ArrayList<StoryTestResults>();

        mockStories.add(storyTestResult(Story.from(WidgetFeature.PurchaseNewWidget.class), 2, 0, 1, 1, 10));
        mockStories.add(storyTestResult(Story.from(WidgetFeature.SearchWidgets.class), 2, 1, 1, 0, 20));
        mockStories.add(storyTestResult(Story.from(WidgetFeature.SearchWidgets.class), 3, 2, 1, 0, 10));
        mockStories.add(storyTestResult(Story.from(GizmoFeature.PurchaseNewGizmo.class), 4, 2, 1, 1, 10));

        return mockStories;

    }

    private StoryTestResults storyTestResult(Story story,
                                             int testCount,
                                             int passingTests,
                                             int failingTests,
                                             int pendingTests,
                                             int totalSteps) {
        StoryTestResults storyResults = mock(StoryTestResults.class);

        when(storyResults.getStory()).thenReturn(story);
        when(storyResults.getTotal()).thenReturn(testCount);
        when(storyResults.getSuccessCount()).thenReturn(passingTests);
        when(storyResults.getFailureCount()).thenReturn(failingTests);
        when(storyResults.getPendingCount()).thenReturn(pendingTests);
        when(storyResults.getStepCount()).thenReturn(totalSteps);
        return storyResults;
    }

    @Test
    public void features_should_be_retrieved_from_the_user_stories() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        assertThat(features.size(), is(2));
        assertThat(features, containsApplicationFeature(widgetFeature));
        assertThat(features, containsApplicationFeature(gizmoFeature));

    }

    @Test
    public void should_know_number_of_stories_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);

        assertThat(widgetFeatureResult.getTotalStories(), is(3));
        assertThat(gizmoFeatureResult.getTotalStories(), is(1));

    }

    @Test
    public void should_know_number_of_tests_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);

        assertThat(widgetFeatureResult.getTotalTests(), is(7));
        assertThat(gizmoFeatureResult.getTotalTests(), is(4));

    }

    @Test
    public void should_know_number_of_tests_in_the_stories_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);

        assertThat(widgetFeatureResult.getTotalTests(), is(7));
        assertThat(gizmoFeatureResult.getTotalTests(), is(4));

    }

    @Test
    public void should_know_number_of_passing_tests_in_the_stories_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);

        assertThat(widgetFeatureResult.getPassingTests(), is(3));
        assertThat(gizmoFeatureResult.getPassingTests(), is(2));

    }

    @Test
    public void should_know_number_of_failing_tests_in_the_stories_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);

        assertThat(widgetFeatureResult.getFailingTests(), is(3));
        assertThat(gizmoFeatureResult.getFailingTests(), is(1));

    }

    @Test
    public void should_know_number_of_pending_tests_in_the_stories_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);

        assertThat(widgetFeatureResult.getPendingTests(), is(1));
        assertThat(gizmoFeatureResult.getPendingTests(), is(1));
    }

    @Test
    public void should_know_number_of_steps_in_the_stories_of_a_feature() throws IOException {
        when(mockUserStoryLoader.loadFrom(reportDirectory)).thenReturn(stories);

        List<FeatureResults> features = loader.loadFrom(reportDirectory);

        FeatureResults widgetFeatureResult = features.get(0);
        FeatureResults gizmoFeatureResult = features.get(1);
        assertThat(widgetFeatureResult.getTotalSteps(), is(40));
        assertThat(gizmoFeatureResult.getTotalSteps(), is(10));
    }


}
