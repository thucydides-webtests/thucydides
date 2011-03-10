package net.thucydides.core.reports.html;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.reports.AggregateTestReporter;

import org.apache.commons.lang.NotImplementedException;

/**
 * Generates an aggregate acceptance test report in XML form. Reads all the
 * reports from the output directory and generates an aggregate report
 * summarizing the results.
 */
public class HtmlAggregateTestReporter extends AggregateTestReporter {

    /**
     * Generate an aggregate XML report for the reports in the output directory.
     */
    public File generateReportFor(final AggregateTestResults aggregateTestResults)
            throws IOException {
        throw new NotImplementedException();
    }

}
