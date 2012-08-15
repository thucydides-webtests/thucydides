package net.thucydides.core.requirements

import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.requirements.reports.RequirementsOutcomes
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory
import spock.lang.Specification
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.issues.IssueTracking

class WhenCreatingARequirement extends Specification {

    def requirementsProvider = new FileSystemRequirementsTagProvider()
    def issueTracking = Mock(IssueTracking)

    def "Should create a requirement using a simple builder"() {

       when: "we create a simple requirement using a builder"
            def requirement = Requirement.named("some_requirement")
                                         .withOptionalDisplayName("a longer name for display purposes")
                                         .withOptionalCardNumber("CARD-1")
                                         .withType("capability")
                                         .withNarrativeText("as a someone I want something so that something else")
       then: "we should have a correctly instantiated requirement"
    }

    def "should report no test results for requirements without associated tests"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the test results for the requirements should be empty"
            def requirementsTestCount = outcomes.requirementOutcomes.collect {it.testOutcomes.total}
            requirementsTestCount == [0,0,0]
    }

    def "should be able to optionally record the examples used to define a requirement"() {
        when: "we create a simple requirement using a builder"
            def requirement = Requirement.named("some_requirement")
                    .withOptionalDisplayName("a longer name for display purposes")
                    .withOptionalCardNumber("CARD-1")
                    .withType("capability")
                    .withNarrativeText("as a someone I want something so that something else")
        and: "we associate it with some comments"

        then: "we should have a correctly instantiated requirement"
    }
}
