package net.thucydides.core.requirements

import com.google.common.collect.Lists
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.requirements.reports.RequirementsOutcomes
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory
import spock.lang.Specification
import net.thucydides.core.reports.html.HtmlRequirementsReporter
import net.thucydides.core.requirements.reportpages.RequirementsReport
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.model.TestStep
import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.requirements.model.Requirement

class WhenGeneratingRequirementsReports extends Specification {

    def requirementsProvider = new FileSystemRequirementsTagProvider()
    def htmlRequirementsReporter = new HtmlRequirementsReporter()
    def outputDirectory = newTemporaryDirectory();
    def issueTracking = Mock(IssueTracking)

    def newTemporaryDirectory() {
        def tempDirectory = File.createTempFile("tmp","reports")
        tempDirectory.delete()
        tempDirectory.mkdir()
        tempDirectory.deleteOnExit()
        return tempDirectory
    }

    def "Should list all top-level capabilities in the capabilities report"() {
        given: "there are no associated tests"
            issueTracking.getIssueTrackerUrl() >> "http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider], issueTracking)
        when: "we generate the capability reports"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
            htmlRequirementsReporter.setOutputDirectory(outputDirectory);
            htmlRequirementsReporter.generateReportFor(outcomes);
        then: "all the known capabilities should be listed"
            RequirementsReport report = RequirementsReport.inDirectory(outputDirectory)
            def rows = report.requirements;
        report.names == ['Grow lots of potatoes', 'Grow wheat', 'Raise chickens']
        and: "the title should reflect the requirements type"
            report.title == 'Capabilities'
        and: "the table title should reflect the requirements type"
            report.tableTitle == 'Capabilities'
        and: "card numbers should be displayed for requirement entries"
            rows[0].id == '#CAP-123'
    }

    def "Should know the type of child requirements"() {
        given: "we read the requirements from the directory structure"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider], issueTracking)
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        when: "we get the child requirement type of a requirement"
            Requirement firstRequirement = outcomes.getRequirementOutcomes().get(0).getRequirement();
        then:
            firstRequirement.getChildType() == 'feature'
    }

    def "Should summarize test results in the capabilities report"() {
        given: "there are some associated tests"
            issueTracking.getIssueTrackerUrl() >> "http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}"
            def someTestOutcomes = TestOutcomes.of(someTestResults())
        and: "we read the requirements from the directory structure"
            def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider], issueTracking)
        when: "we generate the capability reports"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(someTestOutcomes)
            htmlRequirementsReporter.setOutputDirectory(outputDirectory);
            htmlRequirementsReporter.generateReportFor(outcomes);
        then: "there should be test results for each capability"
            RequirementsReport report = RequirementsReport.inDirectory(outputDirectory)
            def rows = report.requirements;
            rows[0].tests == 2
            rows[1].tests == 1
            rows[2].tests == 1
        and: "the number of child requirements should be displayed"
            rows[0].children == 3
            rows[1].children == 0
            rows[2].children == 0
        and: "the icons should reflect the test results"
            rows[0].icon.contains("orange") == true
            rows[1].icon.contains("green") == true
            rows[2].icon.contains("red") == true
    }

    def "Should generate reports for child requirements"() {
        given: "there are some associated tests"
            issueTracking.getIssueTrackerUrl() >> "http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}"
            def someTestOutcomes = TestOutcomes.of(someTestResults())
        and: "we read the requirements from the directory structure"
            def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider], issueTracking)
        when: "we generate the capability reports"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(someTestOutcomes)
            HtmlAggregateStoryReporter aggregateReporter = new HtmlAggregateStoryReporter("project", issueTracking)
            aggregateReporter.setOutputDirectory(outputDirectory);
            aggregateReporter.generateRequirementsReportsFor(someTestOutcomes)
        then: "there should be test results for each capability"
            RequirementsReport report = RequirementsReport.inDirectory(outputDirectory)
            def rows = report.requirements;
            // TODO
    }

    def someTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes").withNarrative("Planting some potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feeding poultry", Story.called("raising chickens"))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        TestStep failingStep = new TestStep("look for grain")
        failingStep.failedWith(new AssertionError("No grain left"))
        testOutcome3.recordStep(failingStep)

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Planting wheat", Story.called("planting some wheet"))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Grow wheat").andType("capability")));
        TestStep passingStep = new TestStep("Grow wheat")
        passingStep.setResult(TestResult.SUCCESS)
        testOutcome4.recordStep(passingStep)

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }
}
