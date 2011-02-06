package net.thucydides.junit.runners;

import java.io.IOException;

import net.thucydides.core.screenshots.Photographer;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

/**
 * Takes and stores screenshots at strategic points during the tests.
 * @author johnsmart
 *
 */
class ScreenshotListener extends RunListener {

    private final Photographer photographer;
    
    private boolean aPreviousTestHasFailed = false;
    
    public ScreenshotListener(Photographer photographer) {
        this.photographer = photographer;
    }
    
    protected void aTestHasFailed() {
        aPreviousTestHasFailed = true;
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        if (noPreviousTestHasFailed()) {
            takeScreenshotAtEndOfTestFor(aTestCalled(description));
        }
    }
    
    private boolean noPreviousTestHasFailed() {
        return !aPreviousTestHasFailed;
    }

    protected String aTestCalled(Description description) {
        return description.getMethodName();
    }
    
    private void takeScreenshotAtEndOfTestFor(String testName) throws IOException {
        photographer.takeScreenshot(testName);
    }        
}