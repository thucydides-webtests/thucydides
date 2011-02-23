package net.thucydides.junit.hamcrest;

import net.thucydides.core.reports.AcceptanceTestReporter;

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
