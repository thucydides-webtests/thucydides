package net.thucydides.core.model;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

public class TestStepFactory {
    public static TestStep successfulTestStepCalled(String description) {
        return createNewTestStep(description, SUCCESS);
    }
    
    public static TestStep failingTestStepCalled(String description, AssertionError assertionError) {
        return createNewTestStep(description, FAILURE, assertionError);
    }
    
    public static TestStep skippedTestStepCalled(String description) {
        return createNewTestStep(description, SKIPPED);
    }

    public static TestStep ignoredTestStepCalled(String description) {
        return createNewTestStep(description, IGNORED);
    }
    
    public static TestStep pendingTestStepCalled(String description) {
        return createNewTestStep(description, PENDING);
    }

    public static TestStep createNewTestStep(String description, TestResult result, AssertionError assertionError) {
        ConcreteTestStep step = new ConcreteTestStep(description);
        step.failedWith(assertionError.getMessage(), assertionError);
        return step;
    }
    public static TestStep createNewTestStep(String description, TestResult result) {
        ConcreteTestStep step = new ConcreteTestStep(description);
        step.setResult(result);
        step.setDuration(100);
        return step;
    }
    
}
