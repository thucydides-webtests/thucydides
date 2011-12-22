package net.thucydides.plugins.jira.service;

import net.thucydides.core.ThucydidesSystemProperty;

/**
 * Obtain the JIRA configuration details from system properties.
 */
public class SystemPropertiesJIRAConfiguration implements JIRAConfiguration {

    public static final String JIRA_URL = "jira.url";
    public static final String JIRA_USERNAME = "jira.username";
    public static final String JIRA_PASSWORD = "jira.password";
    public static final String JIRA_WIKI_RENDERER = "jira.wiki.renderer";

    public String getJiraUser() {
        return System.getProperty(JIRA_USERNAME);
    }

    public String getJiraPassword() {
        return System.getProperty(JIRA_PASSWORD
        );
    }

    public boolean isWikiRenderedActive() {
        return Boolean.valueOf(System.getProperty(JIRA_WIKI_RENDERER, "true"));
    }

    public String getJiraWebserviceUrl() {
        String baseUrl = System.getProperty(JIRA_URL);
        return baseUrl + "/rpc/soap/jirasoapservice-v2";
    }


}
