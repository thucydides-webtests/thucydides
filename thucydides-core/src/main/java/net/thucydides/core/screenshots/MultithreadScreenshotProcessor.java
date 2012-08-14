package net.thucydides.core.screenshots;

import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultithreadScreenshotProcessor implements ScreenshotProcessor {

    List<Thread> screenshotThreads;
    final Queue<QueuedScreenshot> queue;
    final static int THREAD_COUNT = 5;

    private final EnvironmentVariables environmentVariables;

    private final Logger logger = LoggerFactory.getLogger(MultithreadScreenshotProcessor.class);

    @Inject
    public MultithreadScreenshotProcessor(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.queue = new ConcurrentLinkedQueue<QueuedScreenshot>();
        this.screenshotThreads = Lists.newArrayList();
        start();
    }

    public void start() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread screenshotThread = new Thread(new Processor(queue));
            screenshotThreads.add(screenshotThread);
            screenshotThread.start();
        }
    }


    @Override
    public void waitUntilDone() {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    class Processor implements Runnable {

        private final Queue<QueuedScreenshot> queue;

        Processor(Queue<QueuedScreenshot> queue) {
            this.queue = queue;
        }

        boolean done = false;

        @Override
        public void run() {
            while (!done) {
                saveQueuedScreenshot();
                synchronized (queue) {
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
                logger.warn("Failed to write screenshot (possibly an out of memory error): " + e.getMessage());
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
                logger.warn("Failed to write screenshot (possibly an out of memory error): " + e.getMessage());
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

    @Override
    public void queueScreenshot(QueuedScreenshot queuedScreenshot) {
        queue.offer(queuedScreenshot);
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }


}