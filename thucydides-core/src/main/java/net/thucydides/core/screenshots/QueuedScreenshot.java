package net.thucydides.core.screenshots;

import java.io.File;

public class QueuedScreenshot {

    private final File filename;
    private final File sourceFilename;

    public QueuedScreenshot(File sourceFilename, File targetFilename) {
        this.filename = targetFilename;
        this.sourceFilename = sourceFilename;
    }

    public File getDestinationFile() {
        return filename;
    }

    public File getSourceFile() {
        return sourceFilename;
    }
}
