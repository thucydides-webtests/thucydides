package net.thucydides.core.images;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResizableImage {

    private final BufferedImage image;

    public ResizableImage(final BufferedImage image) {
        this.image = image;
    }

    public static ResizableImage loadFrom(final File screenshotFile) throws IOException {
        return new ResizableImage(ImageIO.read(screenshotFile));
    }

    public int getWitdh() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public ResizableImage rescaleCanvas(final int width, final int height) {

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

        fillWithWhiteBackground(resizedImage);

        resizedImage.setData(image.getRaster());

        return new ResizableImage(resizedImage);
    }

    private void fillWithWhiteBackground(final BufferedImage resizedImage) {
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(new Rectangle2D.Float(0, 0, resizedImage.getWidth(), resizedImage.getHeight()));
        g2d.dispose();
    }

    public void saveTo(File file) throws IOException {
        ImageIO.write(image, "PNG", file);
    }
}
