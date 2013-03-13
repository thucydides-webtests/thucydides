package net.thucydides.core.model;

import net.thucydides.core.steps.StepFailureException;

import static net.thucydides.core.model.TestResult.ERROR;
import static net.thucydides.core.model.TestResult.FAILURE;

/**
 * Determine whether a given type of exception should result in a failure or an error.
 */
public class FailureAnalysis {
    public TestResult resultFor(Throwable testFailureCause) {
        if (AssertionError.class.isAssignableFrom(testFailureCause.getClass())) {
            return FAILURE;
        } else if (failingStepException(testFailureCause)) {
            return FAILURE;
        } else {
            return ERROR;
        }
    }

    private boolean failingStepException(Throwable testFailureCause) {
        return ((StepFailureException.class.isAssignableFrom(testFailureCause.getClass()))
                && (testFailureCause.getCause() != null)
                && (AssertionError.class.isAssignableFrom(testFailureCause.getCause().getClass())));
    }
}
