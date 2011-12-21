package net.thucydides.core.screenshots;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordedScreenshot)) return false;

        RecordedScreenshot that = (RecordedScreenshot) o;

        if (screenshot == null) {
            return (that.screenshot == null);
        } else if (that.screenshot == null) {
            return (this.screenshot == null);
        } else {
            try {
                return FileUtils.contentEquals(screenshot, that.screenshot);
            } catch (IOException e) {
                return false;
            }
        }
    }


    @Override
    public int hashCode() {
        return screenshot != null ? screenshot.hashCode() : 0;
    }
}
