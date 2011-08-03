package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.UserStoriesResultSet;
import net.thucydides.core.model.features.FeatureLoader;
import net.thucydides.core.model.userstories.UserStoryLoader;
import net.thucydides.core.reports.UserStoryTestReporter;
import net.thucydides.core.reports.json.JSONResultTree;
import net.thucydides.core.reports.json.JSONProgressResultTree;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

/**
 * Generates an aggregate acceptance test report in XML form. Reads all the
 * reports from the output directory and generates an aggregate report
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final String DEFAULT_USER_STORY_TEMPLATE = "velocity/user-story.vm";

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);
    private static final String STORIES_TEMPLATE_PATH = "velocity/stories.vm";
    private static final String FEATURES_TEMPLATE_PATH = "velocity/features.vm";
    private static final String COVERAGE_DATA_TEMPLATE_PATH = "velocity/coverage.vm";
    private static final String PROGRESS_DATA_TEMPLATE_PATH = "velocity/progress.vm";
    private static final String HOME_TEMPLATE_PATH = "velocity/index.vm";
    private static final String DASHBOARD_TEMPLATE_PATH = "velocity/dashboard.vm";
    private FeatureLoader featureLoader;
    private UserStoryLoader storyLoader;
    private String issueTrackerUrl;

    public HtmlAggregateStoryReporter() {
        storyLoader = new UserStoryLoader();
        featureLoader = new FeatureLoader();
    }
    
    /**
     * Generate aggregate XML reports for the test run reports in the output directory.
     * Returns the list of
     */
    public File generateReportFor(final StoryTestResults storyTestResults) throws IOException {
        
        LOGGER.info("Generating report for user story "
                    + storyTestResults.getTitle() + " to " + getOutputDirectory());

        VelocityContext context = new VelocityContext();
        context.put("story", storyTestResults);
        addFormatterToContext(context);
        String htmlContents = mergeTemplate(DEFAULT_USER_STORY_TEMPLATE).usingContext(context);

        copyResourcesToOutputDirectory();

        String reportFilename = storyTestResults.getReportName(HTML);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private void addFormatterToContext(VelocityContext context) {
        Formatter formatter = new Formatter(ThucydidesSystemProperty.getValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL));
        context.put("formatter", formatter);
    }

    public void generateReportsForStoriesFrom(final File sourceDirectory) throws IOException {
        List<StoryTestResults> storyResults = loadStoryResultsFrom(sourceDirectory);
        List<FeatureResults> featureResults = loadFeatureResultsFrom(sourceDirectory);

        copyResourcesToOutputDirectory();

        for(StoryTestResults storyTestResults : storyResults) {
            generateReportFor(storyTestResults);
        }

        generateAggregateReportFor(storyResults, featureResults);
    }

    private List<StoryTestResults> loadStoryResultsFrom(final File sourceDirectory) throws IOException {
        return storyLoader.loadFrom(sourceDirectory);
    }

    private List<FeatureResults> loadFeatureResultsFrom(final File sourceDirectory) throws IOException {
        return featureLoader.loadFrom(sourceDirectory);
    }

    private void generateAggregateReportFor(final List<StoryTestResults> storyResults,
                                            final List<FeatureResults> featureResults) throws IOException {
        LOGGER.info("Generating summary report for user stories to "+ getOutputDirectory());

        copyResourcesToOutputDirectory();

        generateStoriesReport(storyResults);
        generateFeatureReport(featureResults);
        generateReportHomePage(storyResults, featureResults);
    }

    private void generateFeatureReport(final List<FeatureResults> featureResults) throws IOException {
        VelocityContext context = new VelocityContext();
        addFormatterToContext(context);
        context.put("features", featureResults);
        String htmlContents = mergeTemplate(FEATURES_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory("features.html", htmlContents);

        for(FeatureResults feature : featureResults) {
            generateStoryReportForFeature(feature);
        }
    }

    private void generateStoryReportForFeature(FeatureResults feature) throws IOException {
        VelocityContext context = new VelocityContext();

        context.put("stories", feature.getStoryResults());
        context.put("storyContext", feature.getFeature().getName() );
        addFormatterToContext(context);
        LOGGER.debug("Generating stories page");
        String htmlContents = mergeTemplate(STORIES_TEMPLATE_PATH).usingContext(context);
        LOGGER.debug("Writing stories page");
        String filename = feature.getStoryReportName();
        writeReportToOutputDirectory(filename, htmlContents);
    }

    private void generateStoriesReport(final List<StoryTestResults> storyResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", storyResults);
        context.put("storyContext", "All stories");
        addFormatterToContext(context);
        String htmlContents = mergeTemplate(STORIES_TEMPLATE_PATH).usingContext(context);
        LOGGER.debug("Writing stories page");
        writeReportToOutputDirectory("stories.html", htmlContents);
    }

    private void generateReportHomePage(final List<StoryTestResults> storyResults,
                                        final List<FeatureResults> featureResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", new UserStoriesResultSet(storyResults));
        context.put("features", featureResults);
        addFormatterToContext(context);

        LOGGER.debug("Generating report pages");
        generateReportPage(context, HOME_TEMPLATE_PATH, "index.html");
        generateReportPage(context, DASHBOARD_TEMPLATE_PATH, "dashboard.html");

        LOGGER.debug("Generating coverage data");
        generateCoverageData(featureResults);
        generateProgressData(featureResults);
    }

    private void generateReportPage(final VelocityContext context,
                                    final String template,
                                    final String outputFile) throws IOException {
        String htmlContents = mergeTemplate(template).usingContext(context);
        writeReportToOutputDirectory(outputFile, htmlContents);
    }

    private void generateCoverageData(final List<FeatureResults> featureResults) throws IOException {
        VelocityContext context = new VelocityContext();

        JSONResultTree resultTree = new JSONResultTree();
        for(FeatureResults feature : featureResults) {
            resultTree.addFeature(feature);
        }

        context.put("coverageData", resultTree.toJSON());
        addFormatterToContext(context);

        String javascriptCoverageData = mergeTemplate(COVERAGE_DATA_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory("coverage.js", javascriptCoverageData);
    }

    private void generateProgressData(final List<FeatureResults> featureResults) throws IOException {
        VelocityContext context = new VelocityContext();

        JSONProgressResultTree resultTree = new JSONProgressResultTree();
        for(FeatureResults feature : featureResults) {
            resultTree.addFeature(feature);
        }

        context.put("progressData", resultTree.toJSON());
        addFormatterToContext(context);

        String javascriptCoverageData = mergeTemplate(PROGRESS_DATA_TEMPLATE_PATH).usingContext(context);
        writeReportToOutputDirectory("progress.js", javascriptCoverageData);
    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        this.issueTrackerUrl = issueTrackerUrl;
        if (issueTrackerUrl != null) {
            ThucydidesSystemProperty.setValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL, issueTrackerUrl);
        }
    }
}
