package net.thucydides.jbehave;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import org.junit.Test;

import java.util.List;

import static net.thucydides.core.matchers.PublicThucydidesMatchers.containsResults;
import static net.thucydides.core.model.TestResult.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenRunningJBehaveStories extends AbstractJBehaveStory {

    private static final int TOTAL_NUMBER_OF_JBEHAVE_SCENARIOS = 15;

    final static class AllStoriesSample extends JUnitThucydidesStories {}

    final static class AStorySample extends JUnitThucydidesStories {
        private final String storyName;

        AStorySample(String storyName) {
            this.storyName = storyName;
        }

        public void configure() {
            findStoriesCalled(storyName);
        }
    }

    @Test
    public void all_stories_on_the_classpath_should_be_run_by_default() throws Throwable {

        // Given
        JUnitThucydidesStories stories = new AllStoriesSample();
        stories.setSystemConfiguration(systemConfiguration);
        stories.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(stories);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(TOTAL_NUMBER_OF_JBEHAVE_SCENARIOS));
    }

    final static class StoriesInTheSubsetFolderSample extends JUnitThucydidesStories {
        public void configure() {
            findStoriesIn("stories/subset");
        }
    }

    @Test
    public void a_subset_of_the_stories_can_be_run_individually() throws Throwable {

        // Given
        JUnitThucydidesStories stories = new StoriesInTheSubsetFolderSample();
        stories.setSystemConfiguration(systemConfiguration);
        stories.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(stories);

        // Then

        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(2));
    }

    @Test
    public void stories_with_a_matching_name_can_be_run() throws Throwable {

        // Given
        JUnitThucydidesStories stories = new AStorySample("*PassingBehavior.story");
        stories.setSystemConfiguration(systemConfiguration);
        stories.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(stories);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(3));
    }

    @Test
    public void pending_stories_should_be_reported_as_pending() throws Throwable {

        // Given
        JUnitThucydidesStories pendingStory = new AStorySample("aPendingBehavior.story");

        pendingStory.setSystemConfiguration(systemConfiguration);
        pendingStory.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(pendingStory);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void implemented_pending_stories_should_be_reported_as_pending() throws Throwable {

        // Given
        JUnitThucydidesStories pendingStory = new AStorySample("aPendingImplementedBehavior.story");

        pendingStory.setSystemConfiguration(systemConfiguration);
        pendingStory.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(pendingStory);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void passing_stories_should_be_reported_as_passing() throws Throwable {

        // Given
        JUnitThucydidesStories passingStory = new AStorySample("aPassingBehavior.story");

        passingStory.setSystemConfiguration(systemConfiguration);
        passingStory.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(passingStory);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_passing_story_with_steps_should_record_the_steps() throws Throwable {

        // Given
        JUnitThucydidesStories passingStory = new AStorySample("aPassingBehaviorWithSteps.story");

        passingStory.setSystemConfiguration(systemConfiguration);
        passingStory.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(passingStory);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(outcomes.get(0).getNestedStepCount(), is(7));
    }

    @Test
    public void the_given_when_then_clauses_should_count_as_steps() throws Throwable {

        // Given
        JUnitThucydidesStories passingStory = new AStorySample("aPassingBehaviorWithSteps.story");

        passingStory.setSystemConfiguration(systemConfiguration);
        passingStory.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(passingStory);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();

        List<TestStep> steps = outcomes.get(0).getTestSteps();
        assertThat(steps.get(0).getDescription(), is("Given I have an implemented JBehave scenario"));
        assertThat(steps.get(1).getDescription(), is("And the scenario has steps"));
        assertThat(steps.get(2).getDescription(), is("When I run the scenario"));
        assertThat(steps.get(3).getDescription(), is("Then the steps should appear in the outcome"));
    }

    @Test
    public void failing_stories_should_be_reported_as_failing() throws Throwable {

        // Given
        JUnitThucydidesStories failingStory = new AStorySample("aFailingBehavior.story");

        failingStory.setSystemConfiguration(systemConfiguration);
        failingStory.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(failingStory);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void steps_after_a_failing_step_should_be_skipped() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aComplexFailingBehavior.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));

        // And
        assertThat(outcomes.get(0), containsResults(SUCCESS, FAILURE, SKIPPED, SKIPPED, SKIPPED));
    }

    @Test
    public void a_test_with_a_pending_step_should_be_pending() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aBehaviorWithAPendingStep.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));

        // And
        assertThat(outcomes.get(0).getResult() , is(PENDING));
        // And
        assertThat(outcomes.get(0), containsResults(SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS, PENDING, PENDING, SUCCESS));

    }

    @Test
    public void a_test_should_be_associated_with_a_corresponding_issue_if_specified() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aBehaviorWithAnIssue.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getIssueKeys(), hasItem("MYPROJ-456"));

    }

    @Test
    public void a_test_can_be_associated_with_several_issues() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aBehaviorWithMultipleIssues.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.get(0).getIssueKeys(), hasItems("MYPROJ-3","MYPROJ-4","MYPROJ-5"));

    }

    @Test
    public void a_test_story_can_be_associated_with_several_issues() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aBehaviorWithMultipleIssues.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.get(0).getIssueKeys(), hasItems("MYPROJ-1","MYPROJ-2","MYPROJ-3","MYPROJ-4","MYPROJ-5"));

    }
    @Test
    public void all_the_scenarios_in_a_story_should_be_associated_with_a_corresponding_issue_if_specified_at_the_story_level() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aBehaviorWithIssues.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(2));
        assertThat(outcomes.get(0).getIssueKeys(), hasItems("MYPROJ-123", "MYPROJ-456"));
        assertThat(outcomes.get(1).getIssueKeys(), hasItems("MYPROJ-123", "MYPROJ-789"));
    }

}
