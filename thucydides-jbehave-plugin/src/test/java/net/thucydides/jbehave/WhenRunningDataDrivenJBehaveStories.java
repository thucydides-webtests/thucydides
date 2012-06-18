package net.thucydides.jbehave;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import org.jbehave.core.annotations.Given;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenRunningDataDrivenJBehaveStories extends AbstractJBehaveStory {

    @Test
    public void a_data_driven_test_should_produce_a_set_of_steps_per_line_of_data() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aDataDrivenBehavior.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getTestSteps().size(), is(9));
    }

    @Test
    public void a_data_driven_test_should_produce_a_steps_with_the_data_values_in_the_titles() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aDataDrivenBehavior.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        List<TestStep> steps = outcomes.get(0).getTestSteps();
        assertThat(steps.get(0).getDescription(), containsString("10"));
        assertThat(steps.get(3).getDescription(), containsString("11"));
        assertThat(steps.get(6).getDescription(), containsString("12"));
    }

    @Test
    public void a_data_driven_test_should_produce_a_successful_result_if_all_rows_are_successful() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aDataDrivenBehavior.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.get(0).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_failing_step_in_a_data_driven_test_should_not_affect_subsequent_steps() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aFailingDataDrivenBehavior.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();

        List<TestStep> steps = outcomes.get(0).getTestSteps();
        assertThat(steps.get(2).getResult(), is(TestResult.SUCCESS));
        assertThat(steps.get(6).getResult(), is(TestResult.FAILURE));
        assertThat(steps.get(10).getResult(), is(TestResult.SUCCESS));

        assertThat(outcomes.get(0).getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void steps_in_an_example_after_a_failing_step_should_be_skipped() throws Throwable {

        // Given
        JUnitThucydidesStories story = new AStorySample("aFailingDataDrivenBehavior.story");

        story.setSystemConfiguration(systemConfiguration);
        story.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();

        List<TestStep> steps = outcomes.get(0).getTestSteps();
        assertThat(steps.get(2).getResult(), is(TestResult.SUCCESS));
        assertThat(steps.get(6).getResult(), is(TestResult.FAILURE));
        assertThat(steps.get(7).getResult(), is(TestResult.SKIPPED));
        assertThat(steps.get(10).getResult(), is(TestResult.SUCCESS));

        assertThat(outcomes.get(0).getResult(), is(TestResult.FAILURE));

    }

}
