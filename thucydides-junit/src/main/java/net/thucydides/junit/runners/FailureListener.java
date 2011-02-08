package net.thucydides.junit.runners;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * Keeps track of failing tests, so that subsequent ones can be ignored.
 * @author johnsmart
 *
 */
class FailureListener extends StickyFailureListener {

    public void testFailure(Failure failure) throws Exception {
        aTestHasFailed();        
        super.testFailure(failure);
    }
    
    @Override
    public void testFinished(Description description) throws Exception {
        updatePreviousTestFailures();
        super.testFinished(description);
    }

}    