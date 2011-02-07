package net.thucydides.core.model;

/**
 * Acceptance test results. 
 * Records the possible outcomes of tests within an acceptance test case
 * and of the overall acceptance test case itself.
 * 
 * @author johnsmart
 *
 */
public enum TestResult {
    /** 
     * Test failure.
     * For a test case, this means one of the tests in the test case failed.
     */
    FAILURE,    
    
    /**
     * The test or test case ran as expected.
     */
    SUCCESS, 
    
    /**
     * The test or test case was ignored (e.g. via the @Ignore annotation).
     */
    IGNORED, 
    
    /**
     * The test was not executed because a previous test in this test case failed.
     * Doesn't make sense for a test case.
     */
    SKIPPED
}
