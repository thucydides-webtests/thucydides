package net.thucydides.core.model;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.List;

/**
 * A list of test results, used to determine the overall test result.
 *
 */
public class TestResultList {

    private final List<TestResult> testResults;
    
    public TestResultList(final List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public TestResult getOverallResult() {
            if (testResults.contains(FAILURE)) {
                return FAILURE;
            }

            if (testResults.contains(PENDING)) {
                return PENDING;
            }

            if (containsOnly(testResults, IGNORED)) {
                return IGNORED;
            }

            return SUCCESS;
    }

    private boolean containsOnly(final List<TestResult> testResults, final TestResult value) {
        for (TestResult result : testResults) {
            if (result != value) {
                return false;
            }
        }
        return true;
    }

}
