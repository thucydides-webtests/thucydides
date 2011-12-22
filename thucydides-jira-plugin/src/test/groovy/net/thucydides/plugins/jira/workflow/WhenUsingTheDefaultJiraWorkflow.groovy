package net.thucydides.plugins.jira.workflow

import spock.lang.Specification
import static net.thucydides.core.model.TestResult.*
import net.thucydides.plugins.jira.guice.Injectors

class WhenUsingTheDefaultJiraWorkflow extends Specification {

    def workflow

    def setupSpec() {
        System.properties['thucydides.jira.workflow.active'] = 'true'
    }

    def cleanupSpec() {
        System.properties.remove('thucydides.jira.workflow.active')
    }

    def setup() {
        workflow = Injectors.getInjector().getInstance(WorkflowLoader).load()
    }


    def "should load the default workflow if none is specified"() {
        expect:
            def transitions = workflow.getTransitions().forTestResult(result).whenIssueIs(issueStatus)
            transitions == expectedTransitions

        where:
            issueStatus   | result          | expectedTransitions
            'Open'        | SUCCESS         | ['Resolve Issue']
            'Open'        | FAILURE         | []
            'Open'        | IGNORED         | []
            'Open'        | PENDING         | []
            'Open'        | SKIPPED         | []

            'Resolved'    | SUCCESS         | []
            'Resolved'    | FAILURE         | ['Reopen Issue']
            'Resolved'    | IGNORED         | []
            'Resolved'    | PENDING         | []
            'Resolved'    | SKIPPED         | []

            'Closed'      | SUCCESS         | []
            'Closed'      | FAILURE         | ['Reopen Issue']
            'Closed'      | IGNORED         | []
            'Closed'      | PENDING         | []
            'Closed'      | SKIPPED         | []

            'Reopened'    | SUCCESS         | ['Resolve Issue']
            'Reopened'    | FAILURE         | []
            'Reopened'    | IGNORED         | []
            'Reopened'    | PENDING         | []
            'Reopened'    | SKIPPED         | []

            'In Progress' | SUCCESS         | ['Stop Progress', 'Resolve Issue']
            'In Progress' | FAILURE         | []
            'In Progress' | IGNORED         | []
            'In Progress' | PENDING         | []
            'In Progress' | SKIPPED         | []
    }

}