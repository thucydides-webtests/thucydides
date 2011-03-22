package net.thucydides.core.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * A test step that is actually executed, as opposed to a grouping of test steps.
 * 
 * @author johnsmart
 *
 */
public class ConcreteTestStep extends TestStep {

    private File screenshot;
    private String screenshotPath;
    private TestResult result;
    private String errorMessage;
    private Throwable cause;
    
    public ConcreteTestStep() {
        super();
    }

    public ConcreteTestStep(final String description) {
        super(description);
    }


    /**
     * Each test step has a result, indicating the outcome of this step.
     */
    public void setResult(final TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        return result;
    }

    /**
     * Each test step can be associated with a screenshot.
     */
    public void setScreenshot(final File screenshot) {
        this.screenshot = screenshot;
    }
    
    public void setScreenshotPath(final String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }
    
    public String getScreenshotPath() {
        return screenshotPath;
    }
    
    /**
     * Indicate that this step failed with a given error.
     */
    public void failedWith(final String message, final Throwable e) {
        this.errorMessage = message;
        this.cause = e;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public File getScreenshot() {
        return screenshot;
    }


    public Throwable getException() {
        return cause;
    }

    @Override
    public List<? extends TestStep> getFlattenedSteps() {
        return Arrays.asList(this);
    }

    @Override
    public boolean isAGroup() {
        return false;
    }
}
