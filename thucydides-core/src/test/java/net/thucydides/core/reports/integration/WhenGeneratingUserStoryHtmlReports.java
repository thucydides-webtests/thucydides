package net.thucydides.core.reports.integration;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.reports.UserStoryTestReporter;
import net.thucydides.core.reports.html.HtmlUserStoryTestReporter;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingUserStoryHtmlReports {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private UserStory userStory = new UserStory("A User Story", "", "");
    private UserStoryTestResults userStoryTestResults;

    private UserStoryTestReporter reporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        reporter = new HtmlUserStoryTestReporter();
        outputDirectory = temporaryDirectory.newFolder("temp");
        reporter.setOutputDirectory(outputDirectory);

        userStoryTestResults = new UserStoryTestResults(userStory);
        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 1"));
        userStoryTestResults.recordTestRun(thatSucceedsCalled("Test Run 2"));
        userStoryTestResults.recordTestRun(thatFailsCalled("Test Run 3"));
    }

    @Test
    public void should_write_aggregate_reports_to_output_directory() throws Exception {
        File userStoryReport = reporter.generateReportFor(userStoryTestResults);
        assertThat(userStoryReport.exists(), is(true));
    }

    @Test
    public void should_write_aggregate_report_to_a_file_named_after_the_user_story() throws Exception {
        File userStoryReport = reporter.generateReportFor(userStoryTestResults);
        assertThat(userStoryReport.getName(), is("a_user_story.html"));
    }

    @Test
    public void aggregate_report_should_contain_the_user_story_name_as_a_title() throws Exception {
        File userStoryReport = reporter.generateReportFor(userStoryTestResults);
        String reportText = getStringFrom(userStoryReport);
        assertThat(reportText, containsString("A User Story"));
    }

    @Test
    public void aggregate_report_should_contain_links_to_the_test_runs() throws Exception {
        File userStoryReport = reporter.generateReportFor(userStoryTestResults);
        String reportText = getStringFrom(userStoryReport);
        assertThat(reportText, containsString("href=\"a_user_story_test_run_1.html\""));
        assertThat(reportText, containsString("href=\"a_user_story_test_run_2.html\""));
        assertThat(reportText, containsString("href=\"a_user_story_test_run_3.html\""));
    }

    @Test
    public void can_generate_aggregate_reports_from_xml_files_in_a_directory() throws Exception {
        HtmlUserStoryTestReporter reporter = new HtmlUserStoryTestReporter();
        reporter.setOutputDirectory(outputDirectory);  
        File sourceDirectory = new File("src/test/resources/multiple-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);

        File expectedStoryReport1 = new File(outputDirectory, "a_user_story.html");
        assertThat(expectedStoryReport1.exists(), is(true));
        
        File expectedStoryReport2 = new File(outputDirectory, "another_user_story.html");
        assertThat(expectedStoryReport2.exists(), is(true));
        
        File expectedStoryReport3 = new File(outputDirectory, "yet_another_user_story.html");
        assertThat(expectedStoryReport3.exists(), is(true));

        String reportText = getStringFrom(expectedStoryReport3);
        assertThat(reportText, containsString("A third acceptance test run"));
    }
    
    @Test
    public void should_generate_stories_html_report() throws Exception {
        HtmlUserStoryTestReporter reporter = new HtmlUserStoryTestReporter();
        reporter.setOutputDirectory(outputDirectory);  
        File sourceDirectory = new File("src/test/resources/multiple-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);
        File expectedStoryHtmlReport = new File(outputDirectory, "stories.html");
        assertThat(expectedStoryHtmlReport.exists(), is(true));
    }

    @Test
    public void should_copy_resources_to_target_directory() throws Exception {
        HtmlUserStoryTestReporter reporter = new HtmlUserStoryTestReporter();
        reporter.setOutputDirectory(outputDirectory);  
        File sourceDirectory = new File("src/test/resources/multiple-user-story-reports");
        reporter.generateReportsForStoriesFrom(sourceDirectory);
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "core.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void aggregate_failing_story_should_display_failing_icon() throws Exception {
        File userStoryReport = reporter.generateReportFor(userStoryTestResults);
        String reportText = getStringFrom(userStoryReport);
        assertThat(reportText, containsString("fail.png"));
    }

    @Test
    public void aggregate_failing_story_should_display_test_titles() throws Exception {
        File userStoryReport = reporter.generateReportFor(userStoryTestResults);
        String reportText = getStringFrom(userStoryReport);
        assertThat(reportText, containsString("Test Run 1"));
        assertThat(reportText, containsString("Test Run 2"));
        assertThat(reportText, containsString("Test Run 3"));
    }

    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }
    
    private AcceptanceTestRun thatFailsCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(failingTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 3"));
        testRun.setUserStory(userStory);
        return testRun;
    }

    private AcceptanceTestRun thatSucceedsCalled(String title) {
        AcceptanceTestRun testRun = new AcceptanceTestRun(title);
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.setUserStory(userStory);
        return testRun;
    }

}
