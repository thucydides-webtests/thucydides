package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static net.thucydides.core.model.TestStepFactory.forABrokenTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.forAPendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.forASuccessfulTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.forAnIgnoredTestStepCalled;
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
    public void a_story_with_no_tests_has_no_coverage() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);

        assertThat(storyResults.countStepsInSuccessfulTests(), is(0));
        assertThat(storyResults.getPercentPassingCoverage(), is(0.0));
        assertThat(storyResults.getPercentFailingCoverage(), is(0.0));
        assertThat(storyResults.getPercentPendingCoverage(), is(0.0));
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
            testOutcome.recordStep(forABrokenTestStepCalled("Step " + i, new AssertionError()));
        }
        return testOutcome;
    }
}
