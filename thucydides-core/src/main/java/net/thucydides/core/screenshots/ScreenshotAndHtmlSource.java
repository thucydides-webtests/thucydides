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

    private final File screenshotFile;
    private final File sourcecode;

    public ScreenshotAndHtmlSource(File screenshotFile, File sourcecode) {
        this.screenshotFile = screenshotFile;
        this.sourcecode = sourcecode;
    }

    public ScreenshotAndHtmlSource(File screenshotFile) {
        this(screenshotFile, null);
    }

    public File getScreenshotFile() {
        return screenshotFile;
    }

    public Optional<File> getSourcecode() {
        return Optional.fromNullable(sourcecode);
    }

    public boolean wasTaken() {
        return (screenshotFile != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScreenshotAndHtmlSource)) return false;

        ScreenshotAndHtmlSource that = (ScreenshotAndHtmlSource) o;

        if (screenshotFile == null) {
            return (that.screenshotFile == null);
        } else if (that.screenshotFile == null) {
            return (this.screenshotFile == null);
        } else {
            try {
                return FileUtils.contentEquals(screenshotFile, that.screenshotFile);
            } catch (IOException e) {
                return false;
            }
        }
    }


    @Override
    public int hashCode() {
        return screenshotFile != null ? screenshotFile.hashCode() : 0;
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
