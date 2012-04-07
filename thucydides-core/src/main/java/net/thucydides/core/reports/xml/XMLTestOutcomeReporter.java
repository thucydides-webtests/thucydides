package net.thucydides.core.reports.xml;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static net.thucydides.core.model.ReportType.XML;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class XMLTestOutcomeReporter implements AcceptanceTestReporter {

    private File outputDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLTestOutcomeReporter.class);

    private transient String qualifier;

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * We don't need any resources for XML reports.
     */
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

    public String getName() {
        return "xml";
    }

    /**
     * Generate an XML report for a given test run.
     */
    public File generateReportFor(final TestOutcome testOutcome) throws IOException {

        LOGGER.debug("Generating XML report for {}/{}", testOutcome.getUserStory(),
                                                        testOutcome.getMethodName());

        LOGGER.debug("Test outcome contents = {}", testOutcome);

        Preconditions.checkNotNull(outputDirectory);

        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", TestOutcome.class);
        xstream.registerConverter(usingXmlConverter());
        String xmlContents = xstream.toXML(testOutcome);

        String reportFilename = reportFor(testOutcome);
        LOGGER.debug("Calculated report filename: {}", reportFilename);

        File report = new File(getOutputDirectory(), reportFilename);

        LOGGER.debug("Writing XML report to {}", report.getAbsolutePath());
        FileUtils.writeStringToFile(report, xmlContents);

        return report;
    }

    private TestOutcomeConverter usingXmlConverter() {
        if (qualifier == null) {
            return new TestOutcomeConverter();
        } else {
            return new TestOutcomeConverter(qualifier);
        }
    }

    private String reportFor(final TestOutcome testOutcome) {
        if (qualifier == null) {
            return testOutcome.getReportName(XML);
        } else {
            return testOutcome.getReportName(XML, qualifier);
        }
    }

    public Optional<TestOutcome> loadReportFrom(final File reportFile) throws IOException {

        InputStream input = null;
        try {
            XStream xstream = new XStream();
            xstream.alias("acceptance-test-run", TestOutcome.class);
            xstream.registerConverter(usingXmlConverter());
            input = new FileInputStream(reportFile);
            return Optional.of((TestOutcome) xstream.fromXML(input));
        } catch (CannotResolveClassException e) {
            LOGGER.warn("Tried to load a file that is not a thucydides report: " + reportFile);
            return Optional.absent();
        } finally {
            input.close();
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

}
