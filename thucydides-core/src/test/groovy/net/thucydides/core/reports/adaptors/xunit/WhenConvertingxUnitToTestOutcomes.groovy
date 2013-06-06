package net.thucydides.core.reports.adaptors.xunit

import net.thucydides.core.model.TestOutcome
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
            outcomes.size() == 5
            TestOutcome outcome = outcomes[0];
        then:
            outcome.testCount == 1
            outcome.title == "Should do something"
    }
}
