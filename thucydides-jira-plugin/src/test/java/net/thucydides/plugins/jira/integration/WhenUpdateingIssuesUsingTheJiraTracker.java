package net.thucydides.plugins.jira.integration;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.plugins.jira.JiraListener;
import net.thucydides.plugins.jira.model.IssueComment;
import net.thucydides.plugins.jira.model.IssueTracker;
import net.thucydides.plugins.jira.model.IssueTrackerUpdateException;
import net.thucydides.plugins.jira.service.JIRAConfiguration;
import net.thucydides.plugins.jira.service.JiraIssueTracker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenUpdateingIssuesUsingTheJiraTracker {

    IssueTracker tracker;

    private static final String JIRA_WEBSERVICE_URL = "http://ec2-122-248-221-171.ap-southeast-1.compute.amazonaws.com:8081/rpc/soap/jirasoapservice-v2";

    private String issueKey;

    private final String CLOSED_ISSUE = "THUCINT-743";

    private IssueHarness testIssueHarness;

    @Mock
    JIRAConfiguration configuration;

    @Mock
    Logger logger;

    @Before
    public void prepareIssueTracker() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(configuration.getJiraUser()).thenReturn("bruce");
        when(configuration.getJiraPassword()).thenReturn("batm0bile");
        when(configuration.getJiraWebserviceUrl()).thenReturn(JIRA_WEBSERVICE_URL);

        testIssueHarness = new IssueHarness(JIRA_WEBSERVICE_URL);
        issueKey = testIssueHarness.createTestIssue();

        tracker = new JiraIssueTracker(logger) {
            @Override
            protected JIRAConfiguration getConfiguration() {
                return configuration;
            }
        };
    }


    @After
    public void deleteTestIssue() throws Exception {
        testIssueHarness.deleteTestIssues();
    }

    @Test
    public void should_be_able_to_add_a_comment_to_an_issue() throws Exception {
        List<IssueComment> comments = tracker.getCommentsFor(issueKey);
        assertThat(comments.size(), is(0));

        tracker.addComment(issueKey, "Integration test comment");

        comments = tracker.getCommentsFor(issueKey);
        assertThat(comments.size(), is(1));
    }


    @Test
    public void should_be_able_to_add_a_comment_to_a_closed_issue() throws Exception {
        List<IssueComment> comments = tracker.getCommentsFor(CLOSED_ISSUE);

        String comment = "Comment on closed test: " + new Date();

        tracker.addComment(CLOSED_ISSUE, comment);

        comments = tracker.getCommentsFor(CLOSED_ISSUE);
        assertThat(comments.size(), greaterThan(0));
        assertThat(comments.get(comments.size() - 1).getText(), is(comment));
    }


    @Test
    public void should_be_able_to_update_a_comment_from_an_issue() throws Exception {
        tracker.addComment(issueKey, "Integration test comment 1");
        tracker.addComment(issueKey, "Integration test comment 2");
        tracker.addComment(issueKey, "Integration test comment 3");

        List<IssueComment> comments = tracker.getCommentsFor(issueKey);

        IssueComment oldComment = comments.get(0);
        IssueComment updatedComment = new IssueComment(oldComment.getId(), "Integration test comment 4", oldComment.getAuthor());

        tracker.updateComment(updatedComment);

        comments = tracker.getCommentsFor(issueKey);
        assertThat(comments.get(0).getText(), is("Integration test comment 4"));
    }

    @Test
    public void should_not_be_able_to_update_a_comment_from_an_issue_that_does_not_exist() throws Exception {
        tracker.addComment("#ISSUE-DOES-NOT-EXIST", "Integration test comment 1");

        verify(logger).error("No JIRA issue found with key {}","#ISSUE-DOES-NOT-EXIST");
    }

    @Test
    public void should_be_able_to_read_the_status_of_an_issue_in_human_readable_form() throws Exception {

        String status = tracker.getStatusFor(issueKey);

        assertThat(status, is("Open"));
    }

    @Test
    public void should_not_be_able_to_update_the_status_of_a_closed_issue() throws Exception {
        tracker.doTransition(CLOSED_ISSUE, "Resolve Issue");
        String newStatus = tracker.getStatusFor(CLOSED_ISSUE);
        assertThat(newStatus, is("Closed"));
    }

    @Test
    public void should_be_able_to_update_the_status_of_an_issue() throws Exception {
        String status = tracker.getStatusFor(issueKey);
        assertThat(status, is("Open"));

        tracker.doTransition(issueKey, "Resolve Issue");

        String newStatus = tracker.getStatusFor(issueKey);
        assertThat(newStatus, is("Resolved"));
    }

    @Test
    public void should_not_be_able_to_update_the_status_of_an_issue_if_transition_is_not_allowed() throws Exception {
        String status = tracker.getStatusFor(issueKey);
        assertThat(status, is("Open"));

        tracker.doTransition(issueKey, "Reopen Issue");

        String newStatus = tracker.getStatusFor(issueKey);
        assertThat(newStatus, is("Open"));
    }

    @Test
    public void should_not_be_able_to_update_the_status_for_an_issue_that_does_not_exist() throws Exception {
        tracker.doTransition("#ISSUE-DOES-NOT-EXIST", "Resolve Issue");

        verify(logger).error("No JIRA issue found with key {}","#ISSUE-DOES-NOT-EXIST");
    }

}
