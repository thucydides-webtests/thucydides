package net.thucydides.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.model.loaders.UserStoryLoader;
import net.thucydides.core.reports.html.HtmlUserStoryTestReporter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generate aggregate XML acceptance test reports.
 * 
 * @goal aggregate
 * @requiresReports true
 * @phase verify
 */
public class ThucydidesReporterMojo extends AbstractMojo {
    /**
     * Aggregate reports are generated here
     * 
     * @parameter expression="${project.build.directory}/thucydides"
     * @required
     */
    private File outputDirectory;

    /**
     * Thucydides test reports are read from here
     * 
     * @parameter expression="${project.build.directory}/thucydides"
     * @required
     */
    private File sourceDirectory;

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

    private void generateHtmlStoryReports() throws IOException {
        HtmlUserStoryTestReporter reporter = new HtmlUserStoryTestReporter();
        reporter.setOutputDirectory(outputDirectory);
        
        UserStoryLoader loader = new UserStoryLoader();
        List<UserStoryTestResults> userStoryResults = loader.loadStoriesFrom(sourceDirectory);
        
        for(UserStoryTestResults userStoryTestResults : userStoryResults) {
            reporter.generateReportFor(userStoryTestResults);
        }
    }
    
}
