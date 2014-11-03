package net.thucydides.maven.plugins;

import net.thucydides.core.reports.ResultChecker;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * This plugin deletes existing history files for Thucydides for this project.
 * @goal check
 */
public class ThucydidesCheckMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Aggregate reports are generated here
     * @parameter expression="${thucydides.outputDirectory}" default-value="${project.build.directory}/site/thucydides/"
     * @required
     */
    public File outputDirectory;

    protected ResultChecker getResultChecker() {
        return new ResultChecker(outputDirectory);
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Checking Thucydides test results");
        getResultChecker().checkTestResults();
    }
}
