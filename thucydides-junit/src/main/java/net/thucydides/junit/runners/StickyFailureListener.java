package net.thucydides.junit.runners;

import org.junit.runner.notification.RunListener;

/**
 * A listener capable of keeping track of whether tests have already failed.
 *
 */
public class StickyFailureListener extends RunListener {

    public StickyFailureListener() {
        super();
    }

    /**
     * Has the current test failed.
     */
    private boolean theCurrentTestHasFailed = false;
    /**
     * As soon as a test fails, all subsequent tests are ignored.
     */
    private boolean aPreviousTestHasFailed = false;

    protected void updatePreviousTestFailures() {
        if (theCurrentTestHasFailed) {
            aPreviousTestHasFailed = true;
        }
        theCurrentTestHasFailed = false;
    }
    
    protected void aTestHasFailed() {
        this.theCurrentTestHasFailed = true;           
    }

    public boolean aPreviousTestHasFailed() {
        return aPreviousTestHasFailed;
    }
    
    public boolean theCurrentTestHasFailed() {
        return theCurrentTestHasFailed;
    }    
}