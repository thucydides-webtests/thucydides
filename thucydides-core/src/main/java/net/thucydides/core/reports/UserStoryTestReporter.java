package net.thucydides.core.reports;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.UserStoryTestResults;

/**
 * Generates an aggregate acceptance test report. 
 * The class reads all the reports from the output directory and generates an aggregate report
 * summarizing the results using th generateReportsFor() method.
 */
public abstract class UserStoryTestReporter {

    private File outputDirectory;

    private File sourceDirectory;

    /**
     * Where do report resources come from.
     * We don't need any resources for XML reports, so this does nothing by default.
     */
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

    /**
     * Generate an aggregate report for the test results of a single user story.
     */
    public abstract File generateReportFor(final UserStoryTestResults aggregateTestResults) throws IOException;

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

}
