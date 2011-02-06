package net.thucydides.core.webdriver;

/**
 * Thrown when the test runner tries to use an unsupported WebDriver driver.
 * Typically HtmlUnit, or a driver for which screen copies can't be taken.
 * This is checked when the test runner is first set up, so if it
 * occurs during a test run, something very unexpected has happened.
 * 
 * @author johnsmart
 *
 */
public class UnsupportedDriverException extends RuntimeException {

    private static final long serialVersionUID = -6037729905488938123L;

    public UnsupportedDriverException(String message) {
        super(message);
    }
}
