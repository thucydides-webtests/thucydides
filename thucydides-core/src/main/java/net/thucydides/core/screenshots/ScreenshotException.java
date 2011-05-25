package net.thucydides.core.screenshots;

/**
 * The screenshot could not be taken for some reason.
 */
public class ScreenshotException extends RuntimeException {
    public ScreenshotException(final String message, final Throwable e) {
        super(message,e);
    }
}
