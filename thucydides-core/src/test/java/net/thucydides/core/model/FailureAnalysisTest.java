package net.thucydides.core.model;

import net.thucydides.core.webdriver.WebdriverAssertionError;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;
import org.omg.CORBA.COMM_FAILURE;

import static junit.framework.Assert.assertEquals;

public class FailureAnalysisTest {
    private FailureAnalysis fixture = new FailureAnalysis();

    @Test
    public void an_assertionerror_is_a_failure() {
        assertEquals(TestResult.FAILURE, fixture.resultFor(new AssertionError("test message")));
    }
    @Test
    public void a_subclass_of_assertionerror_is_a_failure() {
        assertEquals(TestResult.FAILURE, fixture.resultFor(new ArrayComparisonFailure("test message", new AssertionError("wrapped exception"), 1)));
    }

    @Test
    public void a_webdriverassertion_with_assertionerror_cause_is_a_failure() {
        assertEquals(TestResult.FAILURE, fixture.resultFor(new WebdriverAssertionError(new AssertionError("wrapped assertion error"))));
    }

    @Test
    public void a_non_assertion_error_is_an_error() {
        assertEquals(TestResult.ERROR, fixture.resultFor(new RuntimeException("message")));
    }

    @Test
    public void a_webdriverassertion_with_a_non_assertion_cause_is_an_error() throws Exception {
        assertEquals(TestResult.ERROR, fixture.resultFor(new WebdriverAssertionError(new NullPointerException())));
    }
}
