package net.thucydides.core.reports.html;

import static net.thucydides.core.reports.ReportNamer.ReportType.HTML;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.reports.ReportNamer;
import net.thucydides.core.reports.UserStoryTestReporter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * Generates an aggregate acceptance test report in XML form. Reads all the
 * reports from the output directory and generates an aggregate report
 * summarizing the results.
 */
public class HtmlUserStoryTestReporter extends UserStoryTestReporter {

    private static final String DEFAULT_USER_STORY_TEMPLATE = "velocity/user-story.vm";

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";

    private TemplateManager templateManager = new TemplateManager();

    private ReportNamer reportNamer = new ReportNamer(HTML);

    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    
    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }
    
    /**
     * Generate aggregate XML reports for the test run reports in the output directory.
     * Returns the list of
     */
    @Override
    public File generateReportFor(final UserStoryTestResults userStoryTestResults) throws IOException {
        String htmlContents = "";
        Template template = getTemplate();
        VelocityContext context = new VelocityContext();
        context.put("story", userStoryTestResults);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        htmlContents = sw.toString();

        String reportFilename =  reportNamer.getNormalizedTestNameFor(userStoryTestResults);
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents);

        HtmlResourceCopier copier = new HtmlResourceCopier(resourceDirectory);
        copier.copyHTMLResourcesTo(getOutputDirectory());
        return report;
    }

    private Template getTemplate() {
        return templateManager.getTemplateFrom(DEFAULT_USER_STORY_TEMPLATE);
    }

}
