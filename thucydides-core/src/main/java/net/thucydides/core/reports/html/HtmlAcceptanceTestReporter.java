package net.thucydides.core.reports.html;

import static net.thucydides.core.reports.ReportNamer.ReportType.HTML;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportNamer;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.google.common.base.Preconditions;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class HtmlAcceptanceTestReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";

    private File outputDirectory;

    private ReportNamer reportNamer = new ReportNamer(HTML);

    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    
    private TemplateManager templateManager = new TemplateManager();
    
    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    /**
     * Generate an XML report for a given test run.
     */
    public File generateReportFor(final AcceptanceTestRun testRun) throws IOException {

        Preconditions.checkNotNull(outputDirectory);

        String htmlContents = "";
        Template template = getTemplate();
        VelocityContext context = new VelocityContext();
        context.put("testrun", testRun);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        htmlContents = sw.toString();

        String reportFilename = reportNamer.getNormalizedTestNameFor(testRun);
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents);

        HtmlResourceCopier copier = new HtmlResourceCopier(resourceDirectory);
        copier.copyHTMLResourcesTo(getOutputDirectory());
        return report;
    }

    private Template getTemplate() {
        return templateManager.getTemplateFrom("velocity/default.vm");
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

}
