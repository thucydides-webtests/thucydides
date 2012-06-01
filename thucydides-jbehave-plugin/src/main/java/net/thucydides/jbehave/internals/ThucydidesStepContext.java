package net.thucydides.jbehave.internals;

/**
 * Keeps track of instantiated JBehave step libraries used in Thucydides tests.
 */
public class ThucydidesStepContext {

    public ThucydidesStepContext() {
    }

    public Object newInstanceOf(Class<?> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new ThucydidesStepInitializationError(e);
        }
    }
}

