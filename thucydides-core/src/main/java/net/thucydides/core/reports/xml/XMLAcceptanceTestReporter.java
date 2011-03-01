package net.thucydides.core.reports.xml;

import static net.thucydides.core.reports.ReportNamer.ReportType.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportNamer;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class XMLAcceptanceTestReporter implements AcceptanceTestReporter {

    private File outputDirectory;

    private ReportNamer reportNamer = new ReportNamer(XML);

    /**
     * We don't need any resources for XML reports.
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

    public AcceptanceTestRun loadReportFrom(final File reportFile) throws NotAThucydidesReportException, IOException {
        try {
            XStream xstream = new XStream();
            xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
            xstream.registerConverter(new AcceptanceTestRunConverter());
            InputStream input = new FileInputStream(reportFile);
            return (AcceptanceTestRun) xstream.fromXML(input);
        } catch (CannotResolveClassException e) {
            throw new NotAThucydidesReportException("This file is not a thucydides report: "
                    + reportFile);
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

}
