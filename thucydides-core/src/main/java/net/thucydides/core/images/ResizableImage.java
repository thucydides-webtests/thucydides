package net.thucydides.core.images;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResizableImage {

    private final File screenshotFile;
    private final SimpleImageInfo imageInfo;
    private final int MAX_SUPPORTED_HEIGHT = 1200;

    public ResizableImage(final File screenshotFile) throws IOException  {
        this.screenshotFile = screenshotFile;
        this.imageInfo = new SimpleImageInfo(screenshotFile);
    }

    public static ResizableImage loadFrom(final File screenshotFile) throws IOException {
        return new ResizableImage(screenshotFile);
    }

    public int getWitdh() {
        return imageInfo.getWidth();
    }

    public int getHeight() {
        return imageInfo.getHeight();
    }

    public ResizableImage rescaleCanvas(final int width, final int height) throws IOException {

        if (getHeight() > height) {
            return this;
        }
        if (getHeight() > MAX_SUPPORTED_HEIGHT) {
            return this;
        }

        BufferedImage image = ImageIO.read(screenshotFile);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

        fillWithWhiteBackground(resizedImage);

        resizedImage.setData(image.getRaster());

        return new ResizedImage(resizedImage, screenshotFile);
    }

    private void fillWithWhiteBackground(final BufferedImage resizedImage) {
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(new Rectangle2D.Float(0, 0, resizedImage.getWidth(), resizedImage.getHeight()));
        g2d.dispose();
    }

    /**
     * If no resize operation has been done, just copy the file.
     * Otherwise we should be applying the saveTo() method on the ResizedImage class.
     */
    public void saveTo(final File savedFile) throws IOException {
        FileUtils.copyFile(screenshotFile, savedFile);
    }
}
