package net.thucydides.core.issues;

import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Determine the issue tracking URL formats for a project.
 */
public class IssueTracking {

    protected static ThucydidesSystemProperties getSystemProperties() {
        return ThucydidesSystemProperties.getProperties();
    }

    public static String getIssueTrackerUrl() {


        if (getSystemProperties().isDefined(ThucydidesSystemProperty.JIRA_URL)) {
            return getSystemProperties().getValue(ThucydidesSystemProperty.JIRA_URL)
                                         + "/browse/" + getJiraProjectSuffix() + "{0}";
        } else {
            return getSystemProperties().getValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL);
        }
    }

    private static String getJiraProjectSuffix() {
        if (!getSystemProperties().isEmpty(ThucydidesSystemProperty.JIRA_PROJECT)) {
            return getSystemProperties().getValue(ThucydidesSystemProperty.JIRA_PROJECT) + "-";
        } else {
            return "";
        }
    }

}
