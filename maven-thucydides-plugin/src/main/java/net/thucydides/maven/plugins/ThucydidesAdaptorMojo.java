package net.thucydides.maven.plugins;

import net.thucydides.core.reports.TestOutcomeAdaptorReporter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.adaptors.AdaptorService;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * This plugin generates converts external (e.g. xUnit) files into Thucydides reports.
 * @goal import
 * @requiresProject false
 */
public class ThucydidesAdaptorMojo extends AbstractMojo {

    /**
     * Aggregate reports are generated here
     * @parameter expression="${import.target}" default-value="target/site/thucydides/"
     * @required
     */
    public String outputDirectory;

    /**
     * External test reports are read from here
     *
     * @parameter expression="${import.format}"
     * @required
     */
    public String format;

    /**
     * External test reports are read from here
     *
     * @parameter expression="${import.source}"
     * @required
     */
    public File sourceDirectory;

    private TestOutcomeAdaptorReporter reporter = new TestOutcomeAdaptorReporter();
    private AdaptorService adaptorService = new AdaptorService();

    protected String getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputName() {
        return "thucydides";
    }

    public String getName(Locale locale) {
        return "Thucydides Import";
    }

    public String getDescription(Locale locale) {
        return "Import other test output formats into Thucydides";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Importing external test reports");
        getLog().info("Source directory: " + sourceDirectory);
        getLog().info("Output directory: " + getOutputDirectory());

        try {
            getLog().info("Adaptor: " + adaptorService.getAdaptor(format));
            reporter.registerAdaptor(adaptorService.getAdaptor(format));
            reporter.setOutputDirectory(new File(getOutputDirectory()));
            reporter.generateReportsFrom(sourceDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
