package net.thucydides.maven.plugins;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.reports.xml.XMLAggregateTestReporter;

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
            XMLAggregateTestReporter reporter = new XMLAggregateTestReporter();
            reporter.setOutputDirectory(outputDirectory);

            AggregateTestResults aggregateTestResults 
                = reporter.loadAllReportsFrom(sourceDirectory);
            reporter.setSourceDirectory(sourceDirectory);
            reporter.generateReportFor(aggregateTestResults);
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating aggregate thucydides reports", e);
        }
    }
}
