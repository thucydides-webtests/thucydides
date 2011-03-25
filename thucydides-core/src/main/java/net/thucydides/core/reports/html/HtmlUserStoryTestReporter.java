package net.thucydides.core.reports.html;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.model.UserStoriesResultSet;
import net.thucydides.core.model.loaders.UserStoryLoader;
import net.thucydides.core.reports.UserStoryTestReporter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public File generateReportFor(final UserStoryTestResults userStoryTestResults) throws IOException {
        
        LOGGER.info("Generating report for user story " 
                    + userStoryTestResults.getTitle() + " to " + getOutputDirectory());
        
        VelocityContext context = new VelocityContext();
        context.put("story", userStoryTestResults);
        String htmlContents = mergeVelocityTemplate(context);

        copyResourcesToOutputDirectory();

        String reportFilename = userStoryTestResults.getReportName(HTML);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    public void generateReportsForStoriesFrom(final File sourceDirectory) throws IOException {
        UserStoryLoader loader = new UserStoryLoader();
        List<UserStoryTestResults> userStoryResults = loader.loadStoriesFrom(sourceDirectory);
        
        copyResourcesToOutputDirectory();

        for(UserStoryTestResults userStoryTestResults : userStoryResults) {
            generateReportFor(userStoryTestResults);
        }

        generateStoriesReportFor(userStoryResults);
    }

    private void generateStoriesReportFor(List<UserStoryTestResults> userStoryResults) throws IOException {
        for(UserStoryTestResults userStory : userStoryResults) {
            System.out.println("Story: " + userStory.getTitle());
        }

        LOGGER.info("Generating summary report for user stories to "+ getOutputDirectory());

        copyResourcesToOutputDirectory();

        generateStoriesReport(userStoryResults);
        generateReportHomePage(userStoryResults);
    }

    private void generateStoriesReport(List<UserStoryTestResults> userStoryResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", userStoryResults);
        Template storyTemplate = templateManager.getTemplateFrom(STORIES_TEMPLATE_PATH);
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        writeReportToOutputDirectory("stories.html", htmlContents);
    }

    private void generateReportHomePage(List<UserStoryTestResults> userStoryResults) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("stories", new UserStoriesResultSet(userStoryResults));
        Template storyTemplate = templateManager.getTemplateFrom(HOME_TEMPLATE_PATH);
        String htmlContents = mergeVelocityTemplate(storyTemplate, context);
        writeReportToOutputDirectory("home.html", htmlContents);
    }
}
