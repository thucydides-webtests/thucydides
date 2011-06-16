package net.thucydides.core.reports.html;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.UserStoriesResultSet;
import net.thucydides.core.model.features.FeatureLoader;
import net.thucydides.core.model.userstories.UserStoryLoader;
import net.thucydides.core.reports.UserStoryTestReporter;
import org.apache.velocity.Template;
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
    private static final String HOME_TEMPLATE_PATH = "velocity/home.vm";
    private FeatureLoader featureLoader;
    private UserStoryLoader storyLoader;

    public HtmlAggregateStoryReporter() {
        setTemplatePath(DEFAULT_USER_STORY_TEMPLATE);
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
        String htmlContents = mergeVelocityTemplate(context);

        copyResourcesToOutputDirectory();

        String reportFilename = storyTestResults.getReportName(HTML);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
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
        context.put("features", featureResults);
        Template featuresTemplate = getTemplateManager().getTemplateFrom(FEATURES_TEMPLATE_PATH);
        LOGGER.debug("Generating features page");
        String htmlContents = mergeVelocityTemplate(featuresTemplate, context);
        LOGGER.debug("Writing features page");
        writeReportToOutputDirectory("features.html", htmlContents);

        for(FeatureResults feature : featureResults) {
            generateStoryReportForFeature(feature);
        }
    }

    private void generateStoryReportForFeature(FeatureResults feature) throws IOException {
        VelocityContext context = new VelocityContext();

        context.put("stories", feature.getStoryResults());
        context.put("storyContext", feature.getFeature().getName() );
        Template storyTemplate = getTemplateManager().getTemplateFrom(STORIES_TEMPLATE_PATH);
        LOGGER.debug("Generating stories page");
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        LOGGER.debug("Writing stories page");
        String filename = feature.getStoryReportName();
        writeReportToOutputDirectory(filename, htmlContents);
    }

    private void generateStoriesReport(final List<StoryTestResults> storyResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", storyResults);
        context.put("storyContext", "All stories");
        Template storyTemplate = getTemplateManager().getTemplateFrom(STORIES_TEMPLATE_PATH);
        LOGGER.debug("Generating stories page");
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        LOGGER.debug("Writing stories page");
        writeReportToOutputDirectory("stories.html", htmlContents);
    }

    private void generateReportHomePage(final List<StoryTestResults> storyResults,
                                        final List<FeatureResults> featureResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", new UserStoriesResultSet(storyResults));
        context.put("features", featureResults);
        Template storyTemplate = getTemplateManager().getTemplateFrom(HOME_TEMPLATE_PATH);
        LOGGER.debug("Generating home page");
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        LOGGER.debug("Writing stories page");
        writeReportToOutputDirectory("home.html", htmlContents);
    }
}
