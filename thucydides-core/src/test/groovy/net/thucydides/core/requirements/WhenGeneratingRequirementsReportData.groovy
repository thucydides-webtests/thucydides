package net.thucydides.core.requirements

import com.google.common.collect.Lists
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.requirements.reports.RequirementsOutcomes
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory
import net.thucydides.core.util.MockEnvironmentVariables
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

    def "should report on the number of passing, failing and pending tests for a requirement"() {
        given: "there are some test results"
            def noTestOutcomes = TestOutcomes.of(someVariedTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the number of failing, passing and total tests should be reported"
            outcomes.passingTestCount == 6
            outcomes.failingTestCount == 2
            outcomes.pendingTestCount == 1
            outcomes.skippedTestCount == 1
            outcomes.totalTestCount == 10
    }

    /*
        + grow_potatoes (2 requirements without tests -> 6 unimplemented tests)
            - grow_new_potatoes: 2 tests, 5 steps, 100% success
            - grow_organic_potatoes: 0 tests
            - grow_sweet_potatoes: 0 tests
        - grow_wheat: 0 tests
        - raise_chickens: 8 tests, 8 steps, 37.5% success

        - Total requirements with no tests: 3
        - Estimated tests for requirements with no tests: 3 * 5 = 15
        - Total implemented tests: 10
        - Total implemented and estimated tests: 25
        - Total passing tests: 6
        - Percentage passing tests:  = 24%
     */
    def "functional coverage should cater for requirements with no tests"() {
        given: "there are some test results"
            def noTestOutcomes = TestOutcomes.of(someVariedTestResults())
        and: "we have configured 5 tests per unimplemented requirement"
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.estimated.tests.per.requirement", "5")
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking, environmentVariables)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the percentage of failing, passing and total steps should include estimations for requirements with no tests"
            outcomes.percentagePassingTestCount == 0.24
            outcomes.percentageFailingTestCount == 0.08
            outcomes.percentagePendingTestCount == 0.68
        and: "the number of requirements should be available"
            outcomes.flattenedRequirementCount == 7
            outcomes.requirementsWithoutTestsCount == 3
        and: "the number of tests should be available"
            outcomes.totalTestCount == 10
            outcomes.passingTestCount == 6
            outcomes.failingTestCount == 2
            outcomes.pendingTestCount == 1
            outcomes.estimatedUnimplementedTests == 15
        and: "the results should be available as formatted values"
            outcomes.formatted.percentPassingCoverage == "24%"
            outcomes.formatted.percentFailingCoverage == "8%"
            outcomes.formatted.percentPendingCoverage == "68%"
    }

    def "functional coverage should cater for requirements with no tests at the requirement outcome level"() {
        given: "there are some test results"
            def noTestOutcomes = TestOutcomes.of(someVariedTestResults())
        and: "we have configured 5 tests per unimplemented requirement"
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.estimated.tests.per.requirement", "3")
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider],issueTracking, environmentVariables)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the percentage of failing, passing and total steps should include estimations for requirements with no tests"
            outcomes.requirementOutcomes[0].percentagePassingTestCount == 0.25
            outcomes.requirementOutcomes[0].percentageFailingTestCount == 0.0
            outcomes.requirementOutcomes[0].percentageErrorTestCount == 0.0
            outcomes.requirementOutcomes[0].percentagePendingTestCount == 0.75
        and: "the number of requirements should be available"
            outcomes.requirementOutcomes[0].flattenedRequirementCount == 5
        and: "the number of implemented tests should be available"
            outcomes.requirementOutcomes[0].testCount == 2
            outcomes.requirementOutcomes[0].passingTestCount == 2
            outcomes.requirementOutcomes[0].failingTestCount == 0
            outcomes.requirementOutcomes[0].errorTestCount == 0
            outcomes.requirementOutcomes[0].pendingTestCount == 0
        and: "the number of requirements without tests should be available"
            outcomes.requirementOutcomes[0].requirementsWithoutTestsCount == 2
        and: "the estimated unimplemented test count should be available"
            outcomes.requirementOutcomes[0].estimatedUnimplementedTests == 6
        and: "the results should be available as formatted values"
            outcomes.requirementOutcomes[0].formatted.percentPassingCoverage == "25%"
            outcomes.requirementOutcomes[0].formatted.percentFailingCoverage == "0%"
            outcomes.requirementOutcomes[0].formatted.percentErrorCoverage == "0%"
            outcomes.requirementOutcomes[0].formatted.percentPendingCoverage == "75%"
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
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("capability")));
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

    /*
        + grow_potatoes
            - grow_new_potatoes: 2 tests, 5 steps
            - grow_organic_potatoes: 0 tests
            - grow_sweet_potatoes: 0 tests
        - grow_wheat: 0 tests
        - raise_chickens: 6 tests, 6 steps,
     */
    def someVariedTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("plant potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.1").withResult(TestResult.SUCCESS))
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.2").withResult(TestResult.SUCCESS))
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.3").withResult(TestResult.SUCCESS))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Plant potatoes").andType("story")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("plant potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2.1").withResult(TestResult.SUCCESS))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2.2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Plant potatoes").andType("story")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome3.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Feed chickens cake", Story.called("Feed chickens"))
        testOutcome4.recordStep(TestStep.forStepCalled("step 4").withResult(TestResult.SUCCESS))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome5 = TestOutcome.forTestInStory("Feed chickens bread", Story.called("Feed chickens"))
        testOutcome5.recordStep(TestStep.forStepCalled("step 5").withResult(TestResult.FAILURE))
        testOutcome5.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome6 = TestOutcome.forTestInStory("Feed chickens oranges", Story.called("Feed chickens"))
        testOutcome6.recordStep(TestStep.forStepCalled("step 6").withResult(TestResult.PENDING))
        testOutcome6.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome7 = TestOutcome.forTestInStory("Feed chickens apples", Story.called("Feed chickens"))
        testOutcome7.recordStep(TestStep.forStepCalled("step 7").withResult(TestResult.SKIPPED))
        testOutcome7.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome8 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome8.recordStep(TestStep.forStepCalled("step 8").withResult(TestResult.SUCCESS))
        testOutcome8.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome9 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome9.recordStep(TestStep.forStepCalled("step 9").withResult(TestResult.SUCCESS))
        testOutcome9.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome10 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome10.recordStep(TestStep.forStepCalled("step 10").withResult(TestResult.FAILURE))
        testOutcome10.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4, testOutcome5, testOutcome6, testOutcome7,
                testOutcome8, testOutcome9, testOutcome10]

    }

}
