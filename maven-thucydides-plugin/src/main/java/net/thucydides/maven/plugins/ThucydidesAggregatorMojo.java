package net.thucydides.maven.plugins;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;

/**
 * Generate aggregate XML acceptance test reports.
 * 
 * @goal aggregate
 * @phase verify
 */
public class ThucydidesAggregatorMojo extends ThucydidesMojo {

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
