package net.thucydides.plugins.jira;

import net.thucydides.plugins.jira.service.JIRAConfiguration;
import net.thucydides.plugins.jira.service.SystemPropertiesJIRAConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenObtainingTheJIRAConfiguration {

    private String originalJiraUsername;
    private String originalJiraPassword;
    private String originalJiraUrl;

    JIRAConfiguration configuration;

    @Before
    public void saveSystemProperties() {
        originalJiraUsername = System.getProperty("jira.username");
        originalJiraPassword = System.getProperty("jira.password");
        originalJiraUrl = System.getProperty("jira.url");

        configuration = new SystemPropertiesJIRAConfiguration();
    }

    @After
    public void restoreSystemProperties() {
        if (originalJiraUsername != null) {
            System.setProperty("jira.username", originalJiraUsername);
        } else {
            System.clearProperty("jira.username");
        }
        if (originalJiraPassword != null) {
            System.setProperty("jira.password", originalJiraPassword);
        } else {
            System.clearProperty("jira.password");
        }
        if (originalJiraUrl != null) {
            System.setProperty("jira.url", originalJiraUrl);
        } else {
            System.clearProperty("jira.url");
        }
    }

    @Test
    public void username_should_be_specified_in_the_jira_username_system_property() {
        System.setProperty("jira.username", "joe");
        assertThat(configuration.getJiraUser(), is("joe"));
    }

    @Test
    public void password_should_be_specified_in_the_jira_password_system_property() {
        System.setProperty("jira.password", "secret");
        assertThat(configuration.getJiraPassword(), is("secret"));
    }

    @Test
    public void base_url_should_be_specified_in_the_jira_url_system_property() {
        System.setProperty("jira.url", "http://build.server/jira");
        assertThat(configuration.getJiraWebserviceUrl(), is("http://build.server/jira/rpc/soap/jirasoapservice-v2"));
    }

}
