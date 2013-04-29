package net.thucydides.core.reports;

import java.io.File;

/**
 * A base directory for Thucydides report generators.
 */
public class ThucydidesReporter {
    private File outputDirectory;

    /**
     * Reports will be generated here.
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
