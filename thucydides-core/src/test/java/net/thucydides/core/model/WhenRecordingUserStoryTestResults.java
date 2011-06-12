package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class WhenRecordingUserStoryTestResults {

    private Story userStory;
    private UserStoryTestResults userStoryTestResults;

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    @Before
    public void init() {
        userStory = Story.from(WidgetFeature.PurchaseNewWidget.class);
        userStoryTestResults = new UserStoryTestResults(userStory);
    }

    static class SubclassedUserStory extends Story {

        public SubclassedUserStory(final Class storyClass) {
            super(storyClass);
        }
    }

    @Test
    public void a_user_story_is_not_equal_to_instances_of_any_other_class() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        Story story2 = new SubclassedUserStory(WidgetFeature.PurchaseNewWidget.class);

        Assert.assertThat(story.equals(story2), is(false));
    }

    @Test
    public void user_stories_with_identical_field_values_are_equal() {
        Story story1 = Story.from(WidgetFeature.PurchaseNewWidget.class);
        Story story2 = Story.from(WidgetFeature.PurchaseNewWidget.class);

        Assert.assertThat(story1.equals(story2), is(true));
        Assert.assertThat(story1.hashCode(), is(equalTo(story2.hashCode())));
    }
    
    @Test
    public void a_user_story_test_result_can_contain_a_set_of_test_runs() {
        Story story1 = Story.from(WidgetFeature.SearchWidgets.class);
        Story story2 = Story.from(WidgetFeature.SearchWidgets.class);
        TestOutcome testOutcome1 = thatFailsFor(story1);
        TestOutcome testOutcome2 = thatSucceedsFor(story2);

        userStoryTestResults.recordTestRun(testOutcome1);
        userStoryTestResults.recordTestRun(testOutcome2);

        Assert.assertThat(userStoryTestResults.getTotal(), is(2));
        Assert.assertThat(userStoryTestResults.getTestOutcomes(), allOf(hasItem(testOutcome1), hasItem(testOutcome2)));
    }
    
    @Test
    public void a_user_story_is_successful_if_all_tests_are_successful() {
        Story story1 = Story.from(WidgetFeature.SearchWidgets.class);
        Story story2 = Story.from(WidgetFeature.SearchWidgets.class);
        TestOutcome testOutcome1 = thatSucceedsFor(story1);
        TestOutcome testOutcome2 = thatSucceedsFor(story2);

        userStoryTestResults.recordTestRun(testOutcome1);
        userStoryTestResults.recordTestRun(testOutcome2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.SUCCESS));
    }

    
    @Test
    public void a_user_story_fails_if_at_least_one_test_fails() {
        Story story1 = Story.from(WidgetFeature.SearchWidgets.class);
        Story story2 = Story.from(WidgetFeature.SearchWidgets.class);
        TestOutcome testOutcome1 = thatSucceedsFor(story1);
        TestOutcome testOutcome2 = thatFailsFor(story2);

        userStoryTestResults.recordTestRun(testOutcome1);
        userStoryTestResults.recordTestRun(testOutcome2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_user_story_is_pending_if_at_least_one_test_is_pending() {
        Story story1 = Story.from(WidgetFeature.SearchWidgets.class);
        Story story2 = Story.from(WidgetFeature.SearchWidgets.class);
        TestOutcome testOutcome1 = thatSucceedsFor(story1);
        TestOutcome testOutcome2 = thatIsPendingFor(story2);

        userStoryTestResults.recordTestRun(testOutcome1);
        userStoryTestResults.recordTestRun(testOutcome2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_user_story_is_ignored_if_all_tests_are_ignored() {
        TestOutcome testOutcome1 = thatIsIgnoredCalled("Test Run 2");
        TestOutcome testOutcome2 = thatIsIgnoredCalled("Test Run 2");

        userStoryTestResults.recordTestRun(testOutcome1);
        userStoryTestResults.recordTestRun(testOutcome2);

        Assert.assertThat(userStoryTestResults.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_failed_test_runs() {

        Story story1 = Story.from(WidgetFeature.SearchWidgets.class);
        Story story2 = Story.from(WidgetFeature.SearchWidgets.class);
        TestOutcome testOutcome1 = thatFailsFor(story1);
        TestOutcome testOutcome2 = thatFailsFor(story2);

        userStoryTestResults.recordTestRun(testOutcome1);
        userStoryTestResults.recordTestRun(testOutcome2);

        Assert.assertThat(userStoryTestResults.getFailureCount(), is(2));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_successful_test_runs() {

        userStoryTestResults.recordTestRun(thatFailsFor(userStory));
        userStoryTestResults.recordTestRun(thatSucceedsFor(userStory));
        userStoryTestResults.recordTestRun(thatFailsFor(userStory));

        Assert.assertThat(userStoryTestResults.getFailureCount(), is(2));
        Assert.assertThat(userStoryTestResults.getSuccessCount(), is(1));
    }

    @Test
    public void an_aggregate_test_result_should_count_the_number_of_pending_test_runs() {

        userStoryTestResults.recordTestRun(thatFailsFor(userStory));
        userStoryTestResults.recordTestRun(thatSucceedsFor(userStory));
        userStoryTestResults.recordTestRun(thatIsPendingFor(userStory));
        userStoryTestResults.recordTestRun(thatIsPendingFor(userStory));
        userStoryTestResults.recordTestRun(thatIsPendingFor(userStory));

        Assert.assertThat(userStoryTestResults.getPendingCount(), is(3));
    }
    
    @Test
    public void a_aggregate_test_result_set_knows_what_stories_it_contains() {

        Story someStory = Story.from(WidgetFeature.PurchaseNewWidget.class);
        UserStoryTestResults testResults = new UserStoryTestResults(someStory);

        testResults.recordTestRun(thatSucceedsFor(userStory));

        Assert.assertThat(testResults.containsResultsFor(someStory), is(true));
    }
    
    @Test
    public void a_aggregate_test_result_set_knows_what_stories_it_contains_even_if_it_is_empty() {

        Story someStory = Story.from(WidgetFeature.PurchaseNewWidget.class);
        UserStoryTestResults testResults = new UserStoryTestResults(someStory);

        testResults.recordTestRun(thatSucceedsFor(userStory));

        Assert.assertThat(testResults.containsResultsFor(someStory), is(true));
    }
    @Test
    public void a_aggregate_test_result_set_matches_stories_by_field_values() {

        Story someStory = Story.from(WidgetFeature.PurchaseNewWidget.class);
        UserStoryTestResults testResults = new UserStoryTestResults(someStory);
        
        testResults.recordTestRun(thatSucceedsFor(userStory));
        
        Assert.assertThat(testResults.containsResultsFor(someStory), is(true));
    }
    
    @Test
    public void a_aggregate_test_result_set_knows_what_stories_it_doesnt_contain() {

        Story someStory1 = Story.from(WidgetFeature.PurchaseNewWidget.class);
        Story someStory2 = Story.from(WidgetFeature.DisplayWidgets.class);

        UserStoryTestResults testResults = new UserStoryTestResults(someStory1);
        
        testResults.recordTestRun(thatSucceedsFor(userStory));

        Assert.assertThat(testResults.containsResultsFor(someStory2), is(false));
    }
    
    private TestOutcome thatFailsFor(Story story) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 3"));
        return testOutcome;
    }
    
    private TestOutcome thatSucceedsFor(Story story) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        return testOutcome;
    }
    
    private TestOutcome thatIsPendingFor(Story story) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(pendingTestStepCalled("Step 2"));
        testOutcome.recordStep(pendingTestStepCalled("Step 3"));
        testOutcome.recordStep(pendingTestStepCalled("Step 4"));
        testOutcome.recordStep(pendingTestStepCalled("Step 5"));
        return testOutcome;
    }
    
    private TestOutcome thatIsIgnoredCalled(String title) {
        TestOutcome testOutcome = new TestOutcome(title);
        testOutcome.recordStep(ignoredTestStepCalled("Step 1"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 2"));
        testOutcome.recordStep(ignoredTestStepCalled("Step 3"));
        return testOutcome;
    }
    
}
