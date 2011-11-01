package net.thucydides.core.reports.html.screenshots;

import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.model.Screenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Class designed to help resize and scale screenshots to a format that is compatible with the Thucydides reports.
 */
public class ScreenshotFormatter {

    private final Screenshot screenshot;
    private final File sourceDirectory;

    private ScreenshotFormatter(final Screenshot screenshot, final File sourceDirectory) {
        this.screenshot = screenshot;
        this.sourceDirectory = sourceDirectory;
    }

    public static ScreenshotFormatter forScreenshot(final Screenshot screenshot) {
        return new ScreenshotFormatter(screenshot, null);
    }

    public ScreenshotFormatter inDirectory(final File sourceDirectory) {
        return new ScreenshotFormatter(screenshot, sourceDirectory);
    }

    public Screenshot expandToHeight(final int targetHeight) throws IOException {
        File screenshotFile = new File(sourceDirectory, screenshot.getFilename());
        if (screenshotFile.exists()) {
            File resizedFile = resizedImage(screenshotFile, targetHeight);
            return new Screenshot(resizedFile.getName(),
                                  screenshot.getDescription(),
                                  screenshot.getWidth(),
                                  screenshot.getError());
        } else {
            return screenshot;
        }
    }

    private File resizedImage(File screenshotFile, int maxHeight) throws IOException {
        ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxHeight);
        File scaledFile = new File(sourceDirectory, "scaled_" + screenshotFile.getName());
        scaledImage.saveTo(scaledFile);
        return scaledFile;
    }
}

