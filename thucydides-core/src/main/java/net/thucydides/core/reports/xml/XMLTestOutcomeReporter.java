package net.thucydides.core.reports.xml;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

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

        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);

        LOGGER.debug("Generating XML report for {}/{}", storedTestOutcome.getUserStory(),
                                                        storedTestOutcome.getMethodName());

        LOGGER.debug("Test outcome contents = {}", storedTestOutcome);

        Preconditions.checkNotNull(outputDirectory);

        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", TestOutcome.class);
        xstream.registerConverter(usingXmlConverter());
        String xmlContents = xstream.toXML(storedTestOutcome);

        String reportFilename = reportFor(storedTestOutcome);
        LOGGER.debug("Calculated report filename: {}", reportFilename);

        File report = new File(getOutputDirectory(), reportFilename);

        LOGGER.debug("Writing XML report to {}", report.getAbsolutePath());
        FileUtils.writeStringToFile(report, xmlContents);

        return report;
    }

    private TestOutcomeConverter usingXmlConverter() {
        return new TestOutcomeConverter();
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(XML);
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

    public List<TestOutcome> loadReportsFrom(File outputDirectory) throws IOException {
        File[] reportFiles = getAllXMLFilesFrom(outputDirectory);
        List<TestOutcome> testOutcomes = Lists.newArrayList();
        if (reportFiles != null) {
            for (File reportFile : reportFiles) {
                testOutcomes.addAll(loadReportFrom(reportFile).asSet());
            }
        }
        return testOutcomes;
    }

    private File[] getAllXMLFilesFrom(final File reportsDirectory) {
        return reportsDirectory.listFiles(new XmlFilenameFilter());
    }

    private static final class XmlFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return filename.toLowerCase(Locale.getDefault()).endsWith(".xml");
        }
    }

}
