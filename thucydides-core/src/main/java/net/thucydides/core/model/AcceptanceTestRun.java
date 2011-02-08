package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
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
 * Represents the results of an acceptance test execution. This includes the
 * narrative steps taken during the test, screenshots at each step, the results
 * of each step, and the overall result.
 * 
 * @composed 1..* steps * TestStep
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

    public void recordStep(TestStep step) {
        Preconditions.checkNotNull(step.getDescription(),
                "The test step description was not defined.");
        Preconditions.checkNotNull(step.getResult(), "The test step result was not defined");
       
        testSteps.add(step);
    }

    public boolean containsOnly(List<TestResult> testResults, TestResult value) {
        for(TestResult result : testResults) {
            if (result != value) {
                return false;
            }
        }
        return true;
    }

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

    static class ExtractTestResultsConverter implements Converter<TestStep, TestResult> {
        public TestResult convert(TestStep step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(testSteps, new ExtractTestResultsConverter());
    }

    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }

}
