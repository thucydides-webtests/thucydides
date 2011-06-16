package net.thucydides.core.reports.json;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.features.ApplicationFeature;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        System.out.println(json);

        assertThat(json, not(containsString("\"class\":")));
    }

    @Test
    public void the_result_tree_should_not_include_the_color_scheme() {
        JSONResultTree resultTree = new JSONResultTree();

        String json = resultTree.toJSON();
        System.out.println(json);

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
    public void should_add_features() {
        JSONResultTree resultTree = new JSONResultTree();

        FeatureResults widgetFeature = featureResultsFor(WidgetFeature.class);
        FeatureResults gizmoFeature = featureResultsFor(GizmoFeature.class);
        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();
        System.out.println(json);

        assertThat(json, containsString("\"id\":\"net.thucydides.core.reports.json.WidgetFeature\""));
        assertThat(json, containsString("\"name\":\"Widget feature\""));
        assertThat(json, containsString("\"id\":\"net.thucydides.core.reports.json.GizmoFeature\""));
        assertThat(json, containsString("\"name\":\"Gizmo feature\""));

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
        System.out.println(json);

        assertThat(json, containsString("\"$area\":100"));
        assertThat(json, containsString("\"$area\":150"));
        assertThat(json, containsString("\"$area\":250"));
    }

    @Test
    public void features_should_record_the_number_of_steps_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();
        System.out.println(json);

        assertThat(json, containsString("\"steps\":10"));
        assertThat(json, containsString("\"steps\":15"));
        assertThat(json, containsString("\"steps\":25"));
    }

    @Test
    public void features_should_record_the_number_of_user_stories_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();
        System.out.println(json);

        assertThat(json, containsString("\"stories\":10"));
        assertThat(json, containsString("\"stories\":15"));
        assertThat(json, containsString("\"stories\":25"));
    }

    @Test
    public void features_should_record_the_number_of_tests_they_contain() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);
        resultTree.addFeature(wozitFeature);

        String json = resultTree.toJSON();
        System.out.println(json);

        assertThat(json, containsString("\"tests\":20"));
        assertThat(json, containsString("\"tests\":30"));
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
        System.out.println(json);

        assertThat(json, containsString("\"passing\":15"));
        assertThat(json, containsString("\"passing\":20"));
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
    public void feature_list_should_have_green_for_passing_feature() {
        JSONResultTree resultTree = new JSONResultTree();

        prepareFeatureResults();

        resultTree.addFeature(widgetFeature);
        resultTree.addFeature(gizmoFeature);

        String json = resultTree.toJSON();

        assertThat(json, containsString("\"$color\":\"#00ff00\""));

    }

    private void prepareFeatureResults() {
        widgetFeature = mockFeatureResults(WidgetFeature.class, 100, 10, 20, 20, 0, 0);
        gizmoFeature = mockFeatureResults(GizmoFeature.class,   150, 15, 30, 0, 0, 30);
        wozitFeature = mockFeatureResults(WozitFeature.class,   250, 25, 50, 0, 50, 0);
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


}