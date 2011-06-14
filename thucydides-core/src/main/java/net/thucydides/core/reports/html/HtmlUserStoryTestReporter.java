package net.thucydides.core.reports.html;

import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.UserStoriesResultSet;
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
public class HtmlUserStoryTestReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final String DEFAULT_USER_STORY_TEMPLATE = "velocity/user-story.vm";

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlUserStoryTestReporter.class);
    private static final String STORIES_TEMPLATE_PATH = "velocity/stories.vm";
    private static final String HOME_TEMPLATE_PATH = "velocity/home.vm";

    public HtmlUserStoryTestReporter() {
        setTemplatePath(DEFAULT_USER_STORY_TEMPLATE);
    }
    
    /**
     * Generate aggregate XML reports for the test run reports in the output directory.
     * Returns the list of
     */
    public File generateReportFor(final StoryTestResults storyTestResults) throws IOException {
        
        LOGGER.info("Generating report for user story "
                    + storyTestResults.getTitle() + " to " + getOutputDirectory());

        System.out.println("storyTestResults outcome count = " + storyTestResults.getTestOutcomes().size());
        VelocityContext context = new VelocityContext();
        context.put("story", storyTestResults);
        String htmlContents = mergeVelocityTemplate(context);
        System.out.println("htmlContents = " + htmlContents);

        copyResourcesToOutputDirectory();

        String reportFilename = storyTestResults.getReportName(HTML);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    public void generateReportsForStoriesFrom(final File sourceDirectory) throws IOException {
        UserStoryLoader loader = new UserStoryLoader();
        List<StoryTestResults> storyResults = loader.loadStoriesFrom(sourceDirectory);
        
        copyResourcesToOutputDirectory();

        for(StoryTestResults storyTestResults : storyResults) {
            generateReportFor(storyTestResults);
        }

        generateStoriesReportFor(storyResults);
    }

    private void generateStoriesReportFor(final List<StoryTestResults> storyResults) throws IOException {
        LOGGER.info("Generating summary report for user stories to "+ getOutputDirectory());

        copyResourcesToOutputDirectory();

        generateStoriesReport(storyResults);
        generateReportHomePage(storyResults);
    }

    private void generateStoriesReport(final List<StoryTestResults> storyResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", storyResults);
        Template storyTemplate = getTemplateManager().getTemplateFrom(STORIES_TEMPLATE_PATH);
        LOGGER.debug("Generating stories page");
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        LOGGER.debug("Writing stories page");
        writeReportToOutputDirectory("stories.html", htmlContents);
    }

    private void generateReportHomePage(final List<StoryTestResults> storyResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", new UserStoriesResultSet(storyResults));
        Template storyTemplate = getTemplateManager().getTemplateFrom(HOME_TEMPLATE_PATH);
        LOGGER.debug("Generating home page");
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        LOGGER.debug("Writing stories page");
        writeReportToOutputDirectory("home.html", htmlContents);
    }
}
