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
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep

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


    def "a requirement with no associated tests is pending"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the overall outcome should all be pending"
            outcomes.completedRequirementsCount == 0
    }

    def "a requirement with only pending tests is pending"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(someTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the overall outcome should all be pending"
            outcomes.completedRequirementsCount == 0
    }


    def "a requirement with only passing tests is completed"() {
        given: "there are some passing tests"
            def noTestOutcomes = TestOutcomes.of(somePassingTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "requirements with passing tests should be completed"
            outcomes.completedRequirementsCount == 1
    }

    def "a requirement with a failing tests is a failure"() {
        given: "there are some passing tests"
            def noTestOutcomes = TestOutcomes.of(someFailingTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "requirements with passing tests should be completed"
            outcomes.failingRequirementsCount == 1
    }

    def someTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        return [testOutcome1, testOutcome2]

    }

    def somePassingTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1").withResult(TestResult.SUCCESS))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        return [testOutcome1, testOutcome2, testOutcome3]

    }

    def someFailingTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1").withResult(TestResult.SUCCESS))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome3.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Feed chickens cake", Story.called("Feed chickens"))
        testOutcome4.recordStep(TestStep.forStepCalled("step 4").withResult(TestResult.FAILURE))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }

}
