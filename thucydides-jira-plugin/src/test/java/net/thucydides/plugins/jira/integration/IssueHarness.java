package net.thucydides.plugins.jira.integration;

import net.thucydides.plugins.jira.client.SOAPSession;
import thucydides.plugins.jira.soap.RemoteIssue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class IssueHarness {

    private final String jiraUrl;
    private List<String> testIssueKeys = new ArrayList<String>();

    public IssueHarness(final String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String createTestIssue() throws Exception {
        SOAPSession session = SOAPSession.openConnectionTo(new URL(jiraUrl))
                                         .usingCredentials("bruce", "batm0bile");

        String token = session.getAuthenticationToken();

        RemoteIssue issue = new RemoteIssue();
        issue.setProject("THUCINT");
        issue.setDescription("A test issue");
        issue.setReporter("bruce");
        issue.setType("1");
        issue.setSummary("A test issue");
        RemoteIssue createdIssue = session.getJiraSoapService().createIssue(token, issue);

        testIssueKeys.add(createdIssue.getKey());

        return createdIssue.getKey();
    }

    public void deleteTestIssues() throws Exception {
        SOAPSession session = SOAPSession.openConnectionTo(new URL(jiraUrl))
                                         .usingCredentials("bruce", "batm0bile");

        String token = session.getAuthenticationToken();

        for(String issueKey : testIssueKeys) {

            session.getJiraSoapService().deleteIssue(token, issueKey);
        }
    }

}
