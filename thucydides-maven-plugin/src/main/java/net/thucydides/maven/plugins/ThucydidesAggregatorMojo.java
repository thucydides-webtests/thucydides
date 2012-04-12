package net.thucydides.maven.plugins;

import net.thucydides.core.ThucydidesSystemProperty;
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
    public MavenProject project;

    /**
     * Aggregate reports are generated here
     * 
     * @parameter expression="${project.build.directory}/site/thucydides"
     * @required
     */
    public File outputDirectory;

    /**
     * Thucydides test reports are read from here
     * 
     * @parameter expression="${project.build.directory}/site/thucydides"
     * @required
     */
    public File sourceDirectory;

    /**
     * URL of the issue tracking system to be used to generate links for issue numbers.
     * @parameter
     */
    public String issueTrackerUrl;

    /**
     * Base URL for JIRA, if you are using JIRA as your issue tracking system.
     * If you specify this property, you don't need to specify the issueTrackerUrl.
     * @parameter
     */
    public String jiraUrl;

    /**
     * JIRA project key, which will be prepended to the JIRA issue numbers.
     * @parameter
     */
    public String jiraProject;

    /**
     * Thucydides project key
     * @parameter expression="${thucydides.project.key}" default-value="default"
     *
     */
    public String projectKey;

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
            configureEnvironmentVariables();
            generateHtmlStoryReports();
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating aggregate thucydides reports", e);
        }
    }

    private void configureEnvironmentVariables() {
        if (projectKey != null) {
            System.setProperty(ThucydidesSystemProperty.PROJECT_KEY.getPropertyName(), projectKey);
        }
    }

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(projectKey);
        }
        return reporter;

    }

    private void generateHtmlStoryReports() throws IOException {
        getReporter().setOutputDirectory(outputDirectory);
        getReporter().setIssueTrackerUrl(issueTrackerUrl);
        getReporter().setJiraUrl(jiraUrl);
        getReporter().setJiraProject(jiraProject);
        getReporter().generateReportsForTestResultsFrom(sourceDirectory);
    }

}
