package net.thucydides.core.requirements

import com.google.common.collect.Lists
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.requirements.reports.RequirementsOutcomes
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory
import spock.lang.Specification
import net.thucydides.core.issues.IssueTracking

class WhenGeneratingRequirementsReportData extends Specification {

    def requirementsProvider = new FileSystemRequirementsTagProvider()
    def issueTracking = Mock(IssueTracking)

    def "Should list all top-level capabilities in the capabilities report"() {

        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "all the known capabilities should be listed"
            def requirementsNames = outcomes.requirementOutcomes.collect {it.requirement.name}
            requirementsNames == ["Grow potatoes", "Grow wheat", "Raise chickens"]
        and: "the display name should be obtained from the narrative file where present"
            def requirementsDisplayNames = outcomes.requirementOutcomes.collect {it.requirement.displayName}
        requirementsDisplayNames == ["Grow lots of potatoes", "Grow wheat", "Raise chickens"]
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

    def "should report narrative test for each requirement"() {
        given: "we read the requirements from the directory structure"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
            def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the requirement outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the requirement outcomes will contain the requirement narratives when specified"
            def requirementsNarratives = outcomes.requirementOutcomes.collect {it.requirement.narrativeText}
            requirementsNarratives[0].contains("In order to let my country eat chips") == true
            requirementsNarratives[1] == "Grow wheat"
            requirementsNarratives[2] == "Raise chickens"
    }

    public void "should report test results associated with specified requirements"() {
        given: "we have a set of test outcomes"
            def someTestOutcomes = TestOutcomes.of(someTestResults())
            def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the requirement outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(someTestOutcomes)
        then: "the number of tests for each requirement should be recorded in the requirements outcome"
            def requirementsTestCount = outcomes.requirementOutcomes.collect {it.testOutcomes.total}
            requirementsTestCount == [2,0,0]
    }

    def someTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        return [testOutcome1, testOutcome2]

    }
}
