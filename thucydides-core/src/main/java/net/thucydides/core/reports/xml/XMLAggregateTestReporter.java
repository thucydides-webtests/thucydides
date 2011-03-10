package net.thucydides.core.reports.xml;

import static net.thucydides.core.reports.ReportNamer.ReportType.XML;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.reports.AggregateTestReporter;
import net.thucydides.core.reports.ReportNamer;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;

/**
 * Generates an aggregate acceptance test report in XML form. Reads all the
 * reports from the output directory and generates an aggregate report
 * summarizing the results.
 */
public class XMLAggregateTestReporter extends AggregateTestReporter {

    private ReportNamer reportNamer = new ReportNamer(XML);

    /**
     * Generate an aggregate XML report for the reports in the output directory.
     */
    public File generateReportFor(final AggregateTestResults aggregateTestResults)
            throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        XStream xstream = new XStream();
        xstream.alias("user-story", AggregateTestResults.class);
        xstream.registerConverter(new AggregateAcceptanceTestRunConverter());
        String xmlContents = xstream.toXML(aggregateTestResults);

        String reportFilename = reportNamer.getNormalizedTestNameFor(aggregateTestResults);
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, xmlContents);

        return report;
    }

}
