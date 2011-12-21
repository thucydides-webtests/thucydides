package net.thucydides.core.screenshots;

import java.io.File;

public class RecordedScreenshot {
    
    private final File screenshot;
    private final File sourcecode;

    public RecordedScreenshot(File screenshot, File sourcecode) {
        this.screenshot = screenshot;
        this.sourcecode = sourcecode;
    }

    public File getScreenshot() {
        return screenshot;
    }

    public File getSourcecode() {
        return sourcecode;
    }

    public boolean wasTaken() {
        return (screenshot != null);
    }
}
