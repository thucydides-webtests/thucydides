package net.thucydides.core.reports.json;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.features.ApplicationFeature;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractColorSchemeTest {

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

    @Feature
    class WozitFeature {
         class PurchaseNewWozit{};
         class SearchWozits{};
         class DisplayWozitss{};
    }

    protected FeatureResults widgetFeature;
    protected FeatureResults gizmoFeature;
    protected FeatureResults wozitFeature;


    protected FeatureResults mockFeatureResults(Class<?> featureClass,
                                              Integer stepCount,
                                              Integer storyCount,
                                              Integer testCount,
                                              Integer passingCount,
                                              Integer pendingCount,
                                              Integer failingCount) {
        FeatureResults feature = mock(FeatureResults.class);
        when(feature.getFeature()).thenReturn(ApplicationFeature.from(featureClass));
        when(feature.getTotalSteps()).thenReturn(stepCount);
        when(feature.getTotalStories()).thenReturn(storyCount);
        when(feature.getTotalTests()).thenReturn(testCount);
        when(feature.getPassingTests()).thenReturn(passingCount);
        when(feature.getPendingTests()).thenReturn(pendingCount);
        when(feature.getFailingTests()).thenReturn(failingCount);
        return feature;
    }

    protected StoryTestResults mockStory(Integer testCount,
                                      Integer passingCount,
                                      Integer pendingCount,
                                      Integer failingCount) {
        StoryTestResults story = mock(StoryTestResults.class);
        when(story.getTotal()).thenReturn(testCount);
        when(story.getSuccessCount()).thenReturn(passingCount);
        when(story.getPendingCount()).thenReturn(pendingCount);
        when(story.getFailureCount()).thenReturn(failingCount);
        return story;
    }

    protected TestOutcome mockTestOutcome(int stepCount, TestResult result) {
        TestOutcome outcome = mock(TestOutcome.class);
        when(outcome.getResult()).thenReturn(result);
        when(outcome.getStepCount()).thenReturn(stepCount);
        return outcome;
    }
}
