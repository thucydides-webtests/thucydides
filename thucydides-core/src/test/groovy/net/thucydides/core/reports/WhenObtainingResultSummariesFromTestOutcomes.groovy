package net.thucydides.core.reports

import net.thucydides.core.model.TestResult
import spock.lang.Specification
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled

class WhenObtainingResultSummariesFromTestOutcomes extends Specification {

    def loader = new TestOutcomeLoader()

    def "should count the number of successful tests in a set"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        then:
            testOutcomes.total == 3
    }

    def "should determine the correct overall result for a set of tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
            testOutcomes.result == result
        where:
            directory                                  | result
            "/test-outcomes/all-successful"            | TestResult.SUCCESS
            "/test-outcomes/containing-failure"        | TestResult.FAILURE
            "/test-outcomes/containing-pending"        | TestResult.PENDING
            "/test-outcomes/containing-skipped"        | TestResult.SUCCESS
    }

    def "should find the total number of tests with a given result in a test outcome set"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
            testOutcomes.successCount == successCount &&
            testOutcomes.failureCount == failureCount &&
            testOutcomes.pendingCount == pendingCount &&
            testOutcomes.skipCount == skipCount
        where:
            directory                                  | successCount | failureCount | pendingCount | skipCount
            "/test-outcomes/all-successful"            | 3            | 0            | 0            | 0
            "/test-outcomes/containing-failure"        | 1            | 1            | 1            | 0
            "/test-outcomes/containing-pending"        | 2            | 0            | 1            | 0
            "/test-outcomes/containing-skipped"        | 3            | 0            | 0            | 1
    }
    
    def "should count the number steps in a set of test outcomes"() {
        when:
           def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        then:
            testOutcomes.stepCount == 17
    }
    
    def "should calculate the percentage of passing steps"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
            testOutcomes.percentagePassingStepCount == percentagePassing &&
            testOutcomes.percentageFailingStepCount == percentageFailing &&
            testOutcomes.percentagePendingStepCount == percentagePending
        where:
            directory                                  | percentagePassing | percentageFailing  | percentagePending
            "/test-outcomes/all-successful"            | 1.0               | 0.0                | 0.0
            "/test-outcomes/containing-failure"        | 0.32              | 0.24               | 0.44
            "/test-outcomes/containing-pending"        | 0.6               | 0.0                | 0.4
            "/test-outcomes/all-pending"               | 0.0               | 0.0                | 1.0
    }

    def "should provide a formatted version of the passing coverage"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        then:
            testOutcomes.formatted.percentPassingCoverage == "32%"
    }

    def "should provide a formatted version of the failing coverage metrics"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        then:
            testOutcomes.formatted.percentFailingCoverage == "24%"
    }

    def "should provide a formatted version of the pending coverage metrics"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        then:
            testOutcomes.formatted.percentPendingCoverage == "44%"
    }

    def "should return 0% passing coverage if there are no steps"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/with-no-steps"));
        then:
            testOutcomes.formatted.percentPassingCoverage == "0%"
    }

    def "should return 0% failing coverage if there are no steps"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/with-no-steps"));
        then:
            testOutcomes.formatted.percentFailingCoverage == "0%"
    }

    def "should return 100% pending coverage if there are no steps"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/with-no-steps"));
        then:
            testOutcomes.formatted.percentPendingCoverage == "100%"
    }

}