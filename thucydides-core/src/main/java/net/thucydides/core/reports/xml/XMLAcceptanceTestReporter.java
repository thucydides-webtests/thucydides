package net.thucydides.core.reports.xml;

import static net.thucydides.core.model.ReportNamer.ReportType.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class XMLAcceptanceTestReporter implements AcceptanceTestReporter {

    private File outputDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLAcceptanceTestReporter.class);

    private String qualifier;


    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

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
        xstream.registerConverter(usingXmlConverter());
        String xmlContents = xstream.toXML(testRun);

        String reportFilename = reportFor(testRun);
        File report = new File(getOutputDirectory(), reportFilename);
        LOGGER.debug("Writing XML report to " + report.getAbsolutePath());
        FileUtils.writeStringToFile(report, xmlContents);

        return report;
    }

    private AcceptanceTestRunConverter usingXmlConverter() {
        if (qualifier == null) {
            return new AcceptanceTestRunConverter();
        } else {
            return new AcceptanceTestRunConverter(qualifier);
        }
    }

    private String reportFor(AcceptanceTestRun testRun) {
        if (qualifier != null) {
            return testRun.getReportName(XML, qualifier);
        } else {
            return testRun.getReportName(XML);
        }
    }

    public AcceptanceTestRun loadReportFrom(final File reportFile) throws NotAThucydidesReportException, IOException {

        InputStream input = null;
        try {
            XStream xstream = new XStream();
            xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
            xstream.registerConverter(usingXmlConverter());
            input = new FileInputStream(reportFile);
            return (AcceptanceTestRun) xstream.fromXML(input);
        } catch (CannotResolveClassException e) {
            throw new NotAThucydidesReportException("This file is not a thucydides report: " + reportFile, e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

}
