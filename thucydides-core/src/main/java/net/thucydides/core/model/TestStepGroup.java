package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Test steps can be organized in groups.
 * Test step groups can be nested to any level, and contain either other test step groups
 * or leaf-level test steps.
 *
 */
public class TestStepGroup extends TestStep {

    private List<TestStep> steps = new ArrayList<TestStep>();
    private TestResult defaultResult;
    /**
     * Each test step has a result, indicating the outcome of this step.
     */

    public void setResult(final TestResult result) {
        setDefaultResult(result);
    }

    public void setDefaultResult(final TestResult result) {
        this.defaultResult = result;
    }

    public TestStepGroup(final String description) {
        super(description);
    }

    public void addTestStep(final TestStep step) {
        steps.add(step);
    }

    @Override
    public String toString() {
        return "TestStepGroup{" +
                "description=" + getDescription() +
                "steps=" + steps +
                '}';
    }

    @Override
    public TestResult getResult() {
        TestResultList resultList = new TestResultList(getChildResults());
        if (!resultList.isEmpty()) {
            return resultList.getOverallResult();
        }
        if (defaultResult != null) {
            return defaultResult;
        } else {
            return TestResult.PENDING;
        }
    }

    private List<TestResult> getChildResults() {
        List<TestResult> results = new ArrayList<TestResult>();
        for (TestStep step : steps) {
            results.add(step.getResult());
        }
        return results;
    }

    public List<TestStep> getSteps() {
        return ImmutableList.copyOf(steps);
    }
    
    @Override
    public List<? extends TestStep> getFlattenedSteps() {
        List<TestStep> nestedTestSteps = new ArrayList<TestStep>();
        for (TestStep step : steps) {
            nestedTestSteps.addAll(step.getFlattenedSteps());
        }
        return nestedTestSteps;
    }

    @Override
    public boolean isAGroup() {
        return true;
    }

}
