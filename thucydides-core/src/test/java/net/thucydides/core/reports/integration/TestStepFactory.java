package net.thucydides.core.reports.integration;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;

public class TestStepFactory {

    public static ConcreteTestStep successfulTestStepCalled(String description) {
        return createNewTestStep(description, SUCCESS);
    }

    public static ConcreteTestStep failingTestStepCalled(String description) {
        return createNewTestStep(description, FAILURE);
    }

    public static ConcreteTestStep skippedTestStepCalled(String description) {
        return createNewTestStep(description, SKIPPED);
    }

    public static ConcreteTestStep ignoredTestStepCalled(String description) {
        return createNewTestStep(description, IGNORED);
    }

    public static ConcreteTestStep pendingTestStepCalled(String description) {
        return createNewTestStep(description, PENDING);
    }

    private static ConcreteTestStep createNewTestStep(String description, TestResult result) {
        ConcreteTestStep step = new ConcreteTestStep(description);
        step.setResult(result);
        return step;
    }
}