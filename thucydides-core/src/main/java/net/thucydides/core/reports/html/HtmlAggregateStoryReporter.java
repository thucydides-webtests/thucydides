package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.UserStoryTestReporter;
import net.thucydides.core.reports.history.TestHistoryInDatabase;
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

/**
 * Generates an aggregate acceptance test report in HTML form.
 * Reads all the reports from the output directory and generates aggregate HTML reports
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private static final String HISTORY_TEMPLATE_PATH = "freemarker/history.ftl";
    private static final String COVERAGE_DATA_TEMPLATE_PATH = "freemarker/coverage.ftl";
    private static final String PROGRESS_DATA_TEMPLATE_PATH = "freemarker/progress.ftl";
    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";
    private static final String TAGTYPE_TEMPLATE_PATH = "freemarker/results-by-tagtype.ftl";

    private TestHistoryInDatabase testHistory;
    private String projectName;
    private ReportNameProvider reportNameProvider;

    private final IssueTracking issueTracking;

    public HtmlAggregateStoryReporter(final String projectName) {
        this(projectName, Injectors.getInjector().getInstance(IssueTracking.class));
    }

    public HtmlAggregateStoryReporter(final String projectName, final IssueTracking issueTracking) {
        this.projectName = projectName;
        this.issueTracking = issueTracking;
        this.reportNameProvider = new ReportNameProvider();
    }

    public String getProjectName() {
        return projectName;
    }

    protected TestHistoryInDatabase getTestHistory() {
        if (testHistory == null) {
            testHistory = new TestHistoryInDatabase();
        }
        return testHistory;
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter(issueTracking);
        context.put("formatter", formatter);
        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
    }

    public TestOutcomes generateReportsForTestResultsFrom(final File sourceDirectory) throws IOException {
        TestOutcomes allTestOutcomes = loadTestOutcomesFrom(sourceDirectory);
        copyResourcesToOutputDirectory();

        generateAggregateReportFor(allTestOutcomes);
        generateTagReportsFor(allTestOutcomes);
        generateTagTypeReportsFor(allTestOutcomes);
        generateResultReportsFor(allTestOutcomes);
        generateHistoryReportFor(allTestOutcomes);
        generateCoverageReportsFor(allTestOutcomes);

        return allTestOutcomes;
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

        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportNameProvider, tag);
            generateAssociatedTagReportsForTag(testOutcomes.withTag(tag.getName()), tag.getName());
        }
    }

    private void generateTagTypeReportsFor(TestOutcomes testOutcomes) throws IOException {

        for (String tagType : testOutcomes.getTagTypes()) {
            generateTagTypeReport(testOutcomes, reportNameProvider, tagType);
        }
    }

    private void generateResultReportsFor(TestOutcomes testOutcomes) throws IOException {
        generateResultReports(testOutcomes, reportNameProvider, "");

        for (TestTag tag : testOutcomes.getTags()) {
            generateResultReports(testOutcomes.withTag(tag.getName()), new ReportNameProvider(tag.getName()), tag.getType());
        }
    }

    private void generateCoverageReportsFor(TestOutcomes testOutcomes) throws IOException {

        for (String tagType : testOutcomes.getTagTypes()) {
            generateCoverageData(testOutcomes, tagType);
        }
    }

    private void generateResultReports(TestOutcomes testOutcomesForThisTag, ReportNameProvider reportName, String tagType) throws IOException {
        if (testOutcomesForThisTag.getSuccessCount() > 0) {
            generateResultReport(testOutcomesForThisTag.getPassingTests(), reportName, tagType, "success");
        }
        if (testOutcomesForThisTag.getPendingCount() > 0) {
            generateResultReport(testOutcomesForThisTag.getPendingTests(), reportName, tagType, "pending");
        }
        if (testOutcomesForThisTag.getFailureCount() > 0) {
            generateResultReport(testOutcomesForThisTag.getFailingTests(), reportName, tagType, "failure");
        }
    }

    private void generateResultReport(TestOutcomes testOutcomes, ReportNameProvider reportName, String tagType, String testResult) throws IOException {
        Map<String, Object> context = buildContext(testOutcomes, reportName);
        context.put("report", ReportProperties.forTestResultsReport());
        context.put("currentTagType", tagType);
        String report = reportName.forTestResult(testResult);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
    }

    private void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportName, TestTag tag) throws IOException {
        TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag.getName());
        Map<String, Object> context = buildContext(testOutcomesForTag, reportName);
        context.put("report", ReportProperties.forTagResultsReport());
        context.put("currentTagType", tag.getType());
        String report = reportName.forTag(tag.getName());
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
    }

    private void generateTagTypeReport(TestOutcomes testOutcomes, ReportNameProvider reportName, String tagType) throws IOException {
        TestOutcomes testOutcomesForTagType = testOutcomes.withTagType(tagType);

        Map<String, Object> context = buildContext(testOutcomesForTagType, reportName);
        context.put("report", ReportProperties.forTagTypeResultsReport());
        context.put("tagType", tagType);

        String report = reportName.forTagType(tagType);
        generateReportPage(context, TAGTYPE_TEMPLATE_PATH, report);
    }

    private void generateAssociatedTagReportsForTag(TestOutcomes testOutcomes, String sourceTag) throws IOException {
        ReportNameProvider reportName = new ReportNameProvider(sourceTag);
        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportName, tag);
        }
    }

    private Map<String, Object> buildContext(TestOutcomes testOutcomesForTagType,
                                             ReportNameProvider reportName) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("testOutcomes", testOutcomesForTagType);
        context.put("allTestOutcomes", testOutcomesForTagType.getRootOutcomes());
        context.put("reportName", reportName);
        addFormattersToContext(context);
        return context;
    }

    private void updateHistoryFor(final TestOutcomes testOutcomes) {
        getTestHistory().updateData(testOutcomes);
    }

    private void generateHistoryReportFor(TestOutcomes testOutcomes) throws IOException {
        updateHistoryFor(testOutcomes);

        List<TestResultSnapshot> history = getTestHistory().getHistory();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("history", history);
        context.put("allTestOutcomes", testOutcomes);
        context.put("reportName", reportNameProvider);
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

    private void generateCoverageData(final TestOutcomes testOutcomes, String tagType) throws IOException {
        Map<String, Object> context = new HashMap<String, Object>();

        JSONResultTree resultTree = new JSONResultTree();
        for (String tag : testOutcomes.getTagsOfType(tagType)) {
            resultTree.addTestOutcomesForTag(tag, testOutcomes.withTag(tag));
        }

        context.put("coverageData", resultTree.toJSON());
        addFormattersToContext(context);

        String javascriptCoverageData = mergeTemplate(COVERAGE_DATA_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory(tagType + "-coverage.js", javascriptCoverageData);
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