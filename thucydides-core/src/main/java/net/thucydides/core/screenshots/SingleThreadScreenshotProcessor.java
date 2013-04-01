package net.thucydides.core.screenshots;

import com.google.common.io.Files;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SingleThreadScreenshotProcessor implements ScreenshotProcessor {

    Thread screenshotThread;
    final Queue<QueuedScreenshot> queue;

    private final EnvironmentVariables environmentVariables;

    private final Logger logger = LoggerFactory.getLogger(SingleThreadScreenshotProcessor.class);

    @Inject
    public SingleThreadScreenshotProcessor(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.queue = new ConcurrentLinkedQueue<QueuedScreenshot>();
        start();
    }

    public void start() {
        screenshotThread = new Thread(new Processor(queue));
        screenshotThread.start();
    }


    public void waitUntilDone() {
        while (!isEmpty()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignore) {
            }
        }
        //wait for just a bit longer so Windows can catch up
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignore) {
        }
    }

    class Processor implements Runnable {

        private final Queue<QueuedScreenshot> queue;

        Processor(Queue<QueuedScreenshot> queue) {
            this.queue = queue;
        }

        boolean done = false;

        public void run() {
            while (!done) {
                synchronized (queue) {
                    saveQueuedScreenshot();
                    try {
                        queue.wait();
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        }

        private void saveQueuedScreenshot() {
            while (!queue.isEmpty()) {
                QueuedScreenshot queuedScreenshot = queue.poll();
                if (queuedScreenshot != null) {
                    processScreenshot(queuedScreenshot);
                }
            }
        }

        private void processScreenshot(QueuedScreenshot queuedScreenshot) {
            if (shouldResize(queuedScreenshot)) {
                resizeScreenshot(queuedScreenshot);
            } else {
                moveScreenshot(queuedScreenshot);
            }
        }

        private int getResizedWidth() {
            return environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.RESIZED_WIDTH, 0);
        }

        private boolean shouldResize(QueuedScreenshot queuedScreenshot) {
            if (getResizedWidth() > 0) {
                BufferedImage image = readImage(queuedScreenshot);
                if (image != null) {
                    int width = image.getData().getWidth();
                    return (width != getResizedWidth());
                }
            }
            return false;
        }

        private BufferedImage readImage(QueuedScreenshot queuedScreenshot) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(queuedScreenshot.getSourceFile());
            } catch (IOException e) {
                logger.warn("Failed to read the stored screenshot (possibly an out of memory error): " + e.getMessage());
            }
            return image;
        }

        private void moveScreenshot(QueuedScreenshot queuedScreenshot) {
            try {
                Files.move(queuedScreenshot.getSourceFile(),
                           queuedScreenshot.getDestinationFile());
            } catch (Throwable e) {
                logger.warn("Failed to move the screenshot to the destination directory: " + e.getMessage());
            }
        }

        private void resizeScreenshot(QueuedScreenshot queuedScreenshot) {
            try {
                BufferedImage image = ImageIO.read(queuedScreenshot.getSourceFile());
                int width = image.getData().getWidth();
                int height = image.getData().getHeight();
                int targetWidth = getResizedWidth();
                int targetHeight = (int) (((double) targetWidth / (double) width) * (double) height);

                BufferedImage resizedImage = resize(image, targetWidth, targetHeight);
                ImageIO.write(resizedImage, "png", queuedScreenshot.getDestinationFile());
                queuedScreenshot.getSourceFile().delete();
            } catch (Throwable e) {
                logger.warn("Failed to resize screenshot: using original size " + e.getMessage());
                moveScreenshot(queuedScreenshot);
            }
        }

        private BufferedImage resize(BufferedImage image, int width, int height) {
            int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.setComposite(AlphaComposite.Src);

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            return resizedImage;
        }
    }

    public void queueScreenshot(QueuedScreenshot queuedScreenshot) {
        queue.offer(queuedScreenshot);
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }


}