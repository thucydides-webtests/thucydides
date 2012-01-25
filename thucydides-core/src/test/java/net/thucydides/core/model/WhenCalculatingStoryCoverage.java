package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static net.thucydides.core.model.TestStepFactory.forAFailingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.forAnIgnoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.forAPendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.forASuccessfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenCalculatingStoryCoverage {

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    List<StoryTestResults> storyResults;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

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
        assertThat(storyResults.getPercentPassingCoverage(), is(0.3));
        assertThat(storyResults.getPercentFailingCoverage(), is(0.2));
        assertThat(storyResults.getPercentPendingCoverage(), is(0.5));
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

        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables);
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);

        StoryTestResults storyResults = new StoryTestResults(story, configuration, issueTracking);
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        environmentVariables.setProperty("thucydides.estimated.average.step.count", "10");
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
    public void a_story_with_no_tests_has_no_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);

        assertThat(storyResults.countStepsInSuccessfulTests(), is(0));
        assertThat(storyResults.getPercentPassingCoverage(), is(0.0));
        assertThat(storyResults.getPercentFailingCoverage(), is(0.0));
        assertThat(storyResults.getPercentPendingCoverage(), is(0.0));
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

        assertThat(featureResults.getPercentPassingCoverage(), is(0.1875));
    }

    @Test
    public void a_feature_with_no_stories_has_no_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);

        assertThat(featureResults.countStepsInSuccessfulTests(), is(0));
        assertThat(featureResults.getPercentPassingCoverage(), is(0.0));
        assertThat(featureResults.getPercentFailingCoverage(), is(0.0));
        assertThat(featureResults.getPercentPendingCoverage(), is(0.0));
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

        assertThat(featureResults.getPercentFailingCoverage(), is(0.3125));
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

        assertThat(featureResults.getPercentPendingCoverage(), is(0.5));
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
    public void a_feature_can_provide_formatted_coverage_results() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatIsFailingFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatIsFailingFor(story, 10));
        storyResults2.recordTestRun(thatIsFailingFor(story, 20));
        storyResults2.recordTestRun(thatIsFailingFor(story, 30));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getFormatted().getPercentFailingCoverage(), is("37.5%"));
        assertThat(featureResults.getFormatted().getPercentPassingCoverage(), is("12.5%"));
        assertThat(featureResults.getFormatted().getPercentPendingCoverage(), is("50%"));
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
    public void a_feature_can_determine_the_overall_result_of_its_stories_for_pending_stories() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatIsPendingFor(story, 10));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatSucceedsFor(story, 10));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_feature_can_determine_the_overall_result_of_its_stories_for_passing_stories() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatSucceedsFor(story, 10));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_feature_can_determine_the_overall_result_of_its_stories_for_failing_stories() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatIsFailingFor(story, 10));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        assertThat(featureResults.getResult(), is(TestResult.FAILURE));
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
            testOutcome.recordStep(forASuccessfulTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsPendingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(forAPendingTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsIgnoredFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(forAnIgnoredTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsFailingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(forAFailingTestStepCalled("Step " + i, new AssertionError()));
        }
        return testOutcome;
    }
}
