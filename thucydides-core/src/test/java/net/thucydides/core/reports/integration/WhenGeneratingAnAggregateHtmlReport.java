package net.thucydides.core.reports.integration;

import net.thucydides.core.reports.history.TestHistory;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

public class WhenGeneratingAnAggregateHtmlReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private HtmlAggregateStoryReporter reporter;

    private File outputDirectory;

    @Mock
    TestHistory testHistory;

    @Before
    public void setupTestReporter() {
        MockitoAnnotations.initMocks(this);
        reporter = new HtmlAggregateStoryReporter("project");
        outputDirectory = temporaryDirectory.newFolder("target/site/thucydides");
        reporter.setOutputDirectory(outputDirectory);
        System.out.println("Writing reports to " + outputDirectory);
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

        File featureReport = new File(outputDirectory,"features.html");
        assertThat(featureReport.exists(), is(true));
    }

    @Test
    public void should_generate_an_aggregate_history_report() throws Exception {

        File sourceDirectory = new File("src/test/resources/featured-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File featureReport = new File(outputDirectory,"history.html");
        assertThat(featureReport.exists(), is(true));
    }

    @Test
    public void should_generate_a_story_report_for_each_feature() throws Exception {

        File sourceDirectory = new File("src/test/resources/featured-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File storyReport1 = new File(outputDirectory,"stories_a_feature.html");
        File storyReport2 = new File(outputDirectory,"stories_another_feature.html");
        File storyReport3 = new File(outputDirectory,"stories_another_different_feature.html");

        assertThat(storyReport1.exists(), is(true));
        assertThat(storyReport2.exists(), is(true));
        assertThat(storyReport3.exists(), is(true));
    }

    @Test
    public void report_dashboard_should_have_links_to_the_stories() throws Exception {

        File sourceDirectory = new File("src/test/resources/featured-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File featureReport = new File(outputDirectory,"features.html");

        String featureReportContents = getStringFrom(featureReport);

        assertThat(featureReportContents, containsString("<a href=\"stories_a_feature.html\""));
        assertThat(featureReportContents, containsString("<a href=\"stories_another_feature.html\""));
        assertThat(featureReportContents, containsString("<a href=\"stories_another_different_feature.html\""));

    }

    @Test
    public void should_generate_functional_coverage_data() throws Exception {

        File sourceDirectory = new File("src/test/resources/featured-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File coverageData = new File(outputDirectory,"coverage.js");
        assertThat(coverageData.exists(), is(true));
    }

    @Test
    public void should_be_able_to_clear_history() throws Exception {

        reporter = new HtmlAggregateStoryReporter("project") {
            @Override
            protected TestHistory getTestHistory() {
                return testHistory;
            }
        };


        reporter.clearHistory();
        verify(testHistory).clearHistory();
    }

    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
