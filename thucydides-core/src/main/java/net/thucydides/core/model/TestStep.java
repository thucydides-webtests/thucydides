package net.thucydides.core.model;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.google.common.collect.ImmutableSet;

/**
 * An acceptance test run is made up of test steps.
 * Test steps can be either concrete steps or groups of steps.
 * Each concrete step should represent an action by the user, and (generally) an expected outcome.
 * A test step is described by a narrative-style phrase (e.g. "the user clicks 
 * on the 'Search' button', "the user fills in the registration form', etc.).
 * A screenshot is stored for each step.
 * 
 * @author johnsmart
 *
 */
public abstract class TestStep {

    private String description;    
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

    public void testsRequirement(final String requirement) {
        testedRequirement.add(requirement);
    }
    
    public Set<String> getTestedRequirements() {
        return ImmutableSet.copyOf(testedRequirement);
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

    public abstract TestResult getResult();

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

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public abstract List<? extends TestStep> getFlattenedSteps();
    
    public abstract boolean isAGroup();

}
