package net.thucydides.junit.hamcrest;

import net.thucydides.junit.runners.OrderedTestStepMethod;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class OrderedTestStepGreaterThanMatcher extends TypeSafeMatcher<OrderedTestStepMethod> {
    
    private final OrderedTestStepMethod testStep;
    
    public OrderedTestStepGreaterThanMatcher(OrderedTestStepMethod testStep) {
        this.testStep = testStep;
    }

    public boolean matchesSafely(OrderedTestStepMethod anotherTestStep) {
        return testStep.compareTo(anotherTestStep) < 0;
    }

    public void describeTo(Description description) {
        description.appendText("greated than '").appendText(testStep.toString()).appendText("'");
    }
}
