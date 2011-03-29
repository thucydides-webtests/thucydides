package net.thucydides.core.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Test steps can be organized in groups.
 * Test step groups can be nested to any level, and contain either other test step groups
 * or leaf-level test steps.
 *
 */
public class TestStepGroup extends TestStep {

    private List<TestStep> steps = new ArrayList<TestStep>();

    public TestStepGroup(final String description) {
        super(description);
    }

    public void addTestStep(final TestStep step) {
        steps.add(step);
    }

    @Override
    public TestResult getResult() {
        TestResultList resultList = new TestResultList(getChildResults());
        return resultList.getOverallResult();
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
