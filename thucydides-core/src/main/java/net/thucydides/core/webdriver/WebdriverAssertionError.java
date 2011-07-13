package net.thucydides.core.webdriver;

/**
 * Turns a webdriver error into an ordinary assertion error.
 */
public class WebdriverAssertionError extends AssertionError {

    private static final long serialVersionUID = 1L;

    public WebdriverAssertionError(String message, Throwable error) {
        super(message);
        this.setStackTrace(error.getStackTrace());
    }
}
