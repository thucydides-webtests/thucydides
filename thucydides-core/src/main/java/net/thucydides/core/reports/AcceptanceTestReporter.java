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

    File generateReportFor(AcceptanceTestRun testRun) throws IOException;
    void setOutputDirectory(File outputDirectory);
    
}
