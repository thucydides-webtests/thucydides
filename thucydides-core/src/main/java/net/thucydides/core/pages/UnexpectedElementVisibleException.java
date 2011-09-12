package net.thucydides.core.pages;

/**
 * Thrown if an element that should not be visible is visible.
 */
public class UnexpectedElementVisibleException extends RuntimeException {
    public UnexpectedElementVisibleException(String message) {
        super(message);
    }
}
