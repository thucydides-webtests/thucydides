package net.thucydides.core.pages;

/**
 * We have navigated to the wrong page.
 *
 */
public class WrongPageException extends AssertionError {

    private static final long serialVersionUID = 1L;

    public WrongPageException(final String message, final Throwable e) {
        super(message);
    }

    public WrongPageException(final String message) {
        super(message);
    }

}
