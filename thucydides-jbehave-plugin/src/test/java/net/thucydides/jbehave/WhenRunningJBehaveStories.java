package net.thucydides.jbehave;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.TxtOutput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRunningJBehaveStories extends AbstractJBehaveStory {

    private static final int TOTAL_NUMBER_OF_JBEHAVE_SCENARIOS = 8;

    final class AllStories extends JUnitThucydidesStories {}

    @Test
    public void all_stories_on_the_classpath_should_be_run_by_default() throws Throwable {

        // Given
        JUnitThucydidesStories stories = new AllStories();
        stories.setSystemConfiguration(systemConfiguration);
        stories.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(stories);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(TOTAL_NUMBER_OF_JBEHAVE_SCENARIOS));
    }

    final class StoriesInTheSubsetFolder extends JUnitThucydidesStories {
        public void configure() {
            findStoriesIn("stories/subset");
        }
    }

    @Test
    public void a_subset_of_the_stories_can_be_run_individually() throws Throwable {

        // Given
        JUnitThucydidesStories stories = new StoriesInTheSubsetFolder();
        stories.setSystemConfiguration(systemConfiguration);
        stories.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(stories);

        // Then

        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(2));
    }

    final class SomePassingStories extends JUnitThucydidesStories {
        public void configure() {
            findStoriesCalled("*PassingStory.story");
        }
    }
    @Test
    public void stories_with_a_matching_name_can_be_run() throws Throwable {

        // Given
        JUnitThucydidesStories stories = new SomePassingStories();
        stories.setSystemConfiguration(systemConfiguration);
        stories.configuredEmbedder().configuration().storyReporterBuilder().withReporters(printOutput);

        // When
        run(stories);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();
        assertThat(outcomes.size(), is(3));
    }

}
