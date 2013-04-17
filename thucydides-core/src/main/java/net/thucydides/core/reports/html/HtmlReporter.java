package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.templates.ReportTemplate;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * An HTML report generates reports in a given directory and uses resources (images,...) from another.
 *
 * @author johnsmart
 */
public abstract class HtmlReporter {

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";
    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    private File outputDirectory;
    private final TemplateManager templateManager;
    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReporter.class);

    public HtmlReporter() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public HtmlReporter(final EnvironmentVariables environmentVariables) {
        super();
        this.templateManager = Injectors.getInjector().getInstance(TemplateManager.class);
        this.environmentVariables = environmentVariables;
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

    protected EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    private boolean alreadyCopied = false;

    protected void copyResourcesToOutputDirectory() throws IOException {
        if (!alreadyCopied) {
            updateResourceDirectoryFromSystemPropertyIfDefined();
            HtmlResourceCopier copier = new HtmlResourceCopier(getResourceDirectory());

            copier.copyHTMLResourcesTo(getOutputDirectory());
            alreadyCopied = true;
        }
    }

    private void updateResourceDirectoryFromSystemPropertyIfDefined() {

        String systemDefinedResourceDirectory
             = getEnvironmentVariables().getProperty(ThucydidesSystemProperty.REPORT_RESOURCE_PATH.getPropertyName());
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
//        FileUtils.writeStringToFile(report, htmlContents);

        BufferedWriter bw = null;
        OutputStreamWriter osw = null;

        FileOutputStream fos = new FileOutputStream(report, false);
        try
        {
            if (report.length() < 1L) {
                byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
                fos.write(bom);
            }

            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            if (htmlContents != null) {
                byte[] utf8Bytes = htmlContents.getBytes();
                String encodedString = new String(utf8Bytes, "UTF-8");
                bw.write(encodedString);
            }

            try
            {
                bw.close();
                fos.close();
            }
            catch (Exception ex) {}

            LOGGER.debug("Writing HTML report to {}", report.getAbsolutePath()); } catch (IOException ex) { throw ex;
        } finally {
            try {
                bw.close();
                fos.close();
            }
            catch (Exception ex) {}
        }
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
                throw new RuntimeException("Failed to merge template: " + e.getMessage(), e);
            }
        }
    }

}