package net.thucydides.core.model;

import java.io.File;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

public class TestStepFactory {
    public static TestStep successfulTestStepCalled(String description) {
        return createNewTestStep(description, SUCCESS);
    }
    
    public static TestStep successfulNestedTestStepCalled(String description) {
        return createNewNestedTestSteps(description, SUCCESS);
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
        TestStep step = new TestStep(description);
        step.failedWith(assertionError);
        return step;
    }

    public static TestStep createNewTestStep(String description, TestResult result) {
        TestStep step = new TestStep(description);
        step.setScreenshot(new File(description + ".png"));
        step.setScreenshotPath(description + ".png");
        step.setResult(result);
        step.setDuration(100);
        return step;

    }
    

    public static TestStep createNewNestedTestSteps(String description, TestResult result) {
        TestStep step =  new TestStep(description);
        TestStep child1 = new TestStep(description);
        TestStep child2 = new TestStep(description);

        child1.setScreenshot(new File(description + ".png"));
        child1.setScreenshotPath(description + ".png");
        child1.setResult(result);
        child1.setDuration(100);

        child2.setScreenshot(new File(description + ".png"));
        child2.setScreenshotPath(description + ".png");
        child2.setResult(result);
        child2.setDuration(100);

        step.addChildStep(child1);
        step.addChildStep(child2);

        return step;
    }

}
