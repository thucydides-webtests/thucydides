package net.thucydides.core.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Represents the results of an acceptance test execution.
 * This includes the narrative steps taken during the test, screenshots
 * at each step, the results of each step, and the overall result.
 * 
 * @author johnsmart
 *
 */
public class AcceptanceTestRun {

    final private List<TestStep> testSteps = new ArrayList<TestStep>();
    
    private String title;
    
    /**
     * Create a new acceptance test run instance.
     */
    public AcceptanceTestRun() {
    }

    public AcceptanceTestRun(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        Preconditions.checkState(this.title == null, "Test runs are immutable - the title can only be defined once.");
        this.title = title;
    }
    /**
     * An acceptance test run always has a title.
     * The title should be something like the name of the user story being tested,
     * possibly with some precisions if several test cases test the same user story.
     * If the test cases are written using a BDD style, the name can be derived directly
     * from the test case name.
     */
    public String getTitle() {
        return title;
    }

    public void recordStep(TestStep step) {
        Preconditions.checkNotNull(step.getDescription(), "The test step description was not defined.");
        Preconditions.checkNotNull(step.getResult(), "The test step result was not defined");
        testSteps.add(step);
        
    }

    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }

}
