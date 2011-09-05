package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.features.ApplicationFeature;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;

public class WhenCalculatingStoryCoverage {

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    @Rule
    public SaveWebdriverSystemPropertiesRule rule = new SaveWebdriverSystemPropertiesRule();

    List<StoryTestResults> storyResults;

    @Test
    public void a_story_with_all_passing_tests_has_a_step_coverage_of_100_percent() {
        StoryTestResults results = testResultsFor(WidgetFeature.PurchaseNewWidget.class, 5, 10, 0, 0);
        assertThat(results.getCoverage(), is(1.0));

    }

    @Test
    public void a_story_with_all_passing_and_failing_tests_has_a_step_coverage_of_100_percent() {
        StoryTestResults results = testResultsFor(WidgetFeature.PurchaseNewWidget.class, 5, 10, 0, 10);
        assertThat(results.getCoverage(), is(1.0));

    }

    @Test
    public void a_story_with_half_pending_tests_has_a_step_coverage_of_50_percent() {
        StoryTestResults results = testResultsFor(WidgetFeature.PurchaseNewWidget.class, 5, 5, 5, 0);
        assertThat(results.getCoverage(), is(0.5));
    }

    @Test
    public void a_story_with_half_unimplemented_pending_tests_has_a_step_coverage_of_50_percent() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getCoverage(), is(0.5));
    }

    @Test
    public void a_story_with_a_quater_implemented_tests_has_a_step_coverage_of_25_percent() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getCoverage(), is(0.25));
    }

    @Test
    public void ignored_tests_should_not_count_as_having_test_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatIsIgnoredFor(story, 10));

        assertThat(storyResults.getCoverage(), is(0.5));
    }


    @Test
    public void story_coverage_can_be_returned_as_a_percentage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatIsIgnoredFor(story, 10));

        assertThat(storyResults.getPercentCoverage(), is(50.0));
        assertThat(storyResults.getPercentPassingCoverage(), is(50.0));
        assertThat(storyResults.getPercentPendingCoverage(), is(50.0));
        assertThat(storyResults.getPercentFailingCoverage(), is(0.0));
    }

    @Test
    public void a_test_with_only_ignored_tests_should_have_no_test_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsIgnoredFor(story, 10));
        storyResults.recordTestRun(thatIsIgnoredFor(story, 10));

        assertThat(storyResults.getCoverage(), is(0.0));
    }

    @Test
    public void failing_tests_should_count_as_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsFailingFor(story, 10));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getCoverage(), is(0.25));
    }

    @Test
    public void should_distinguish_between_passing_and_failing_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsFailingFor(story, 8));
        storyResults.recordTestRun(thatSucceedsFor(story, 12));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getCoverage(), is(0.5));
        assertThat(storyResults.getPercentPassingCoverage(), is(30.0));
        assertThat(storyResults.getPercentFailingCoverage(), is(20.0));
        assertThat(storyResults.getPercentPendingCoverage(), is(50.0));
    }

    @Test
    public void implemented_pending_steps_do_not_count_as_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatIsFailingFor(story, 20));
        storyResults.recordTestRun(thatIsPendingFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getCoverage(), is(0.25));
    }

    @Test
    public void should_know_the_average_number_of_steps_in_the_implemented_tests() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getAverageTestSize(), is(20.0));
    }

    @Test
    public void average_number_of_steps_in_the_implemented_tests_should_include_failing_tests() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatIsFailingFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getAverageTestSize(), is(20.0));
    }

    @Test
    public void average_number_of_steps_in_the_implemented_tests_should_include_pending_tests_with_defined_steps() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatIsPendingFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        assertThat(storyResults.getAverageTestSize(), is(20.0));
    }

    @Test
    public void average_number_of_steps_should_be_a_system_default_if_none_are_defined() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));


        System.setProperty("thucydides.estimated.average.step.count", "10");
        assertThat(storyResults.getAverageTestSize(), is(10.0));
    }

    @Test
    public void average_number_of_steps_should_be_a_sensible_default_if_no_system_value_is_defined() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));


        assertThat(storyResults.getAverageTestSize(), is(5.0));
    }

    @Test
    public void a_feature_with_one_story_has_the_same_coverage_as_the_story() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getCoverage(), is(0.5));
    }

    @Test
    public void a_feature_coverage_can_be_formatted_directly_as_a_percentage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getPercentCoverage(), is(50.0));
    }

    @Test
    public void a_feature_passing_coverage_can_be_formatted_directly_as_a_percentage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatIsFailingFor(story, 50));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getPercentPassingCoverage(), is(18.75));
    }

    @Test
    public void a_feature_failing_coverage_can_be_formatted_directly_as_a_percentage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatIsFailingFor(story, 50));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getPercentFailingCoverage(), is(31.25));
    }

    @Test
    public void a_feature_pending_coverage_can_be_formatted_directly_as_a_percentage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatIsFailingFor(story, 50));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getPercentPendingCoverage(), is(50.0));
    }
    @Test
    public void a_feature_with_several_stories_has_the_average_coverage_across_all_the_stories() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatIsPendingFor(story, 10));
        storyResults2.recordTestRun(thatIsPendingFor(story, 20));
        storyResults2.recordTestRun(thatIsPendingFor(story, 30));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getCoverage(), is(0.25));
    }

    @Test
    public void a_feature_with_several_pending_stories_has_no_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsPendingFor(story, 10));
        storyResults.recordTestRun(thatIsPendingFor(story, 20));
        storyResults.recordTestRun(thatIsPendingFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatIsPendingFor(story, 10));
        storyResults2.recordTestRun(thatIsPendingFor(story, 20));
        storyResults2.recordTestRun(thatIsPendingFor(story, 30));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getCoverage(), is(0.0));
    }

    @Test
    public void a_feature_with_one_passing_story_has_full_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getCoverage(), is(1.0));
    }

    @Test
    public void a_feature_with_one_failing_story_has_full_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsFailingFor(story, 10));
        storyResults.recordTestRun(thatIsFailingFor(story, 20));
        storyResults.recordTestRun(thatIsFailingFor(story, 30));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.getCoverage(), is(1.0));
    }

    @Test
    public void a_feature_with_several_passing_stories_has_full_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatSucceedsFor(story, 30));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatSucceedsFor(story, 10));
        storyResults2.recordTestRun(thatSucceedsFor(story, 20));
        storyResults2.recordTestRun(thatSucceedsFor(story, 30));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getCoverage(), is(1.0));
    }

    @Test
    public void a_feature_with_empty_stories_has_no_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        StoryTestResults storyResults2 = new StoryTestResults(story);

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getCoverage(), is(0.0));
    }

    private StoryTestResults testResultsFor(Class<?> storyClass,
                                            Integer stepCount,
                                            Integer successCount,
                                            Integer pendingCount,
                                            Integer failingCount) {
        Story story = Story.from(storyClass);
        StoryTestResults storyResults = new StoryTestResults(story);
        for(int i = 1; i <= successCount; i++) {
            storyResults.recordTestRun(thatSucceedsFor(story, stepCount));
        }
        for(int i = 1; i <= pendingCount; i++) {
            storyResults.recordTestRun(thatIsPendingFor(story, stepCount));
        }
        for(int i = 1; i <= failingCount; i++) {
            storyResults.recordTestRun(thatIsFailingFor(story, stepCount));
        }
        return storyResults;
    }

    private TestOutcome thatSucceedsFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(successfulTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsPendingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(pendingTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsIgnoredFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(ignoredTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsFailingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(failingTestStepCalled("Step " + i, new AssertionError()));
        }
        return testOutcome;
    }
}
