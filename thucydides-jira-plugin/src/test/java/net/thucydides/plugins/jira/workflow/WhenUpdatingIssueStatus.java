package net.thucydides.plugins.jira.workflow;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.plugins.jira.JiraListener;
import net.thucydides.plugins.jira.model.IssueComment;
import net.thucydides.plugins.jira.model.IssueTracker;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenUpdatingIssueStatus {

    @Feature
    public static final class SampleFeature {
        public class SampleStory {}
        public class SampleStory2 {}
    }

    @Story(SampleFeature.SampleStory.class)
    static class SampleTestCase {

        @Issue("#MYPROJECT-123")
        public void issue_123_should_be_fixed_now() {}

        @Issues({"#MYPROJECT-123","#MYPROJECT-456"})
        public void issue_123_and_456_should_be_fixed_now() {}

        public void anotherTest() {}
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(environmentVariables.getProperty("jira.url")).thenReturn("http://my.jira.server");
        when(environmentVariables.getProperty("thucydides.public.url")).thenReturn("http://my.server/myproject/thucydides");
        
        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
    }

    @Mock
    IssueTracker issueTracker;

    @Mock
    EnvironmentVariables environmentVariables;
    
    ClasspathWorkflowLoader workflowLoader;

    @Test
    public void a_successful_test_should_not_update_status_if_workflow_is_not_activated() {

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("false");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"),anyString());
    }

    @Test
    public void a_successful_test_should_not_update_status_if_workflow_update_status_is_not_specified() {

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"),anyString());
    }

    @Test
    public void a_successful_test_should_resolve_an_open_issue() {

        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker).doTransition("MYPROJECT-123", "Resolve Issue");
    }


    @Test
    public void a_successful_test_should_resolve_an_in_progress_issue() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("In Progress");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        InOrder inOrder = inOrder(issueTracker);
        inOrder.verify(issueTracker).doTransition("MYPROJECT-123","Stop Progress");
        inOrder.verify(issueTracker).doTransition("MYPROJECT-123","Resolve Issue");
    }

    @Test
    public void a_successful_test_should_resolve_a_reopened_issue() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Reopened");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker).doTransition("MYPROJECT-123", "Resolve Issue");
    }

    @Test
    public void a_successful_test_should_not_affect_a_resolved_issue() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Resolved");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void a_failing_test_should_open_a_resolved_issue() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Resolved");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
    }

    @Test
    public void a_failing_test_should_open_a_closed_issue() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Closed");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
    }

    @Test
    public void a_failing_test_should_leave_an_open_issue_open() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void a_failing_test_should_leave_a_reopened_issue_reopened() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Reopen");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void a_failing_test_should_leave_in_progress_issue_in_progress() {
        when(environmentVariables.getProperty("thucydides.jira.workflow.active")).thenReturn("true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("In Progress");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Ignore
    @Pending
    @Test
    public void should_maintain_a_stability_score_based_on_the_number_of_recently_passed_tests() {

    }

    private TestOutcome newTestOutcome(String testMethod, TestResult testResult) {
        TestOutcome result = TestOutcome.forTest(testMethod, SampleTestCase.class);
        TestStep step = new TestStep("a narrative description");
        step.setResult(testResult);
        result.recordStep(step);
        return result;
    }
}
