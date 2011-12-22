package net.thucydides.plugins.jira.service;

/**
 * JIRA configuration details for the target JIRA instance.
 */
public interface JIRAConfiguration {

    String getJiraUser();

    String getJiraPassword();

    String getJiraWebserviceUrl();

    boolean isWikiRenderedActive();
}
