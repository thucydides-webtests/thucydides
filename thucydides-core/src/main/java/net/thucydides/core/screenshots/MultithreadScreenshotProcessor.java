package net.thucydides.core.screenshots;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultithreadScreenshotProcessor implements ScreenshotProcessor {

    List<Thread> screenshotThreads;
    final Queue<QueuedScreenshot> queue;
    final static int THREAD_COUNT = 5;

    private final Logger logger = LoggerFactory.getLogger(MultithreadScreenshotProcessor.class);

    public MultithreadScreenshotProcessor() {
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
                    saveScreenshot(queuedScreenshot);
                }
            }
        }

        private void saveScreenshot(QueuedScreenshot queuedScreenshot) {
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(queuedScreenshot.getFilename());
                stream.write(queuedScreenshot.getScreenshot());
            } catch (Throwable e) {
                logger.warn("Failed to write screenshot (possibly an out of memory error): " + e.getMessage());
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignore) {
                    }
                }
            }
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