package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.*;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.function.convert.Converter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Represents the results of an acceptance test execution. 
 * This includes the narrative steps taken during the test, screenshots at each step, 
 * the results of each step, and the overall result.
 * 
 * @composed 1..* steps * TestStep
 * 
 * @author johnsmart
 * 
 */
public class AcceptanceTestRun {

    private String title;

    final private List<TestStep> testSteps = new ArrayList<TestStep>();

    /**
     * Create a new acceptance test run instance.
     */
    public AcceptanceTestRun() {
    }

    /**
     * The title is immutable once set.
     * For convenience, you can create a test run directly with a title 
     * using this constructor.
     */
    public AcceptanceTestRun(String title) {
        this.title = title;
    }

    /**
     * The test case title.
     * Cannot be modified once set.
     */
    public void setTitle(String title) {
        Preconditions.checkState(this.title == null, "The title can only be defined once.");
        this.title = title;
    }

    /**
     * An acceptance test run always has a title. The title should be something
     * like the name of the user story being tested, possibly with some
     * precisions if several test cases test the same user story. If the test
     * cases are written using a BDD style, the name can be derived directly
     * from the test case name.
     */
    public String getTitle() {
        return title;
    }

    /**
     * An acceptance test is made up of a series of steps.
     * Each step is in fact a small test, which follows on from 
     * the previous one. The outcome of the acceptance test
     * as a whole depends on the outcome of all of the steps.
     */
    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }
    
    /**
     * The outcome of the acceptance test, based on the outcome of the test steps.
     * If any steps fail, the test as a whole is considered a failure. 
     * If any steps are pending, the test as a whole is considered pending.
     * If all of the steps are ignored, the test will be considered 'ignored'.
     * If all of the tests succeed except the ignored tests, the test is a success.
     * @return
     */
    public TestResult getResult() {
        List<TestResult> allTestResults = getCurrentTestResults();

        if (allTestResults.contains(FAILURE)) {
            return FAILURE;
        }
        
        if (allTestResults.contains(PENDING)) {
            return PENDING;
        }
        
        if (containsOnly(allTestResults,IGNORED)) {
            return IGNORED;
        }
        
        return SUCCESS;
    }
    
    /**
     * Add a test step to this acceptance test.
     */
    public void recordStep(TestStep step) {
        Preconditions.checkNotNull(step.getDescription(),
                "The test step description was not defined.");
        Preconditions.checkNotNull(step.getResult(), "The test step result was not defined");
       
        testSteps.add(step);
    }

    private boolean containsOnly(List<TestResult> testResults, TestResult value) {
        for(TestResult result : testResults) {
            if (result != value) {
                return false;
            }
        }
        return true;
    }

    static private class ExtractTestResultsConverter implements Converter<TestStep, TestResult> {
        public TestResult convert(TestStep step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(testSteps, new ExtractTestResultsConverter());
    }

    public Integer getSuccessCount() {
        return select(testSteps, having(on(TestStep.class).isSuccessful())).size();
    }

    public Integer getFailureCount() {
        return select(testSteps, having(on(TestStep.class).isFailure())).size();
    }

    public Integer getIgnoredCount() {
        return select(testSteps, having(on(TestStep.class).isIgnored())).size();
    }

    public Integer getSkippedCount() {
        return select(testSteps, having(on(TestStep.class).isSkipped())).size();
    }

    public Integer getPendingCount() {
        return select(testSteps, having(on(TestStep.class).isPending())).size();
    }
}
