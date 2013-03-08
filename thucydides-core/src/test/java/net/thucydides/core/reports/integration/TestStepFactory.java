package net.thucydides.core.reports.integration;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;

import static net.thucydides.core.model.TestResult.ERROR;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

public class TestStepFactory {

    public static TestStep successfulTestStepCalled(String description) {
        return createNewTestStep(description, SUCCESS);
    }

    public static TestStep failingTestStepCalled(String description) {
        return createNewTestStep(description, FAILURE);
    }

    public static TestStep errorTestStepCalled(String description) {
        return createNewTestStep(description, ERROR);
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

    private static TestStep createNewTestStep(String description, TestResult result) {
        TestStep step = new TestStep(description);
        step.setResult(result);
        return step;
    }
}