package net.thucydides.core.csv;

/**
 * Error that occurs if the test data could not be instanciated for some reason.
 */
public class FailedToInitializeTestData extends RuntimeException {
    public FailedToInitializeTestData(String message, Exception cause) {
        super(message, cause);
    }
}
