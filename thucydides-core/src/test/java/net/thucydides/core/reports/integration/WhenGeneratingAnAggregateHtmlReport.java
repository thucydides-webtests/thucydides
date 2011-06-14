package net.thucydides.core.reports.integration;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenGeneratingAnAggregateHtmlReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private HtmlAggregateStoryReporter reporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        reporter = new HtmlAggregateStoryReporter();
        outputDirectory = temporaryDirectory.newFolder("target/thucydides");
        reporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_generate_an_aggregate_story_report() throws Exception {

        File sourceDirectory = new File("src/test/resources/featured-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File htmlStoryReport = new File(outputDirectory,"stories.html");
        assertThat(htmlStoryReport.exists(), is(true));
    }

    @Test
    public void should_generate_an_aggregate_feature_report() throws Exception {

        File sourceDirectory = new File("src/test/resources/featured-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File htmlStoryReport = new File(outputDirectory,"features.html");
        assertThat(htmlStoryReport.exists(), is(true));
    }

}
