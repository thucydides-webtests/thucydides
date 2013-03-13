package net.thucydides.core.model;

import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;
import org.omg.CORBA.COMM_FAILURE;

import static junit.framework.Assert.assertEquals;

public class FailureAnalysisTest {
    private FailureAnalysis fixture = new FailureAnalysis();
    @Test
    public void testResultFor() throws Exception {
        assertEquals(TestResult.FAILURE, fixture.resultFor(new AssertionError("test message")));
        assertEquals(TestResult.FAILURE, fixture.resultFor(new ArrayComparisonFailure("test message", new AssertionError("wrapped exception"), 1)));
        assertEquals(TestResult.ERROR, fixture.resultFor(new RuntimeException("message")));
    }
}
