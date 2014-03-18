package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.checksumCRC32;

/**
 * A screenshot image and the corresponding HTML source code.
 */
public class ScreenshotAndHtmlSource {

    private final File screenshot;
    private final File sourcecode;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotAndHtmlSource.class);

    public ScreenshotAndHtmlSource(File screenshot, File sourcecode) {
        this.screenshot = screenshot;
        this.sourcecode = sourcecode;
    }

    public ScreenshotAndHtmlSource(File screenshot) {
        this.screenshot = screenshot;
        this.sourcecode = null;
    }

    public File getScreenshotFile() {
        return screenshot;
    }

    public Optional<File> getSourcecode() {
        return Optional.fromNullable(sourcecode);
    }

    public boolean wasTaken() {
        return (screenshot != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScreenshotAndHtmlSource)) return false;

        ScreenshotAndHtmlSource that = (ScreenshotAndHtmlSource) o;

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

    public boolean hasIdenticalScreenshotsAs(ScreenshotAndHtmlSource anotherScreenshotAndHtmlSource) {
        if (hasNoScreenshot() || anotherScreenshotAndHtmlSource.hasNoScreenshot()) {
            return false;
        }
        return (getScreenshotFile().getName().equals(anotherScreenshotAndHtmlSource.getScreenshotFile().getName()));
    }

    public File getScreenshotFile(File screenshotTargetDirectory) {
        return new File(screenshotTargetDirectory, getScreenshotFile().getName());
    }
    public boolean hasNoScreenshot() {
        return getScreenshotFile() == null;
    }
}
