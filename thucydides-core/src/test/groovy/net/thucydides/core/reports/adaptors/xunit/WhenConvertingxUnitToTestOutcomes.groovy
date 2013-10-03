package net.thucydides.core.reports.adaptors.xunit

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.fileInClasspathCalled

/**
 * We want to convert xUnit outputs (possibly with some extra custom fields) to TestOutcomes
 * so that they can be used to generate viable Thucydides test reports.
 */
class WhenConvertingxUnitToTestOutcomes extends Specification {

    def "should convert an xunit test result with one test to a single test outcome"() {

        given:
            def xunitFileDirectory = fileInClasspathCalled("/xunit-sample-output")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.loadOutcomesFrom(xunitFileDirectory)
            TestOutcome outcome = outcomes[0];
        then:
            outcomes.size() == 6
            outcome.testCount == 1
            outcome.title == "Should do something"

    }

    def "should set the test result to SUCCESS for successful testcases"() {
        given:
            def xunitFile = fileInClasspathCalled("/xunit-sample-output/singleTestCase.xml")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.testOutcomesIn(xunitFile)
            TestOutcome outcome = outcomes[0];
        then:
            outcomes.size() == 1
            outcome.testCount == 1
            outcome.title == "Should do something"
            outcome.result == TestResult.SUCCESS
    }

    def "should convert skipped tests into an outcome with Ignored result"() {

        given:
            def xunitFile = fileInClasspathCalled("/xunit-sample-output/skippedTestCase.xml")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.testOutcomesIn(xunitFile)
            TestOutcome outcome = outcomes[0];
        then:
            outcomes.size() == 1
            outcome.testCount == 1
            outcome.title == "Should do something"
            outcome.result == TestResult.IGNORED
    }
}
