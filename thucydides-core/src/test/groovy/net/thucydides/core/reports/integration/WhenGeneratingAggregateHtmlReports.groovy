package net.thucydides.core.reports.integration
import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.ThucydidesSystemProperties
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.reports.history.ProgressSnapshot
import net.thucydides.core.reports.history.TestHistory
import net.thucydides.core.reports.history.TestResultSnapshot
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled

public class WhenGeneratingAggregateHtmlReports extends Specification {

    @TempDir File temporaryDirectory

    def issueTracking = Mock(IssueTracking)
    def mockTestHistory = Mock(TestHistory)
    def mockSystemProperties = Mock(ThucydidesSystemProperties)

    def reporter = new HtmlAggregateStoryReporter("project", issueTracking, mockTestHistory);

    File outputDirectory
    WebDriver driver;

    def NO_PROGRESS_HISTORY = new ArrayList<ProgressSnapshot>()
    def NO_SNAPSHOTS = new ArrayList<TestResultSnapshot>()

    def setup() {
        outputDirectory = new File(temporaryDirectory,"target/site/thucydides")
        outputDirectory.mkdirs()
        reporter.outputDirectory = outputDirectory;

        driver = new HtmlUnitDriver();
        mockTestHistory.progress >> NO_PROGRESS_HISTORY
        mockTestHistory.history >> NO_SNAPSHOTS
    }

    def "aggregate dashboard should contain a list of features and stories for legacy tests"() {

        given: "We generate reports from a directory containing features and stories only"
            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")

        when: "we view the report"
            driver.get reportHomePageUrl();
            def tagTypeNames = driver.findElements(By.cssSelector(".tagTitle")).collect { it.text}

        then: "The tags should show the features and stories from the tests"
            tagTypeNames.contains "A User Story In A Feature"
            tagTypeNames.contains "A Feature"
    }

    def "we can navigate sub reports"() {
        given: "We generate reports from a directory containing features and stories only"
            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
        when: "we view the report"
            driver.get reportHomePageUrl();
            def tagTypeNames = driver.findElements(By.cssSelector(".tagTitle")).collect { it.text}
        then: "we can see all available tags and click on 'Another Different Feature' link"
            def anotherDifferentFeatureLink = driver.findElement(By.linkText("Another Different Feature"))
            anotherDifferentFeatureLink.click()
            def breadcrumbText = driver.findElement(By.cssSelector(".bluetext")).getText()
            breadcrumbText == "Thucydides Reports> another different feature"
        when: "we click on the Features link"
            def featuresLink = driver.findElement(By.linkText("Features"))
            featuresLink.click()
        then: "we see the breadcrumb showing Thucydedes Reports > another different feature"
            def subReportBreadcrumbText = driver.findElement(By.cssSelector(".bluetext")).getText()
            subReportBreadcrumbText == "Home > Another Different Feature > Features"
        and: "a single feature"
            def featureLink = driver.findElement(By.linkText("Another Different Feature"))
            featureLink.enabled

    }

    def "should pass JIRA URL to reporter"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            customReport.jiraUrl = "http://my.jira.url"
        then:
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.JIRA_URL,"http://my.jira.url")
    }

    def "should pass JIRA project to reporter"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            customReport.jiraProject = "MYPROJECT"
        then:
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.JIRA_PROJECT,"MYPROJECT")
    }

    def "should pass issue tracker to reporter"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            customReport.issueTrackerUrl = "http://my.issue.tracker"
        then:
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL,"http://my.issue.tracker")

    }

    def "should be able to clear history"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            reporter.clearHistory();
        then:
            1 * mockTestHistory.clearHistory()
    }

    class CustomHtmlAggregateStoryReporter extends HtmlAggregateStoryReporter {

        public CustomHtmlAggregateStoryReporter(final String projectName) {
            super(projectName);
        }

        protected TestHistory getTestHistory() {
            return mockTestHistory;
        }
        @Override
        protected ThucydidesSystemProperties getSystemProperties() {
            return mockSystemProperties;
        }
    }

    def reportHomePageUrl() {
        "file:///${reportHomePage.absolutePath}"
    }

    def getReportHomePage() {
        new File(outputDirectory,"index.html")
    }

    def directory(String path) {
        directoryInClasspathCalled(path)
    }
}