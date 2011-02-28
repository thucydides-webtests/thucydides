package net.thucydides.core.reports.xml;

import static net.thucydides.core.reports.ReportNamer.ReportType.XML;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.reports.ReportNamer;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;

/**
 * Generates an aggregate acceptance test report in XML form.
 * Reads all the reports from the output directory and generates an aggregate
 * report summarizing the results.
 */
public class XMLAggregateTestReporter {

    private File outputDirectory;

    private File sourceDirectory;

    private ReportNamer reportNamer = new ReportNamer(XML);

    /**
     * We don't need any resources for XML reports.
     */
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

    /**
     * Generate an aggregate XML report for the reports in the output directory.
     */
    public File generateReportFor(final AggregateTestResults aggregateTestResults) throws IOException {

        Preconditions.checkNotNull(outputDirectory);

        XStream xstream = new XStream();
        xstream.alias("user-story", AggregateTestResults.class);
        xstream.registerConverter(new AggregateAcceptanceTestRunConverter());
        String xmlContents = xstream.toXML(aggregateTestResults);

        String reportFilename = reportNamer.getNormalizedTestNameFor(aggregateTestResults);
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, xmlContents);

        return report;
    }

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
        
        for(File reportFile : reportFiles) {
            AcceptanceTestRun testRun = acceptanceTestReporter.loadReportFrom(reportFile);
            aggregateTestResults.recordTestRun(testRun);
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
