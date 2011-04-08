package net.thucydides.core.resources;

/**
 * An error occurred when copying resources required for the HTML reports.
 */
public class ResourceCopyingError extends RuntimeException {
    public ResourceCopyingError(final String message, final Throwable e) {
        super(message, e);
    }
}
