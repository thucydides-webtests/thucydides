package net.thucydides.core.reports;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.reports.xml.NotAThucydidesReportException;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

/**
 * Generates an aggregate acceptance test report. 
 * The class reads all the reports from the output directory and generates an aggregate report
 * summarizing the results using th generateReportsFor() method.
 */
public abstract class AggregateTestReporter {

    private File outputDirectory;

    private File sourceDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(PageObject.class);

    /**
     * We don't need any resources for XML reports.
     */
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

    /**
     * Generate an aggregate report for the reports in the output directory.
     */
    public abstract File generateReportFor(final AggregateTestResults aggregateTestResults) throws IOException;

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setSourceDirectory(final File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public AggregateTestResults loadAllReportsFrom(final File reportsDirectory) throws IOException {

        AggregateTestResults aggregateTestResults = new AggregateTestResults("All User Stories");

        XMLAcceptanceTestReporter acceptanceTestReporter = new XMLAcceptanceTestReporter();

        File[] reportFiles = getAllXMLFilesFrom(reportsDirectory);

        for (File reportFile : reportFiles) {
            try {
                AcceptanceTestRun testRun = acceptanceTestReporter.loadReportFrom(reportFile);
                aggregateTestResults.recordTestRun(testRun);
            } catch (NotAThucydidesReportException e) {
                LOGGER.info("Skipping XML file - not a Thucydides report: " + reportFile);
            }
        }

        return aggregateTestResults;
    }

    private File[] getAllXMLFilesFrom(final File reportsDirectory) {
        File[] reportFiles = reportsDirectory.listFiles(new FilenameFilter() {
            public boolean accept(final File file, final String filename) {
                return filename.toLowerCase().endsWith(".xml");
            }
        });
        return reportFiles;
    }

}
