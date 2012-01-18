package net.thucydides.plugins.jira;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.plugins.jira.model.IssueComment;
import net.thucydides.plugins.jira.model.IssueTracker;
import net.thucydides.plugins.jira.service.NoSuchIssueException;
import net.thucydides.plugins.jira.workflow.ClasspathWorkflowLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenUpdatingCommentsInJIRA {

    @Feature
    public static final class SampleFeature {
        public class SampleStory {}
        public class SampleStory2 {}
    }

    @Story(SampleFeature.SampleStory.class)
    private static final class SampleTestSuite {

        @Title("Test for issue #MYPROJECT-123")
        public void issue_123_should_be_fixed_now() {}

        @Title("Fixes issues #MYPROJECT-123,#MYPROJECT-456")
        public void issue_123_and_456_should_be_fixed_now() {}

        public void anotherTest() {}
    }

    @Story(SampleFeature.SampleStory.class)
    private static final class SampleTestSuiteWithoutPrefixes {

        @Title("Test for issue #123")
        public void issue_123_should_be_fixed_now() {}

        @Title("Fixes issues #123,#456")
        public void issue_123_and_456_should_be_fixed_now() {}

        public void anotherTest() {}
    }

    @Story(SampleFeature.SampleStory.class)
    private static final class SampleTestSuiteWithIssueAnnotation {

        @Issue("#MYPROJECT-123")
        public void issue_123_should_be_fixed_now() {}

        @Issues({"MYPROJECT-123", "MYPROJECT-456"})
        public void issue_123_and_456_should_be_fixed_now() {}

        public void anotherTest() {}
    }
    ClasspathWorkflowLoader workflowLoader;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("jira.url", "http://my.jira.server");
        environmentVariables.setProperty("thucydides.public.url", "http://my.server/myproject/thucydides");
        environmentVariables.setProperty("thucydides.jira.workflow.active","true");
        environmentVariables.setProperty("build.id","2012-01-17_15-39-03");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
    }

    @After
    public void resetPluginSpecificProperties() {
        System.clearProperty("thucydides.skip.jira.updates");
    }

    @Mock
    IssueTracker issueTracker;

    EnvironmentVariables environmentVariables;

    private TestOutcome newTestOutcome(String testMethod, TestResult testResult) {
        TestOutcome result = TestOutcome.forTest(testMethod, SampleTestSuite.class);
        TestStep step = new TestStep("a narrative description");
        step.setResult(testResult);
        result.recordStep(step);
        return result;
    }

    @Test
    public void when_a_test_with_a_referenced_issue_finishes_the_plugin_should_add_a_new_comment_for_this_issue() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void should_not_add_the_project_prefix_to_the_issue_number_if_already_present() {
        MockEnvironmentVariables mockEnvironmentVariables = prepareMockEnvironment();

        JiraListener listener = new JiraListener(issueTracker, mockEnvironmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void should_add_the_project_prefix_to_the_issue_number_if_not_already_present() {
        MockEnvironmentVariables mockEnvironmentVariables = prepareMockEnvironment();

        JiraListener listener = new JiraListener(issueTracker, mockEnvironmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuiteWithoutPrefixes.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    private MockEnvironmentVariables prepareMockEnvironment() {
        MockEnvironmentVariables mockEnvironmentVariables = new MockEnvironmentVariables();
        mockEnvironmentVariables.setProperty("jira.project", "MYPROJECT");
        mockEnvironmentVariables.setProperty("jira.url", "http://my.jira.server");
        mockEnvironmentVariables.setProperty("thucydides.public.url", "http://my.server/myproject/thucydides");
        return mockEnvironmentVariables;
    }

    @Test
    public void when_a_test_with_a_referenced_annotated_issue_finishes_the_plugin_should_add_a_new_comment_for_this_issue() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuiteWithIssueAnnotation.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void when_a_test_with_several_referenced_issues_finishes_the_plugin_should_add_a_new_comment_for_each_issue() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_and_456_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_and_456_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
        verify(issueTracker).addComment(eq("MYPROJECT-456"), anyString());
    }

    @Test
    public void when_a_test_with_several_annotated_referenced_issues_finishes_the_plugin_should_add_a_new_comment_for_each_issue() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuiteWithIssueAnnotation.class);
        listener.testStarted("issue_123_and_456_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_and_456_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
        verify(issueTracker).addComment(eq("MYPROJECT-456"), anyString());
    }
    @Mock
    ExecutedStepDescription stepDescription;

    @Mock
    StepFailure failure;

    @Test
    public void should_add_one_comment_even_when_several_steps_are_called() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_and_456_should_be_fixed_now");

        listener.stepStarted(stepDescription);
        listener.stepFinished();

        listener.stepStarted(stepDescription);
        listener.stepFailed(failure);

        listener.stepStarted(stepDescription);
        listener.stepIgnored();

        listener.stepStarted(stepDescription);
        listener.stepPending();

        listener.testFailed(new AssertionError("Oops!"));

        listener.testFinished(newTestOutcome("issue_123_and_456_should_be_fixed_now", TestResult.FAILURE));

        listener.testStarted("anotherTest");
        listener.testIgnored();
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
        verify(issueTracker).addComment(eq("MYPROJECT-456"), anyString());
    }

    @Test
    public void should_work_with_a_story_class() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(net.thucydides.core.model.Story.from(SampleTestSuite.class));
        listener.testStarted("Fixes issues #MYPROJECT-123");
        listener.testFinished(newTestOutcome("Fixes issues #MYPROJECT-123", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"),
                contains("[Thucydides Test Results|http://my.server/myproject/thucydides/sample_test_suite.html]"));
    }

    @Test
    public void the_comment_should_contain_a_link_to_the_corresponding_story_report() {
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker).addComment(eq("MYPROJECT-123"),
                contains("[Thucydides Test Results|http://my.server/myproject/thucydides/sample_story.html]"));
    }

    @Test
    public void should_update_existing_thucydides_report_comments_if_present() {

        List<IssueComment> existingComments = Arrays.asList(new IssueComment(1L,"a comment", "bruce"),
                                                            new IssueComment(2L,"Thucydides Test Results", "bruce"));
        when(issueTracker.getCommentsFor("MYPROJECT-123")).thenReturn(existingComments);

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker).updateComment(any(IssueComment.class));
    }


    @Test
    public void should_not_update_status_if_issue_does_not_exist() {
        when(issueTracker.getStatusFor("MYPROJECT-123"))
                .thenThrow(new NoSuchIssueException("It ain't there no more."));

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(anyString(), anyString());
    }

    @Test
    public void should_not_update_status_if_jira_url_is_undefined() {
        MockEnvironmentVariables environmentVariables = prepareMockEnvironment();
        environmentVariables.setProperty("jira.url","");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(anyString(), anyString());
    }

    @Test
    public void should_skip_JIRA_updates_if_requested() {
        MockEnvironmentVariables environmentVariables = prepareMockEnvironment();
        environmentVariables.setProperty("thucydides.skip.jira.updates","true");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker, never()).addComment(anyString(), anyString());
    }


    @Test
    public void should_skip_JIRA_updates_if_no_public_url_is_specified() {

        MockEnvironmentVariables environmentVariables = prepareMockEnvironment();
        environmentVariables.setProperty("thucydides.public.url","");
        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker, never()).addComment(anyString(), anyString());
    }

    @Test
    public void default_listeners_should_use_default_issue_tracker() {
        JiraListener listener = new JiraListener();

        assertThat(listener.getIssueTracker(), is(notNullValue()));
    }

    @Test
    public void a_passing_test_should_resolve_an_open_issue() {

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Resolve Issue");
    }

    @Test
    public void a_failing_test_should_open_a_closed_issue() {

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Closed");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
    }

    @Test
    public void a_failing_and_a_passing_test_should_open_a_closed_issue() {

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Closed","Open");

        JiraListener listener = new JiraListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestSuite.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE));
        listener.testStarted("issue_123_and_456_should_be_fixed_now");
        listener.testFinished(newTestOutcome("issue_123_and_456_should_be_fixed_now", TestResult.SUCCESS));
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
        verify(issueTracker, never()).doTransition("MYPROJECT-123", "Resolve Issue");
    }

}
