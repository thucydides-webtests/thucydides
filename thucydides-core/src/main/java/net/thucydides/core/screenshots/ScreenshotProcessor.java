package net.thucydides.core.screenshots;

public interface ScreenshotProcessor {
    void waitUntilDone();

    void queueScreenshot(QueuedScreenshot queuedScreenshot);

    boolean isEmpty();
}
