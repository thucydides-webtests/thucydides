package net.thucydides.core.model;

import net.thucydides.core.PendingStepException;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepFailureException;
import net.thucydides.core.webdriver.WebdriverAssertionError;
import org.apache.regexp.RETest;

import static net.thucydides.core.model.TestResult.ERROR;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.PENDING;

/**
 * Determine whether a given type of exception should result in a failure or an error.
 * Any exception  that extends AssertionError is a FAILURE.
 * Any exception  that extends WebdriverAssertionError and has a cause that is an AssertionError is also a FAILURE.
 * All other exceptions are an ERROR (except for StepFailureException as described below)
 *
 * Any exception that extends StepFailureException and has a cause that meets the above criteria is classed as above.
 * All other exceptions are an ERROR
 */
public class FailureAnalysis {
    public TestResult resultFor(Throwable testFailureCause) {
        if (PendingStepException.class.isAssignableFrom(testFailureCause.getClass())) {
            return PENDING;
        } else if (isFailureError(testFailureCause)) {
            return FAILURE;
        } else if (failingStepException(testFailureCause)) {
            return FAILURE;
        } else {
            return ERROR;
        }
    }

    public TestResult resultFor(StepFailure stepFailure) {
        if (stepFailure.getException() == null) {
            return FAILURE;
        } else {
            return resultFor(stepFailure.getException());
        }
    }

    private boolean failingStepException(Throwable testFailureCause) {
        return ((StepFailureException.class.isAssignableFrom(testFailureCause.getClass()))
                && (testFailureCause.getCause() != null)
                && (isFailureError(testFailureCause.getCause())));
    }

    private boolean isFailureError(Throwable testFailureCause) {
        Class<? extends Throwable> failureCauseClass = testFailureCause.getClass();

        if(WebdriverAssertionError.class.isAssignableFrom(failureCauseClass)) {
            return testFailureCause.getCause() == null || AssertionError.class.isAssignableFrom(testFailureCause.getCause().getClass());
        }
        return AssertionError.class.isAssignableFrom(failureCauseClass);
    }
}
