package net.thucydides.core.model;


import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.TestsStory;
import net.thucydides.core.model.features.ApplicationFeature;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SimpleTestCase {};

public class WhenGroupingUserStoriesByFeature {

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
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
    public void a_user_story_can_return_the_name_of_its_feature_class() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        Story story = Story.from(userStoryClass);

        assertThat(story.getFeatureId(), is(WidgetFeature.class.getCanonicalName()));
    }

}
