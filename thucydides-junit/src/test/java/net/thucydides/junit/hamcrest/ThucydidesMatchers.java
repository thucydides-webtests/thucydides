package net.thucydides.junit.hamcrest;

import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.junit.runners.OrderedTestStepMethod;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class ThucydidesMatchers {

    @Factory
    public static ContainsAnInstanceOfMatcher containsAReportOfType(Class<? extends AcceptanceTestReporter> reporterClass) {
        return new ContainsAnInstanceOfMatcher(reporterClass);
    }
    
    @Factory
    public static OrderedTestStepLessThanMatcher lessThan(OrderedTestStepMethod testStep) {
        return new OrderedTestStepLessThanMatcher(testStep);
    }
    
    @Factory
    public static OrderedTestStepGreaterThanMatcher greaterThan(OrderedTestStepMethod testStep) {
        return new OrderedTestStepGreaterThanMatcher(testStep);
    }
    
    @Factory
    public static Matcher<Failure> hasMessage(Matcher<String> matcher){
        return new FailureWithMessageMatcher(matcher);
    }
    
    @Factory
    public static Matcher<Failure> hasMethodName(Matcher<String> matcher){
        return new FailureWithMethodNamedMatcher(matcher);
    }

    @Factory
    public static Matcher<Description> hasDescriptionMethodName(Matcher<String> matcher){
        return new DescriptionWithMethodNameMatcher(matcher);
    }
        
}
