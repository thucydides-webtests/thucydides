package net.thucydides.jbehave.internals;

public class ThucydidesStepInitializationError extends RuntimeException {
    public ThucydidesStepInitializationError(Exception cause) {
        super(cause);
    }
}
