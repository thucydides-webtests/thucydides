package net.thucydides.junit.hamcrest;

import net.thucydides.junit.runners.OrderedTestStepMethod;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class OrderedTestStepLessThanMatcher extends TypeSafeMatcher<OrderedTestStepMethod> {
    
    private final OrderedTestStepMethod testStep;
    
    public OrderedTestStepLessThanMatcher(OrderedTestStepMethod testStep) {
        this.testStep = testStep;
    }

    public boolean matchesSafely(OrderedTestStepMethod anotherTestStep) {
        return testStep.compareTo(anotherTestStep) > 0;
    }

    public void describeTo(Description description) {
        description.appendText("less than '").appendText(testStep.toString()).appendText("'");
    }
}
