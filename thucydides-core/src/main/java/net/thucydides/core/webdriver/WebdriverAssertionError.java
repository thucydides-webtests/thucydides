package net.thucydides.core.webdriver;

/**
 * Turns a webdriver error into an ordinary assertion error.
 */
public class WebdriverAssertionError extends AssertionError {
    public WebdriverAssertionError(String message, Throwable error) {
        super(message);
        this.setStackTrace(error.getStackTrace());
    }
}
