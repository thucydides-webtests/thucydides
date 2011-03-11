package net.thucydides.core.reports.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.List;

import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.model.loaders.UserStoryLoader;
import net.thucydides.core.reports.UserStoryTestReporter;
import net.thucydides.core.reports.html.HtmlUserStoryTestReporter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingAUserStoryHtmlReport {

    @Rule 
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();
    
    private UserStoryTestReporter reporter;

    private File outputDirectory;
    
    @Before
    public void setupTestReporter() {
        reporter = new HtmlUserStoryTestReporter();
        outputDirectory = temporaryDirectory.newFolder("temp");
        reporter.setOutputDirectory(outputDirectory);
    }
    
    @Test
    public void should_write_aggregate_reports_to_output_directory()
            throws Exception {
        
        File sourceDirectory = new File("src/test/resources/single-user-story-reports");

        UserStoryLoader loader = new UserStoryLoader();
        List<UserStoryTestResults> userStoryResults = loader.loadStoriesFrom(sourceDirectory);

        reporter.setSourceDirectory(sourceDirectory);
        File userStoryReport = reporter.generateReportFor(userStoryResults.get(0));
        assertThat(userStoryReport.exists(), is(true));
    }
}
