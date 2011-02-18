package net.thucydides.core.pages;

/**
 * We have navigated to the wrong page.
 *
 */
public class WrongPageException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrongPageException(String message, Throwable e) {
        super(message, e);
    }

    public WrongPageException(String message) {
        super(message);
    }

}
