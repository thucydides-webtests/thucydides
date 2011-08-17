package net.thucydides.core.reports.integration;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.model.features.FeatureLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static net.thucydides.core.matchers.ThucydidesMatchers.containsApplicationFeature;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class WhenLoadingTestResultsByFeature {

    FeatureLoader loader;

    @Before
    public void createNewLoader() {
        loader = new FeatureLoader();
    }

    private File directoryInClasspathCalled(final String resourceName) {
        return new File(getClass().getResource(resourceName).getPath());
    }

    @Test
    public void should_load_the_test_results_into_a_set_of_features() throws IOException {

        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/single-user-story-reports"));
        assertThat(features.size(), is(1));
    }

    @Test
    public void should_load_the_test_results_from_several_stories_into_a_set_of_features() throws IOException {

        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/multiple-user-story-reports"));
        assertThat(features.size(), is(3));
    }

    @Test
    public void features_should_be_retrieved_from_the_user_stories() throws IOException {

        ApplicationFeature aFeature = new ApplicationFeature("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature",
                                                                  "A feature");

        ApplicationFeature anotherFeature = new ApplicationFeature("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AnotherFeature",
                                                                  "Another feature");

        ApplicationFeature anotherDifferentFeature = new ApplicationFeature("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AnotherDifferentFeature",
                                                                  "Another different feature");

        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/multiple-user-story-reports"));

        assertThat(features.size(), is(3));
        assertThat(features, containsApplicationFeature(aFeature));
        assertThat(features, containsApplicationFeature(anotherFeature));
        assertThat(features, containsApplicationFeature(anotherDifferentFeature));

    }

    @Test
    public void should_know_number_of_stories_of_a_feature() throws IOException {

        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/multiple-user-story-reports"));

        FeatureResults aFeatureResult = features.get(0);
        FeatureResults anotherFeatureResult = features.get(1);
        FeatureResults anotherDifferentFeatureResult = features.get(2);

        assertThat(aFeatureResult.getTotalStories(), is(2));
        assertThat(anotherFeatureResult.getTotalStories(), is(1));
        assertThat(anotherDifferentFeatureResult.getTotalStories(), is(1));

    }

    @Test
    public void should_know_number_of_passing_tests_in_the_stories_of_a_feature() throws IOException {

        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/multiple-user-story-reports"));

        FeatureResults aFeatureResult = features.get(0);
        FeatureResults anotherFeatureResult = features.get(1);
        FeatureResults anotherDifferentFeatureResult = features.get(2);

        assertThat(aFeatureResult.getPassingTests(), is(2));
        assertThat(anotherFeatureResult.getPassingTests(), is(0));
        assertThat(anotherDifferentFeatureResult.getPassingTests(), is(0));

    }

    @Test
    public void should_know_number_of_failing_tests_in_the_stories_of_a_feature() throws IOException {
        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/featured-user-story-reports"));

        FeatureResults aFeatureResult = features.get(0);
        FeatureResults anotherFeatureResult = features.get(1);
        FeatureResults anotherDifferentFeatureResult = features.get(2);

        assertThat(aFeatureResult.getFailingTests(), is(0));
        assertThat(anotherFeatureResult.getFailingTests(), is(1));
        assertThat(anotherDifferentFeatureResult.getFailingTests(), is(0));

    }

    @Test
    public void should_know_number_of_pending_tests_in_the_stories_of_a_feature() throws IOException {
        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/featured-user-story-reports"));

        FeatureResults aFeatureResult = features.get(0);
        FeatureResults anotherFeatureResult = features.get(1);
        FeatureResults anotherDifferentFeatureResult = features.get(2);

        assertThat(aFeatureResult.getPendingTests(), is(1));
        assertThat(anotherFeatureResult.getPendingTests(), is(0));
        assertThat(anotherDifferentFeatureResult.getPendingTests(), is(1));
    }


    @Test
    public void should_know_number_of_steps_in_the_stories_of_a_feature() throws IOException {
        List<FeatureResults> features = loader.loadFrom(directoryInClasspathCalled("/featured-user-story-reports"));

        FeatureResults aFeatureResult = features.get(0);
        FeatureResults anotherFeatureResult = features.get(1);
        FeatureResults anotherDifferentFeatureResult = features.get(2);

        assertThat(aFeatureResult.getTotalSteps(), is(17));
        assertThat(anotherFeatureResult.getTotalSteps(), is(6));
        assertThat(anotherDifferentFeatureResult.getTotalSteps(), is(6));
    }

}
