package net.thucydides.core.model;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

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

    private String description;    
    private File screenshot;
    private String screenshotPath;
    private String group;
    private TestResult result;
    private String errorMessage;
    private Throwable cause;
    private long duration;
    private long startTime;
    
    private Set<String> testedRequirement = new HashSet<String>();
    
    public TestStep() {
        startTime = System.currentTimeMillis();
    }

    public TestStep(final String description) {
        this();
        this.description = description;
    }

    public void recordDuration() {
        setDuration(System.currentTimeMillis() - startTime);
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public void testsRequirement(final String requirement) {
        testedRequirement.add(requirement);
    }
    
    public Set<String> getTestedRequirements() {
        return ImmutableSet.copyOf(testedRequirement);
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
     * Each test step has a result, indicating the outcome of this step.
     */
    public void setResult(final TestResult result) {
        this.result = result;
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

    public Throwable getException() {
        return cause;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getGroup() {
        return group;
    }
    
    public boolean isInGroup(final String aGroup) {
        return aGroup.equals(group);
    }

}
