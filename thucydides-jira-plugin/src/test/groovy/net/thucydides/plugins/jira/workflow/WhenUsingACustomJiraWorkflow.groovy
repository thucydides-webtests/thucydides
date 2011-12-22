package net.thucydides.plugins.jira.workflow

import spock.lang.Specification
import static net.thucydides.core.model.TestResult.FAILURE
import static net.thucydides.core.model.TestResult.IGNORED
import static net.thucydides.core.model.TestResult.PENDING
import static net.thucydides.core.model.TestResult.SKIPPED
import static net.thucydides.core.model.TestResult.SUCCESS
import net.thucydides.plugins.jira.guice.Injectors

class WhenUsingACustomJiraWorkflow extends Specification {

    def workflow

    def setupSpec() {
        System.properties['thucydides.jira.workflow'] = 'custom-workflow.groovy'
    }

    def cleanupSpec() {
        System.properties.remove('thucydides.jira.workflow')
    }

    def setup() {
        workflow = Injectors.getInjector().getInstance(WorkflowLoader).load()
    }

    def "should load a custom workflow defined in the thucydides.jira.workflow system property"() {

        expect:
            println workflow.transitions
            def transitions = workflow.transitions.forTestResult(result).whenIssueIs(issueStatus)
            transitions == expectedTransitions

        where:
            issueStatus             | result          | expectedTransitions
            'Open'                  | SUCCESS         | ['Resolve Issue']
            'Pending Validation'    | FAILURE         | ['Reopen Issue']
   }

}