package net.thucydides.core.webdriver;

/**
 * Turns a webdriver error into an ordinary assertion error.
 */
public class WebdriverAssertionError extends AssertionError {

    private static final long serialVersionUID = 1L;

    public WebdriverAssertionError(String message, Throwable cause) {
        super(message);
        this.setStackTrace(cause.getStackTrace());
    }
}
