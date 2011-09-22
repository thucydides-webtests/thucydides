package net.thucydides.core.reports.html.screenshots;

import com.google.inject.internal.ImmutableList;
import mx4j.tools.naming.NamingServiceMBean;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.model.Screenshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class designed to help resize and scale screenshots to a format that is compatible with the Thucydides reports.
 */
public class ScreenshotFormatter {

    private final List<Screenshot> screenshots;
    private final File outputDirectory;

    private ScreenshotFormatter(final List<Screenshot> screenshots, final File outputDirectory) {
        this.screenshots = ImmutableList.copyOf(screenshots);
        this.outputDirectory = outputDirectory;
    }

    public static ScreenshotFormatter forEachScreenshotIn(final List<Screenshot> screenshots) {
        return new ScreenshotFormatter(screenshots, null);
    }

    public ScreenshotFormatter inDirectory(final File outputDirectory) {
        return new ScreenshotFormatter(screenshots, outputDirectory);

    }
    /**
     * Scale screenshots to a specified width
     */
    public ScreenshotFormatter scaleToWidth(final int maxWidth) throws IOException {
        List<Screenshot> scaledScreenshots = new ArrayList<Screenshot>();

        ResizableImage scaledImage;
        File screenshotFile;
        File scaledFile;
        int maxHeight = getMaximumHeightForWidth(maxWidth);

        for(Screenshot screenshot : screenshots) {
            screenshotFile = new File(outputDirectory, screenshot.getFilename());
            scaledImage = ResizableImage.loadFrom(screenshotFile).scaleToWidth(maxWidth, maxHeight);
            scaledFile = new File(outputDirectory, "sc_" + screenshot.getFilename());
            scaledImage.saveTo(scaledFile);
            scaledScreenshots.add(new Screenshot(scaledFile.getName(),screenshot.getDescription()));

            System.gc(); // Tell the JVM that freeing up the image buffers now would be a good idea.
        }

        return new ScreenshotFormatter(scaledScreenshots, outputDirectory);
    }


    public ScreenshotFormatter equalizeHeight() throws IOException {
        List<Screenshot> scaledScreenshots = new ArrayList<Screenshot>();

        int maxHeight = getMaximumHeight();
        int maxWidth = getMaximumWidth();
        for(Screenshot screenshot : screenshots) {
            File screenshotFile = new File(outputDirectory, screenshot.getFilename());
            if (screenshotFile.exists()) {
                File resizedFile = resizedImage(screenshotFile, maxWidth, maxHeight);
                scaledScreenshots.add(new Screenshot(resizedFile.getName(), screenshot.getDescription()));
            }
        }

        return new ScreenshotFormatter(scaledScreenshots, outputDirectory);
    }

    private File resizedImage(File screenshotFile, int maxWidth, int maxHeight) throws IOException {
        ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxWidth, maxHeight);
        File scaledFile = new File(outputDirectory, "eq_" + screenshotFile.getName());
        scaledImage.saveTo(scaledFile);
        return scaledFile;
    }

    /**
     * Return the processed screenshots as a list of screenshots.
     */
    public List<Screenshot> list() {
        return ImmutableList.copyOf(screenshots);
    }

    private int getMaximumWidth() throws IOException {
        int maxWidth = 0;
        for (Screenshot screenshot : screenshots) {
            File screenshotFile = new File(outputDirectory, screenshot.getFilename());
            if (screenshotFile.exists()) {
                int width = new ResizableImage(screenshotFile).getWitdh();
                maxWidth = (width > maxWidth) ? width : maxWidth;
            }
        }
        return maxWidth;
    }

    private int getMaximumHeight() throws IOException {
        int maxHeight = 0;
        for (Screenshot screenshot : screenshots) {
            File screenshotFile = new File(outputDirectory, screenshot.getFilename());
            if (screenshotFile.exists()) {
                int height = new ResizableImage(screenshotFile).getHeight();
                maxHeight = (height > maxHeight) ? height : maxHeight;
            }
        }
        return maxHeight;
    }

    private int getMaximumHeightForWidth(final int width) throws IOException {
        int maxHeight = 0;
        for (Screenshot screenshot : screenshots) {
            File screenshotFile = new File(outputDirectory, screenshot.getFilename());
            if (screenshotFile.exists()) {
                int rawHeight = new ResizableImage(screenshotFile).getHeight();
                int rawWidth = new ResizableImage(screenshotFile).getWitdh();
                int proportionalHeight = (int) ((width * 1.0 / rawWidth) * rawHeight);
                maxHeight = (proportionalHeight > maxHeight) ? proportionalHeight : maxHeight;
            }
        }
        return maxHeight;
    }

/*
    List<Screenshot> expandScreenshotList = new ArrayList<Screenshot>();

    int maxWidth = maxScreenshotWidthIn(screenshots);
    int maxHeight = maxScreenshotHeightIn(screenshots);

    for(Screenshot screenshot : screenshots) {
        File screenshotFile = new File(getOutputDirectory(), screenshot.getFilename());
        if (screenshotFile.exists()) {
            ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxWidth, maxHeight);
            File scaledFile = new File(getOutputDirectory(), "scaled_" + screenshot.getFilename());
            scaledImage.saveTo(scaledFile);
            expandScreenshotList.add(new Screenshot(scaledFile.getName(),screenshot.getDescription()));
        } else {
            expandScreenshotList.add(screenshot);
        }
    }
    return expandScreenshotList;


private int maxScreenshotWidthIn(List<Screenshot> screenshots) throws IOException {
    int maxWidth = 0;
    for (Screenshot screenshot : screenshots) {
        File screenshotFile = new File(getOutputDirectory(),screenshot.getFilename());
        if (screenshotFile.exists()) {
            maxWidth = maxWidthOf(maxWidth, screenshotFile);
        }
    }
    return maxWidth;
}

private int maxWidthOf(int maxWidth, File screenshotFile) throws IOException {
    int width = ResizableImage.loadFrom(screenshotFile).getWitdh();
    if (width > MAXIMUM_SCREENSHOT_WIDTH) {
        width = MAXIMUM_SCREENSHOT_WIDTH;
    }
    if (width > maxWidth) {
        maxWidth = width;
    }
    return maxWidth;
}

private int maxScreenshotHeightIn(List<Screenshot> screenshots) throws IOException {
    int maxHeight = 0;
    for (Screenshot screenshot : screenshots) {
        File screenshotFile = new File(getOutputDirectory(),screenshot.getFilename());
        if (screenshotFile.exists()) {
            maxHeight = maxHeightOf(maxHeight, screenshotFile);
        }
    }
    return maxHeight;
}

private int maxHeightOf(int maxHeight, File screenshotFile) throws IOException {
    int height = ResizableImage.loadFrom(screenshotFile).getHeight();
    int width = ResizableImage.loadFrom(screenshotFile).getWitdh();
    if (width > MAXIMUM_SCREENSHOT_WIDTH) {
        height = (int) ((height * 1.0) * (MAXIMUM_SCREENSHOT_WIDTH * 1.0 / width));
    }
    if (height > maxHeight) {
        maxHeight = height;
    }
    return maxHeight;
}

private String withoutType(final String screenshot) {
    int dot = screenshot.lastIndexOf('.');
    if (dot > 0) {
        return screenshot.substring(0, dot);
    } else {
        return screenshot;
    }
}
*/
}

