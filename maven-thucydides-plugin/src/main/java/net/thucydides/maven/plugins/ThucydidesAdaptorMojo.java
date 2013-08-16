package net.thucydides.maven.plugins;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.TestOutcomeAdaptorReporter;
import net.thucydides.core.reports.adaptors.AdaptorService;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;

/**
 * This plugin generates converts external (e.g. xUnit) files into Thucydides reports.
 * @goal import
 * @requiresProject false
 * @description Import other test output formats into Thucydides
 */
public class ThucydidesAdaptorMojo extends AbstractMojo {

    /**
     * Aggregate reports are generated here
     * @parameter expression="${import.target}" default-value="target/site/thucydides/"
     * @required
     */
    public File outputDirectory;

    /**
     * External test reports are read from here
     *
     * @parameter expression="${import.format}"
     * @required
     */
    public String format;

    /**
     * External test reports are read from here if necessary.
     * This could be either a directory or a single file, depending on the adaptor used.
     * For some adaptors (e.g. database connectors), it will not be necessary.
     *
     * @parameter expression="${import.source}"
     */
    public File source;

    private final EnvironmentVariables environmentVariables;
    private final AdaptorService adaptorService;
    private final TestOutcomeAdaptorReporter reporter = new TestOutcomeAdaptorReporter();

    public ThucydidesAdaptorMojo(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.adaptorService = new AdaptorService(environmentVariables);
    }

    public ThucydidesAdaptorMojo() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    protected File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSource(File source) {
        this.source = source;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Importing external test reports");
        getLog().info("Source directory: " + source);
        getLog().info("Output directory: " + getOutputDirectory());

        try {
            getLog().info("Adaptor: " + adaptorService.getAdaptor(format));
            reporter.registerAdaptor(adaptorService.getAdaptor(format));
            reporter.setOutputDirectory(outputDirectory);
            reporter.generateReportsFrom(source);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
