package net.thucydides.core.reports.integration;

import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.reports.history.TestHistoryInDatabase;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

public class WhenGeneratingAnAggregateHtmlReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private HtmlAggregateStoryReporter reporter;

    private File outputDirectory;

    WebDriver driver;
    
    @Mock
    TestHistoryInDatabase testHistory;

    @Before
    public void setupTestReporter() {
        MockitoAnnotations.initMocks(this);
        reporter = new HtmlAggregateStoryReporter("project");
        outputDirectory = temporaryDirectory.newFolder("target/site/thucydides");
        reporter.setOutputDirectory(outputDirectory);

        driver = new HtmlUnitDriver();
    }

    @Test
    public void should_aggregate_dashboard_should_contain_a_list_of_features_and_stories_for_legacy_tests() throws Exception {

        File sourceDirectory = directoryInClasspathCalled("/test-outcomes/containing-features-and-stories");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);

        File report = new File(outputDirectory,"index.html");

        String reportUrl = "file:///" + report.getAbsolutePath();
        driver.get(reportUrl);

        List<WebElement> tagTypes = driver.findElements(By.cssSelector(".tagTitle"));
        List<String> tagTypeNames = extract(tagTypes, on(WebElement.class).getText());
        assertThat(tagTypeNames, hasItems("A User Story In A Feature","A Feature"));
    }

    @Mock ThucydidesSystemProperties systemProperties;

    class CustomHtmlAggregateStoryReporter extends HtmlAggregateStoryReporter {

        public CustomHtmlAggregateStoryReporter(final String projectName) {
            super(projectName);
        }

        @Override
        protected ThucydidesSystemProperties getSystemProperties() {
            return systemProperties;
        }
    }

    @Test
    public void should_pass_jira_url_to_reporter() throws Exception {

        CustomHtmlAggregateStoryReporter customReport = new CustomHtmlAggregateStoryReporter("project");
        customReport.setJiraUrl("http://my.jira.url");

        verify(systemProperties).setValue(ThucydidesSystemProperty.JIRA_URL,"http://my.jira.url");
    }

    @Test
    public void should_pass_jira_project_to_reporter() throws Exception {

        CustomHtmlAggregateStoryReporter customReport = new CustomHtmlAggregateStoryReporter("project");
        customReport.setJiraProject("MYPROJECT");

        verify(systemProperties).setValue(ThucydidesSystemProperty.JIRA_PROJECT,"MYPROJECT");
    }

    @Test
    public void should_pass_issue_tracker_to_reporter() throws Exception {

        CustomHtmlAggregateStoryReporter customReport = new CustomHtmlAggregateStoryReporter("project");
        customReport.setIssueTrackerUrl("http://my.issue.tracker");

        verify(systemProperties).setValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL,"http://my.issue.tracker");
    }

    @Test
    public void should_be_able_to_clear_history() throws Exception {

        reporter = new HtmlAggregateStoryReporter("project") {
            @Override
            protected TestHistoryInDatabase getTestHistory() {
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
