package net.thucydides.maven.plugins;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * Generate aggregate XML acceptance test reports.
 * 
 * @goal aggregate
 * @phase verify
 */
public class ThucydidesAggregatorMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Aggregate reports are generated here
     * 
     * @parameter expression="${project.build.directory}/site/thucydides"
     * @required
     */
    private File outputDirectory;

    /**
     * Thucydides test reports are read from here
     * 
     * @parameter expression="${project.build.directory}/site/thucydides"
     * @required
     */
    private File sourceDirectory;

    /**
     * URL of the issue tracking system to be used to generate links for issue numbers.
     *
     * @parameter
     */
    private String issueTrackerUrl;

    private HtmlAggregateStoryReporter reporter;

    protected void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    protected void setProject(final MavenProject project) {
        this.project = project;
    }

    protected void setSourceDirectory(final File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    protected void setReporter(final HtmlAggregateStoryReporter reporter) {
        this.reporter = reporter;
    }

    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try {
            generateHtmlStoryReports();
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating aggregate thucydides reports", e);
        }
    }

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(MavenProjectHelper.getProjectIdentifier(project));
        }
        return reporter;

    }

    private void generateHtmlStoryReports() throws IOException {
        getReporter().setOutputDirectory(outputDirectory);
        getReporter().setIssueTrackerUrl(issueTrackerUrl);
        getReporter().generateReportsForStoriesFrom(sourceDirectory);
    }

}
