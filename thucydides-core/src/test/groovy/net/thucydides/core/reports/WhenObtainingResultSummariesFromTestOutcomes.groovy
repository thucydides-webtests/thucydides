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
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
            testOutcomes.result == result
        where:
            directory                                  | result
            "/test-outcomes/all-successful"            | TestResult.SUCCESS
            "/test-outcomes/containing-failure"        | TestResult.FAILURE
            "/test-outcomes/containing-nostep-errors"  | TestResult.FAILURE
            "/test-outcomes/containing-errors"         | TestResult.ERROR
            "/test-outcomes/containing-pending"        | TestResult.PENDING
            "/test-outcomes/containing-skipped"        | TestResult.SUCCESS
    }

    def "should find the total number of tests with a given result in a test outcome set"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
            testOutcomes.successCount == successCount &&
            testOutcomes.failureCount == failureCount &&
            testOutcomes.errorCount == errorCount &&
            testOutcomes.pendingCount == pendingCount &&
            testOutcomes.skipCount == skipCount
        where:
            directory                                  | successCount | failureCount | errorCount   | pendingCount | skipCount
            "/test-outcomes/all-successful"            | 3            | 0            | 0            | 0            | 0
            "/test-outcomes/containing-failure"        | 1            | 1            | 0            | 1            | 0
            "/test-outcomes/containing-nostep-errors"  | 1            | 2            | 1            | 1            | 0
            "/test-outcomes/containing-errors"         | 1            | 0            | 2            | 0            | 0
            "/test-outcomes/containing-pending"        | 2            | 0            | 0            | 1            | 0
            "/test-outcomes/containing-skipped"        | 3            | 0            | 0            | 0            | 1
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


    def "should calculate the formatted percentage of passing steps"() {
        when:
        def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
        testOutcomes.decimalPercentagePassingStepCount == percentagePassing &&
                testOutcomes.decimalPercentageFailingStepCount == percentageFailing &&
                testOutcomes.decimalPercentagePendingStepCount == percentagePending
        where:
        directory                                  | percentagePassing | percentageFailing  | percentagePending
        "/test-outcomes/all-successful"            | "1"                 | "0"                | "0"
        "/test-outcomes/containing-failure"        | "0.32"              | "0.24"             | "0.44"
        "/test-outcomes/containing-pending"        | "0.6"               | "0"                | "0.4"
        "/test-outcomes/all-pending"               | "0"                 | "0"                | "1"
    }

    def "should provide a formatted version of the passing coverage"() {
        when:
        def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        then:
        testOutcomes.formatted.percentPassingCoverage == "32%"
    }


    def "should calculate the formatted percentage of passing tests"() {
        when:
        def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled(directory));
        then:
        testOutcomes.decimalPercentagePassingTestCount == percentagePassing &&
                testOutcomes.decimalPercentageFailingTestCount == percentageFailing &&
                testOutcomes.decimalPercentageErrorTestCount == percentageErroring &&
                testOutcomes.decimalPercentagePendingTestCount == percentagePending
        where:
        directory                                 | percentagePassing | percentageFailing | percentageErroring | percentagePending
        "/test-outcomes/all-successful"           | "1"               | "0"               | "0"                | "0"
        "/test-outcomes/containing-failure"       | "0.33"            | "0.33"            | "0"                | "0.33"
        "/test-outcomes/containing-pending"       | "0.67"            | "0"               | "0"                | "0.33"
        "/test-outcomes/containing-nostep-errors" | "0.2"             | "0.4"             | "0.2"              | "0.2"
        "/test-outcomes/containing-errors"        | "0.33"            | "0"               | "0.67"             | "0"
        "/test-outcomes/all-pending"              | "0"               | "0"               | "0"                | "1"
    }

    def "should provide a formatted version of the failing coverage metrics"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        then:
            testOutcomes.formatted.percentFailingCoverage == "24%"
        and:
            testOutcomes.formattedTestCount.percentFailingCoverage == "33.3%"

    }

    def "should provide a formatted version of the pending coverage metrics"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        then:
            testOutcomes.formatted.percentPendingCoverage == "44%"
        and:
            testOutcomes.formattedTestCount.percentPendingCoverage == "33.3%"
    }

    def "should return 0% passing coverage if there are no steps"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/with-no-steps"));
        then:
            testOutcomes.formatted.percentPassingCoverage == "0%"
        and:
            testOutcomes.formattedTestCount.percentPassingCoverage == "0%"
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


    def "should count lines in data-driven tests as individual tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/datadriven"));
        then:
            testOutcomes.total == 14
            testOutcomes.totalTestScenarios == 2
    }


    def "should count results in lines of data-driven tests as individual tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/datadriven"));
        then:
            testOutcomes.successCount == 12
            testOutcomes.failureCount == 2
    }

    def "should count percentage results in lines of data-driven tests as individual tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/datadriven"));
        then:
            testOutcomes.formatted.percentPassingCoverage == "85.7%"
            testOutcomes.formatted.percentFailingCoverage == "14.3%"
    }


    def "should count results correctly in mixed data-driven and normal tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/somedatadriven"));
        then:
            testOutcomes.successCount == 1
            testOutcomes.failureCount == 2
            testOutcomes.pendingCount == 3
            testOutcomes.errorCount == 1
            testOutcomes.total  == 7
            testOutcomes.totalTestScenarios  == 4
    }

    def "should count percentage results correctly in mixed data-driven and normal tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/somedatadriven"));
        then:
            testOutcomes.hasDataDrivenTests()
            testOutcomes.totalDataRows == 5
            testOutcomes.percentagePassingTestCount == 0.14285714285714285
            testOutcomes.percentageFailingTestCount == 0.2857142857142857
            testOutcomes.percentageErrorTestCount == 0.14285714285714285
            testOutcomes.percentagePendingTestCount == 0.42857142857142855
    }

}