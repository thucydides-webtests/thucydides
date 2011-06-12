package net.thucydides.core.model.features;

/**
 * A feature could not be instanciated for some reason.
 */
public class InvalidFeatureException extends RuntimeException {
    public InvalidFeatureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
