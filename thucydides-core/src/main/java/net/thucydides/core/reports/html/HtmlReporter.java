package net.thucydides.core.reports.html;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import net.thucydides.core.ThucydidesSystemProperty;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.templates.ReportTemplate;
import net.thucydides.core.reports.templates.TemplateManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An HTML report generates reports in a given directory and uses resources (images,...) from another.
 *
 * @author johnsmart
 */
public abstract class HtmlReporter {

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";
    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    private File outputDirectory;
    private TemplateManager templateManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReporter.class);

    public HtmlReporter() {
        super();
        templateManager = Injectors.getInjector().getInstance(TemplateManager.class);
    }

    private TemplateManager getTemplateManager() {
        return templateManager;
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
     * Write the actual HTML report to a file with the specified name in the output directory.
     */
    protected File writeReportToOutputDirectory(final String reportFilename,
                                                final String htmlContents) throws IOException {
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents);
        LOGGER.debug("Writing HTML report to " + report.getAbsolutePath());
        return report;
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }

    protected class Merger {
        final String templateFile;

        public Merger(final String templateFile) {
            this.templateFile = templateFile;
        }

        public String usingContext(final Map<String, Object> context) {
            try {
                ReportTemplate template = getTemplateManager().getTemplateFrom(templateFile);
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge template", e);
            }
        }
    }

}