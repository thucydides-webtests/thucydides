package net.thucydides.maven.plugins;

import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.core.util.EnvironmentVariables;
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
     * @parameter expression="${thucydides.outputDirectory}" default-value="${project.build.directory}/site/thucydides/"
     * @required
     */
    public File outputDirectory;

    /**
     * Thucydides test reports are read from here
     *
     * @parameter expression="${thucydides.sourceDirectory}" default-value="${project.build.directory}/site/thucydides/"
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
     * @parameter
     */
    public String jiraUsername;

    /**
     * @parameter
     */
    public String jiraPassword;

    /**
     * JIRA project key, which will be prepended to the JIRA issue numbers.
     * @parameter
     */
    public String jiraProject;

    /**
     * Base directory for requirements.
     * @parameter
     */
    public String requirementsBaseDir;


    /**
     * @parameter
     */
    public String statisticsDriver;

    /**
     * @parameter
     */
    public String statisticsUsername;

    /**
     * @parameter
     */
    public String statisticsPassword;

    /**
     * @parameter
     */
    public String statisticsDialect;

    /**
     * @parameter
     */
    public String statisticsUrl;

    EnvironmentVariables environmentVariables;

    /**
     * Thucydides project key
     * @parameter expression="${thucydides.project.key}" default-value="default"
     *
     */
    public String projectKey;

    protected void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    protected void setProject(final MavenProject project) {
        this.project = project;
    }

    protected void setSourceDirectory(final File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void prepareExecution() {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        configureEnvironmentVariables();
    }

    private EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables == null) {
            environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        }
        return environmentVariables;
    }

    private void configureEnvironmentVariables() {
        updateSystemProperty(ThucydidesSystemProperty.PROJECT_KEY.getPropertyName(), projectKey, Thucydides.getDefaultProjectKey());

        updateSystemProperty("thucydides.statistics.driver_class", statisticsDriver);
        updateSystemProperty("thucydides.statistics.url", statisticsUrl);
        updateSystemProperty("thucydides.statistics.username", statisticsUsername);
        updateSystemProperty("thucydides.statistics.password", statisticsPassword);
        updateSystemProperty("thucydides.statistics.dialect", statisticsDialect);

        updateSystemProperty("thucydides.test.requirements.basedir", requirementsBaseDir);
    }

    private void updateSystemProperty(String key, String value, String defaultValue) {
        if (value != null) {
            getEnvironmentVariables().setProperty(key, value);
        } else {
            getEnvironmentVariables().setProperty(key, defaultValue);
        }
    }

    private void updateSystemProperty(String key, String value) {
        if (value != null) {
            getEnvironmentVariables().setProperty(key, value);
        }
    }

    private HtmlAggregateStoryReporter reporter;

    protected void setReporter(final HtmlAggregateStoryReporter reporter) {
        this.reporter = reporter;
    }

    public void execute() throws MojoExecutionException {
        prepareExecution();

        try {
            generateHtmlStoryReports();
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating aggregate thucydides reports", e);
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
        getReporter().setJiraUsername(jiraUsername);
        getReporter().setJiraPassword(jiraPassword);
        getReporter().generateReportsForTestResultsFrom(sourceDirectory);
    }
}
