package net.thucydides.core.model;

import java.util.Arrays;
import java.util.List;

/**
 * A test step that is actually executed, as opposed to a grouping of test steps.
 *
 * @author johnsmart
 */
public class ConcreteTestStep extends TestStep {

    private TestResult result;

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


    @Override
    public List<? extends TestStep> getFlattenedSteps() {
        return Arrays.asList(this);
    }

    @Override
    public boolean isAGroup() {
        return false;
    }

}
