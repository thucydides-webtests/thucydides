package net.thucydides.core.resources;

import java.io.IOException;

/**
 * An error occurred when copying resources required for the HTML reports.
 */
public class ResourceCopyingError extends RuntimeException {
    public ResourceCopyingError(String message, Throwable e) {
        super(message, e);
    }
}
