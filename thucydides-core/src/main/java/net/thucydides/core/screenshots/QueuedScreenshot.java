package net.thucydides.core.screenshots;

import java.io.File;

public class QueuedScreenshot {

    private final byte[] screenshot;
    private final File filename;

    public QueuedScreenshot(byte[] screenshot, File filename) {
        this.screenshot = screenshot;
        this.filename = filename;
    }

    public byte[] getScreenshot() {
        return screenshot;
    }

    public File getFilename() {
        return filename;
    }
}
