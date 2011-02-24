package net.thucydides.core.reports.xml;

import static net.thucydides.core.reports.ReportNamer.ReportType.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportNamer;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class XMLAcceptanceTestReporter implements AcceptanceTestReporter {

    private File outputDirectory;

    private ReportNamer reportNamer = new ReportNamer(XML);

    /**
     * We don't need any resourcs for XML reports.
     */
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

    /**
     * Generate an XML report for a given test run.
     */
    public File generateReportFor(final AcceptanceTestRun testRun) throws IOException {

        Preconditions.checkNotNull(outputDirectory);

        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
        xstream.registerConverter(new AcceptanceTestRunConverter());
        String xmlContents = xstream.toXML(testRun);

        String reportFilename = reportNamer.getNormalizedTestNameFor(testRun);
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, xmlContents);

        return report;
    }

    public AcceptanceTestRun loadReportFrom(final File reportFile) throws IOException {
        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
        xstream.registerConverter(new AcceptanceTestRunConverter());
        InputStream input = new FileInputStream(reportFile);
        return (AcceptanceTestRun) xstream.fromXML(input); 
    }
    
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<AcceptanceTestRun> loadAllReportsFrom(final File reportsDirectory) throws IOException {
        
        List<AcceptanceTestRun> acceptanceTests = new ArrayList<AcceptanceTestRun>();
        
        File[] reportFiles = getAllXMLFilesFrom(reportsDirectory);
        
        for(File reportFile : reportFiles) {
            AcceptanceTestRun testRun = loadReportFrom(reportFile);
            acceptanceTests.add(testRun);
        }
        
        return acceptanceTests;
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
