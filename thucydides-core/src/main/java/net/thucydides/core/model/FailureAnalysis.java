package net.thucydides.core.model;

import net.thucydides.core.steps.StepFailureException;

import static net.thucydides.core.model.TestResult.ERROR;
import static net.thucydides.core.model.TestResult.FAILURE;

/**
 * Determine whether a given type of exception should result in a failure or an error.
 */
public class FailureAnalysis {
    public TestResult resultFor(Throwable testFailureCause) {
        if (testFailureCause.getClass().isAssignableFrom(AssertionError.class)) {
            return FAILURE;
        } else if (failingStepException(testFailureCause)) {
            return FAILURE;
        } else {
            return ERROR;
        }
    }

    private boolean failingStepException(Throwable testFailureCause) {
        return ((testFailureCause.getClass().isAssignableFrom(StepFailureException.class))
                && (testFailureCause.getCause() != null)
                && (testFailureCause.getCause().getClass().isAssignableFrom(AssertionError.class)));
    }
}
