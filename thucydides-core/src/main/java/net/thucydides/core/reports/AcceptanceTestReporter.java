package net.thucydides.core.reports;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;

/**
 * Generates a report based on a set of acceptence test results.
 *
 * @author johnsmart
 *
 */
public interface AcceptanceTestReporter {

    /**
     * Generate reports for a given acceptance test run.
     */
    File generateReportFor(AcceptanceTestRun testRun) throws IOException;
    
    /**
     * Define the output directory in which the reports will be written.
     */
    void setOutputDirectory(File outputDirectory);
    
}
