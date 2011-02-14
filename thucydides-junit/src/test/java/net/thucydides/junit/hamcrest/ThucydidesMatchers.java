package net.thucydides.junit.hamcrest;

import net.thucydides.core.reports.AcceptanceTestReporter;

import org.hamcrest.Factory;

public class ThucydidesMatchers {

    @Factory
    public static ContainsAnInstanceOfMatcher containsAReportOfType(Class<? extends AcceptanceTestReporter> reporterClass) {
        return new ContainsAnInstanceOfMatcher(reporterClass);
    }
}
