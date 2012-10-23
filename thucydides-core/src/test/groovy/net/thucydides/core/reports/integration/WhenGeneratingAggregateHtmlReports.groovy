package net.thucydides.core.reports.integration;


import com.github.goldin.spock.extensions.tempdir.TempDir
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.reports.history.TestHistory

import static org.mockito.Mockito.when
import net.thucydides.core.reports.history.ProgressSnapshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import net.thucydides.core.reports.history.TestResultSnapshot
import org.openqa.selenium.WebElement
import org.junit.Test
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.ThucydidesSystemProperties

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

    /*
    @Rule
    public TemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private HtmlAggregateStoryReporter reporter;

    private File outputDirectory;

    WebDriver driver;

    @Mock
    IssueTracking issueTracking;

    @Mock
    TestHistory testHistory;

    @Before
    public void setupTestReporter() throws IOException {
        MockitoAnnotations.initMocks(this);
        reporter = new HtmlAggregateStoryReporter("project", issueTracking, testHistory);
        outputDirectory = temporaryDirectory.newFolder("target/site/thucydides");
        reporter.setOutputDirectory(outputDirectory);

        driver = new HtmlUnitDriver();
        when(testHistory.getProgress()).thenReturn(new ArrayList<ProgressSnapshot>());
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
        assertThat(tagTypeNames, hasItem("A User Story In A Feature"));
        assertThat(tagTypeNames, hasItem("A Feature"));
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
    */
}