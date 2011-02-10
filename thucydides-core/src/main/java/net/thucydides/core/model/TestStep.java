package net.thucydides.core.model;

import java.io.File;

import com.google.common.base.Preconditions;
import static net.thucydides.core.model.TestResult.*;

/**
 * An acceptence test run is made up of test steps.
 * Each step should represent an action by the user, and (generally) an expected outcome.
 * A test step is described by a narrative-style phrase (e.g. "the user clicks 
 * on the 'Search' button', "the user fills in the registration form', etc.).
 * A screenshot is stored for each step.
 * 
 * @author johnsmart
 *
 */
public class TestStep {

    private final String description;    
    private File screenshot;
    private TestResult result;
    
    public TestStep(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setScreenshot(File screenshot) {
        Preconditions.checkState(this.screenshot == null, "Test steps are immutable - a screenshot can only be assigned once.");
        this.screenshot = screenshot;
    }
    
    public void setResult(TestResult result) {
        Preconditions.checkState(this.result == null, "Test steps are immutable - the test result can only be assigned once.");
        this.result = result;
    }
   
    public TestResult getResult() {
        return result;
    }
    
    public File getScreenshot() {
        return screenshot;
    }

    public Boolean isSuccessful() {
        return getResult() == SUCCESS;
    }

    public Boolean isFailure() {
        return  getResult() == FAILURE;
    }

    public Boolean isIgnored() {
        return  getResult() == IGNORED;
    }

    public Boolean isSkipped() {
        return  getResult() == SKIPPED;
    }

    public Boolean isPending() {
        return  getResult() == PENDING;
    }

}
