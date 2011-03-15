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

    public static TestStep successfulTestStepCalled(String description, String group) {
        return createNewTestStep(description, group, SUCCESS);
    }

    public static TestStep failingTestStepCalled(String description) {
        return createNewTestStep(description, FAILURE);
    }
    
    public static TestStep failingTestStepCalled(String description, String group) {
        return createNewTestStep(description, group, FAILURE);
    }
    
    public static TestStep skippedTestStepCalled(String description) {
        return createNewTestStep(description, SKIPPED);
    }

    public static TestStep skippedTestStepCalled(String description, String group) {
        return createNewTestStep(description, group, SKIPPED);
    }

    public static TestStep ignoredTestStepCalled(String description) {
        return createNewTestStep(description, IGNORED);
    }
    
    public static TestStep ignoredTestStepCalled(String description, String group) {
        return createNewTestStep(description, group, IGNORED);
    }
    
    public static TestStep pendingTestStepCalled(String description) {
        return createNewTestStep(description, PENDING);
    }
    
    public static TestStep pendingTestStepCalled(String description, String group) {
        return createNewTestStep(description, group, PENDING);
    }

    public static TestStep createNewTestStep(String description, TestResult result) {
        TestStep step = new TestStep(description);
        step.setResult(result);
        return step;
    }
    
    public static TestStep createNewTestStep(String description, String group, TestResult result) {
        TestStep step = new TestStep(description);
        step.setResult(result);
        step.setGroup(group);
        return step;
    }
    
}
