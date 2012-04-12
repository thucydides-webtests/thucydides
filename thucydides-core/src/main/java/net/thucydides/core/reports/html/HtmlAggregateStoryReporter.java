package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.UserStoriesResultSet;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.UserStoryTestReporter;
import net.thucydides.core.reports.history.TestHistory;
import net.thucydides.core.reports.html.history.TestResultSnapshot;
import net.thucydides.core.reports.json.JSONProgressResultTree;
import net.thucydides.core.reports.json.JSONResultTree;
import net.thucydides.core.util.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.model.ReportType.HTML;

/**
 * Generates an aggregate acceptance test report in XML form. Reads all the
 * reports from the output directory and generates an aggregate report
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final String DEFAULT_USER_STORY_TEMPLATE = "freemarker/user-story.ftl";

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);
    private static final String STORIES_TEMPLATE_PATH = "freemarker/stories.ftl";
    private static final String HISTORY_TEMPLATE_PATH = "freemarker/history.ftl";
    private static final String FEATURES_TEMPLATE_PATH = "freemarker/features.ftl";
    private static final String COVERAGE_DATA_TEMPLATE_PATH = "freemarker/coverage.ftl";
    private static final String PROGRESS_DATA_TEMPLATE_PATH = "freemarker/progress.ftl";
    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";
    private static final String TREEMAP_TEMPLATE_PATH = "freemarker/treemap.ftl";
    private static final String DASHBOARD_TEMPLATE_PATH = "freemarker/dashboard.ftl";
    private TestHistory testHistory;
    private String projectName;

    private final IssueTracking issueTracking;

    public HtmlAggregateStoryReporter(final String projectName) {
        this(projectName, Injectors.getInjector().getInstance(IssueTracking.class));
    }

    public HtmlAggregateStoryReporter(final String projectName, final IssueTracking issueTracking) {
        this.projectName = projectName;
        this.issueTracking = issueTracking;
    }

    public String getProjectName() {
        return projectName;
    }

    protected TestHistory getTestHistory() {
        if (testHistory == null) {
            testHistory = new TestHistory(getProjectName());
        }
        return testHistory;
    }

    /**
     * Generate aggregate XML reports for the test run reports in the output directory.
     * Returns the list of
     */
    public File generateReportFor(final StoryTestResults storyTestResults) throws IOException {

        LOGGER.info("Generating report for user story {} to {}", storyTestResults.getTitle(), getOutputDirectory());

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("story", storyTestResults);
        addFormattersToContext(context);
        String htmlContents = mergeTemplate(DEFAULT_USER_STORY_TEMPLATE).usingContext(context);

        copyResourcesToOutputDirectory();

        String reportFilename = storyTestResults.getReportName(HTML);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter(issueTracking);
        context.put("formatter", formatter);
        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
    }

    public TestOutcomes generateReportsForTestResultsFrom(final File sourceDirectory) throws IOException {
        TestOutcomes testOutcomes = loadTestOutcomesFrom(sourceDirectory);

        copyResourcesToOutputDirectory();

        generateAggregateReportFor(testOutcomes);
        generateTagReportsFor(testOutcomes);
        generateResultReportsFor(testOutcomes);
        generateHistoryReport();

        return testOutcomes;
    }

    private TestOutcomes loadTestOutcomesFrom(File sourceDirectory) throws IOException {
        return TestOutcomeLoader.testOutcomesIn(sourceDirectory).withHistory();
    }

    private void generateAggregateReportFor(TestOutcomes testOutcomes) throws IOException {

        ReportNameProvider defaultNameProvider = new ReportNameProvider();
        Map<String, Object> context = buildContext(testOutcomes, defaultNameProvider);
        context.put("report", ReportProperties.forAggregateResultsReport());
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, "index.html");
    }

    private void generateTagReportsFor(TestOutcomes testOutcomes) throws IOException {
        generateTagReportsFor(testOutcomes, new ReportNameProvider());
    }

    private void generateResultReportsFor(TestOutcomes testOutcomes) throws IOException {
        generateResultReportsFor(testOutcomes, new ReportNameProvider());
    }

    private void generateTagReportsFor(TestOutcomes testOutcomes, ReportNameProvider reportName) throws IOException {

        for (String tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportName, tag);
            generateAssociatedTagReportsForTag(testOutcomes.withTag(tag), tag);
        }
    }

    private void generateResultReportsFor(TestOutcomes testOutcomes, ReportNameProvider reportName) throws IOException {
        generateResultReports(testOutcomes, reportName);

        for (String tag : testOutcomes.getTags()) {
            generateResultReports(testOutcomes.withTag(tag), new ReportNameProvider(tag));
        }
    }

    private void generateResultReports(TestOutcomes testOutcomesForThisTag, ReportNameProvider reportName) throws IOException {
        if (testOutcomesForThisTag.getSuccessCount() > 0) {
            generateResultReport(testOutcomesForThisTag.getPassingTests(), reportName, "success");
        }
        if (testOutcomesForThisTag.getPendingCount() > 0) {
            generateResultReport(testOutcomesForThisTag.getPendingTests(), reportName, "pending");
        }
        if (testOutcomesForThisTag.getFailureCount() > 0) {
            generateResultReport(testOutcomesForThisTag.getFailingTests(), reportName, "failure");
        }
    }

    private void generateResultReport(TestOutcomes testOutcomes, ReportNameProvider reportName, String testResult) throws IOException {
        Map<String, Object> context = buildContext(testOutcomes, reportName);
        context.put("report", ReportProperties.forTestResultsReport());
        String report = reportName.forTestResult(testResult);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
    }

    private void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportName, String tag) throws IOException {
        TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag);
        Map<String, Object> context = buildContext(testOutcomesForTag, reportName);
        context.put("report", ReportProperties.forTagResultsReport());
        String report = reportName.forTag(tag);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
    }

    private void generateAssociatedTagReportsForTag(TestOutcomes testOutcomes, String sourceTag) throws IOException {
        ReportNameProvider reportName = new ReportNameProvider(sourceTag);
        for (String tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportName, tag);
        }
    }

    private Map<String, Object> buildContext(TestOutcomes testOutcomesForTagType,
                                             ReportNameProvider reportName) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("testOutcomes", testOutcomesForTagType);
        context.put("reportName", reportName);
        addFormattersToContext(context);
        return context;
    }

    private void updateHistoryFor(final List<FeatureResults> featureResults) {
        getTestHistory().updateData(featureResults);
    }

    private void generateHistoryReport() throws IOException {
        List<TestResultSnapshot> history = getTestHistory().getHistory();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("history", history);
        context.put("rowcount", history.size());
        addFormattersToContext(context);
        String htmlContents = mergeTemplate(HISTORY_TEMPLATE_PATH).usingContext(context);
        LOGGER.debug("Writing history page");
        writeReportToOutputDirectory("history.html", htmlContents);

    }

    private void generateReportPage(final Map<String, Object> context,
                                    final String template,
                                    final String outputFile) throws IOException {
        String htmlContents = mergeTemplate(template).usingContext(context);
        writeReportToOutputDirectory(outputFile, htmlContents);
    }

    private void generateCoverageData(final List<FeatureResults> featureResults) throws IOException {
        Map<String, Object> context = new HashMap<String, Object>();

        JSONResultTree resultTree = new JSONResultTree();
        for (FeatureResults feature : featureResults) {
            resultTree.addFeature(feature);
        }

        context.put("coverageData", resultTree.toJSON());
        addFormattersToContext(context);

        String javascriptCoverageData = mergeTemplate(COVERAGE_DATA_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory("coverage.js", javascriptCoverageData);
    }

    private void generateOutcomeData(final TestOutcomes testOutcomes) throws IOException {
        Map<String, Object> context = new HashMap<String, Object>();

        List<String> tagTypes = testOutcomes.getTagTypes();
        for (String tagType : tagTypes) {
            generateOutcomeDataForTagType(tagType, testOutcomes.withTagType(tagType));
        }
    }

    private void generateOutcomeDataForTagType(String tagType, TestOutcomes testOutcomes) throws IOException {
        Map<String, Object> context = new HashMap<String, Object>();

        JSONResultTree resultTree = new JSONResultTree();

        List<String> tags = testOutcomes.getTagsOfType(tagType);
        for(String tag : tags) {
            resultTree.addTestOutcomesForTag(tag, testOutcomes.withTag(tag));
        }

        context.put("coverageData", resultTree.toJSON());
        addFormattersToContext(context);

        String javascriptCoverageData = mergeTemplate(COVERAGE_DATA_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory("coverage-" + tagType +".js", javascriptCoverageData);
    }

    private void generateProgressData(final List<FeatureResults> featureResults) throws IOException {
        Map<String, Object> context = new HashMap<String, Object>();

        JSONProgressResultTree resultTree = new JSONProgressResultTree();
        for (FeatureResults feature : featureResults) {
            resultTree.addFeature(feature);
        }

        context.put("progressData", resultTree.toJSON());
        addFormattersToContext(context);

        String javascriptCoverageData = mergeTemplate(PROGRESS_DATA_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory("progress.js", javascriptCoverageData);
    }

    public void clearHistory() {
        getTestHistory().clearHistory();
    }

    protected ThucydidesSystemProperties getSystemProperties() {
        return ThucydidesSystemProperties.getProperties();
    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        if (issueTrackerUrl != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL, issueTrackerUrl);
        }
    }

    public void setJiraUrl(String jiraUrl) {
        if (jiraUrl != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_URL, jiraUrl);
        }
    }

    public void setJiraProject(String jiraProject) {
        if (jiraProject != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_PROJECT, jiraProject);
        }
    }

}