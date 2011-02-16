package net.thucydides.junit.hamcrest;

import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.junit.runners.OrderedTestStepMethod;

import org.hamcrest.Factory;

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

}
