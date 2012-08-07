package net.thucydides.maven.plugins;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * This plugin deletes existing history files for Thucydides for this project.
 * @goal clean
 */
public class ThucydidesCleanMojo extends AbstractMojo {
        /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    private HtmlAggregateStoryReporter reporter;

    private ThucydidesHTMLReportGenerator htmlReportGenerator;

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(MavenProjectHelper.getProjectIdentifier(project));
        }
        return reporter;

    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Clearing Thucydides historical reports");
        getReporter().clearHistory();
    }
}
