package net.thucydides.core.model;


import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.TestsStory;
import net.thucydides.core.model.features.ApplicationFeature;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

class SimpleTestCase {};

public class WhenGroupingUserStoriesByFeature {

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

    class MyApp {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    @TestsStory(WidgetFeature.PurchaseNewWidget.class)
    class WhenUserPurchasesNewWidgetsTestCase {
        public void shouldDoThis(){}
        public void shouldDoThat(){}
    }

    @TestsStory(WidgetFeature.PurchaseNewWidget.class)
    class WhenUserPurchasesLotsOfNewWidgetsTestCase {
        public void shouldDoSomethingElse(){}
    }

    @Test
    public void a_user_story_can_belong_to_a_feature() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        Story story = Story.from(userStoryClass);
        Class<?> featureClass = story.getFeatureClass();
        assertThat(featureClass.getName(), is(WidgetFeature.class.getName()));
    }

    @Test
    public void a_user_story_does_not_have_to_belong_to_a_feature() {
        Class<?> userStoryClass = SimpleTestCase.class;

        Story story = Story.from(userStoryClass);
        Class<?> featureClass = story.getFeatureClass();
        assertThat(featureClass, is(nullValue()));
    }

    @Test
    public void a_feature_can_be_defined_by_providing_a_feature_class() {
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);

        assertThat(feature.getId(), is(WidgetFeature.class.getCanonicalName()));
        assertThat(feature.getName(), is("Widget feature"));
    }

    @Test
    public void a_feature_can_also_be_defined_by_providing_the_class_path_and_name() {
        ApplicationFeature feature = new ApplicationFeature(WidgetFeature.class.getCanonicalName(), "Widget feature");

        assertThat(feature.getId(), is(WidgetFeature.class.getCanonicalName()));
        assertThat(feature.getName(), is("Widget feature"));
    }

    @Test
    public void a_user_story_can_be_nested_in_a_class_that_is_not_a_feature() {
        Class<?> userStoryClass = MyApp.PurchaseNewWidget.class;

        Story story = Story.from(userStoryClass);
        Class<?> featureClass = story.getFeatureClass();
        assertThat(featureClass, is(nullValue()));
    }

    @Test
    public void a_user_story_can_return_the_name_of_its_feature() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        Story story = Story.from(userStoryClass);

        assertThat(story.getFeatureName(), is("Widget feature"));
    }

    @Test
    public void features_referring_to_the_same_feature_class_are_identical() {
        ApplicationFeature feature1 = ApplicationFeature.from(WidgetFeature.class);
        ApplicationFeature feature2 = ApplicationFeature.from(WidgetFeature.class);

        assertThat(feature1, is(feature2));
    }

    @Test
    public void features_referring_to_different_feature_classes_are_different() {
        ApplicationFeature feature1 = ApplicationFeature.from(WidgetFeature.class);
        ApplicationFeature feature2 = ApplicationFeature.from(GizmoFeature.class);

        assertThat(feature1, is(not(feature2)));
    }

    @Test
    public void features_referring_to_the_same_feature_id_and_name_are_identical() {
        ApplicationFeature feature1 = new ApplicationFeature("id","name");
        ApplicationFeature feature2 = new ApplicationFeature("id","name");

        assertThat(feature1, is(feature2));
    }

    @Test
    public void features_referring_to_different_feature_id_and_names_are_different() {
        ApplicationFeature feature1 = new ApplicationFeature("id","name");
        ApplicationFeature feature2 = new ApplicationFeature("id2","name2");

        assertThat(feature1, is(not(feature2)));
    }

    @Test
    public void a_user_story_can_return_the_corresponding_feature_class() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        Story story = Story.from(userStoryClass);
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);

        assertThat(story.getFeature(), is(feature));
    }

    @Test
    public void a_user_story_can_return_the_corresponding_feature_class_using_id_and_name() {
        Story story = new Story("story.class","AStory", "feature.class","AFeature");

        ApplicationFeature feature = new ApplicationFeature("feature.class","AFeature");

        assertThat(story.getFeature(), is(feature));
    }

    @Test
    public void if_the_feature_name_and_class_are_null_the_features_name_should_be_blank() {
        ApplicationFeature feature = new ApplicationFeature("id",null);

        assertThat(feature.getName(), is(""));
    }


}
