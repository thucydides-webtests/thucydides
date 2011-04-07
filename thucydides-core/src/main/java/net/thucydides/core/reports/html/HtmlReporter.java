package net.thucydides.core.reports.html;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import net.thucydides.core.ThucydidesSystemProperty;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An HTML report generates reports in a given directory and uses resources (images,...) from another.
 * @author johnsmart
 *
 */
public abstract class HtmlReporter {

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";
    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    private File outputDirectory;
    private final TemplateManager templateManager = new TemplateManager();

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReporter.class);

    private String templatePath;
    
    public HtmlReporter() {
        super();
    }

    public Template getTemplate() {
        return templateManager.getTemplateFrom(getTemplatePath());        
    }

    protected TemplateManager getTemplateManager() {
        return templateManager;
    }
    
    public String getTemplatePath() {
        return templatePath;
    }
    
    public void setTemplatePath(final String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * HTML reports will be generated here.
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }
    
    public String getResourceDirectory() {
        return resourceDirectory;
    }

    protected void copyResourcesToOutputDirectory() throws IOException {
        updateResourceDirectoryFromSystemPropertyIfDefined();
        HtmlResourceCopier copier = new HtmlResourceCopier(getResourceDirectory());

        copier.copyHTMLResourcesTo(getOutputDirectory());
    }

    private void updateResourceDirectoryFromSystemPropertyIfDefined() {
        String systemDefinedResourceDirectory 
                = System.getProperty(ThucydidesSystemProperty.REPORT_RESOURCE_PATH.getPropertyName());
        if (systemDefinedResourceDirectory != null) {
            setResourceDirectory(systemDefinedResourceDirectory);
        }
    }

    /**
     * Merge a velocity template using a provided velocity context.
     */
    protected String mergeVelocityTemplate(final Template template, final VelocityContext context) {
        String htmlContents = "";
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        htmlContents = sw.toString();
        return htmlContents;
    }
    /**
     * Merge a velocity template using a provided velocity context.
     */
    protected String mergeVelocityTemplate(final VelocityContext context) {
        return mergeVelocityTemplate(getTemplate(), context);
    }

    /**
     * Write the actual HTML report to a file with the specified name in the output directory.
     */
    protected File writeReportToOutputDirectory(final String reportFilename, 
                                                final String htmlContents) throws IOException {
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents);
        LOGGER.debug("Writing HTML report to " + report.getAbsolutePath());
        return report;
    }

}