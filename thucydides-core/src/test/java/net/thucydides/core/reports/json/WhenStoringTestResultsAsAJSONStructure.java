package net.thucydides.core.reports.json;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.features.ApplicationFeature;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Feature
class WidgetFeature {
     class PurchaseNewWidget{};
     class SearchWidgets{};
     class DisplayWidgets{};
     class SaveWidgets {};
     class DeleteWidgets {};
}

@Feature
class GizmoFeature {
    class PurchaseNewGizmo{};
    class SearchGizmos{};
    class DisplayGizmos{};
    class SaveGizmos{};
}

@Feature
class WozitFeature {
    class PurchaseNewWozit{};
    class SearchWozits{};
    class DisplayWozitss{};
    class SaveWozitss{};
    class OrderWozitss{};
}


public class WhenStoringTestResultsAsAJSONStructure {

    FeatureResults widgetFeature;
    FeatureResults gizmoFeature;
    FeatureResults wozitFeature;


    @Test
    public void an_empty_result_tree_should_have_a_root_node() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();
        assertThat(json, containsString("\"id\":\"root\""));
    }

    @Test
    public void the_result_tree_should_not_include_the_class() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();

        assertThat(json, not(containsString("\"class\":")));
    }

    @Test
    public void the_result_tree_should_not_include_the_color_scheme() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();

        assertThat(json, not(containsString("\"colorScheme\":")));
    }

    @Test
    public void an_empty_result_tree_should_contain_no_data_in_the_root_node() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"data\":{},"));
    }

    @Test
    public void an_empty_result_tree_should_contain_no_children_in_the_root_node() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"children\":[],"));
    }

    @Test
    public void the_root_node_should_be_called_Application() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"name\":\"Application\""));
    }

    @Test
    public void should_be_able_to_add_features() {
        JSONResultTree resultTree = new JSONResultTree();

        FeatureResults widgetFeature = featureResultsFor(WidgetFeature.class);
        FeatureResults gizmoFeature = featureResultsFor(GizmoFeature.class);
        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"id\":\"net.thucydides.core.reports.json.WidgetFeature\""));
        assertThat(json, containsString("\"name\":\"Widget feature\""));
        assertThat(json, containsString("\"id\":\"net.thucydides.core.reports.json.GizmoFeature\""));
        assertThat(json, containsString("\"name\":\"Gizmo feature\""));

    }

    @Test
    public void should_be_able_to_add_user_stories() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();
        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"id\":\"net.thucydides.core.reports.json.WidgetFeature.PurchaseNewWidget\""));
        assertThat(json, containsString("\"name\":\"Purchase new widget\""));

    }

    private FeatureResults featureResultsFor(Class<?> featureClass) {
        ApplicationFeature applicationFeature = ApplicationFeature.from(featureClass);
        return new FeatureResults(applicationFeature);
    }

    @Test
    public void features_should_have_a_size_proportional_to_the_total_number_of_steps_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"$area\":90"));
        assertThat(json, containsString("\"$area\":120"));
        assertThat(json, containsString("\"$area\":150"));
    }

    @Test
    public void features_should_record_the_number_of_steps_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"steps\":90"));
        assertThat(json, containsString("\"steps\":120"));
        assertThat(json, containsString("\"steps\":150"));
    }

    @Test
    public void features_should_record_the_number_of_user_stories_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"stories\":3"));
        assertThat(json, containsString("\"stories\":4"));
        assertThat(json, containsString("\"stories\":5"));
    }

    @Test
    public void features_should_record_the_number_of_tests_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"tests\":30"));
        assertThat(json, containsString("\"tests\":40"));
        assertThat(json, containsString("\"tests\":50"));
    }

    @Test
    public void features_should_record_the_number_of_passing_tests_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"passing\":30"));
    }

    @Test
    public void feature_list_should_not_contain_class() {
        JSONResultTree resultTree = new JSONResultTree();

        FeatureResults widgetFeature = featureResultsFor(WidgetFeature.class);
        FeatureResults gizmoFeature = featureResultsFor(GizmoFeature.class);
        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, not(containsString("\"class\":")));

    }

    @Test
    public void feature_list_should_have_colors() {
        JSONResultTree resultTree = new JSONResultTree();

        FeatureResults widgetFeature = featureResultsFor(WidgetFeature.class);
        FeatureResults gizmoFeature = featureResultsFor(GizmoFeature.class);
        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"$color\":"));

    }

    @Test
    public void feature_list_should_have_red_for_failing_feature() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"$color\":\"#ff0000\""));

    }


    @Test
    public void stories_should_contain_the_name_of_the_corresponding_report_page() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"report\":\"story-report.html\""));

    }

    @Test
    public void feature_list_should_have_green_for_passing_feature() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"$color\":\"#00ff00\""));

    }

    @Test
    public void the_count_of_executed_tests_in_a_feature_should_include_only_passing_and_failing_tests() {

        prepareFeatureResults();


        JSONTreeNode treeNode = new JSONTreeNode("widgets", "Widgets", new ColorScheme());

        assertThat(treeNode.totalExecutedTestsIn(widgetFeature), is(51));
    }

    @Test
    public void the_count_of_executed_test_steps_in_a_feature_should_include_only_steps_inpassing_and_failing_tests() {

        prepareFeatureResults();


        JSONTreeNode treeNode = new JSONTreeNode("widgets", "Widgets", new ColorScheme());

        assertThat(treeNode.totalStepsInExecutedTestsIn(widgetFeature), is(153));
    }

    private void prepareFeatureResults() {

        List<StoryTestResults> widgetStoryResults = new ArrayList<StoryTestResults>();
        widgetStoryResults.add(mockStoryTestResults(WidgetFeature.PurchaseNewWidget.class, 30, 10, 10, 0, 0));
        widgetStoryResults.add(mockStoryTestResults(WidgetFeature.SearchWidgets.class,     30, 10, 10, 0, 0));
        widgetStoryResults.add(mockStoryTestResults(WidgetFeature.DisplayWidgets.class,    30, 10, 10, 0, 0));
        widgetStoryResults.add(mockStoryTestResults(WidgetFeature.SaveWidgets.class,       30, 10, 5,  3, 2));
        widgetStoryResults.add(mockStoryTestResults(WidgetFeature.DeleteWidgets.class,     60, 20, 10, 6, 4));

        widgetFeature = mockFeatureResults(WidgetFeature.class, widgetStoryResults, 90, 3, 30, 30, 0, 0);

        gizmoFeature = mockFeatureResults(GizmoFeature.class,   120, 4, 40, 0, 0, 40);
        wozitFeature = mockFeatureResults(WozitFeature.class,   150, 5, 50, 0, 50, 0);
    }

    private FeatureResults mockFeatureResults(Class<?> featureClass,
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

    private FeatureResults mockFeatureResults(Class<?> featureClass,
                                              List<StoryTestResults> storyResults,
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
        when(feature.getStoryResults()).thenReturn(storyResults);
        return feature;
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
        List<TestOutcome> outcomes = mockSomeTestOutcomes(passingCount,pendingCount,failingCount);
        when(story.getTestOutcomes()).thenReturn(outcomes);
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
            when(outcome.getTitle()).thenReturn("test_method" + result + "_" + count);
            when(outcome.getTitle()).thenReturn("Test " + result + " " + count);
            when(outcome.getResult()).thenReturn(result);
            when(outcome.getStepCount()).thenReturn(3);
            when(outcome.getReportName(ReportNamer.ReportType.HTML)).thenReturn("story-report.html");
            if (result == TestResult.FAILURE) {
                when(outcome.isFailure()).thenReturn(true);
                when(outcome.isSuccess()).thenReturn(false);
                when(outcome.isPending()).thenReturn(false);
            } else if (result == TestResult.SUCCESS) {
                when(outcome.isFailure()).thenReturn(false);
                when(outcome.isSuccess()).thenReturn(true);
                when(outcome.isPending()).thenReturn(false);
            } else if (result == TestResult.PENDING) {
                when(outcome.isFailure()).thenReturn(false);
                when(outcome.isSuccess()).thenReturn(false);
                when(outcome.isPending()).thenReturn(true);
            }
            outcomes.add(outcome);
        }
    }


}