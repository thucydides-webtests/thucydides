package net.thucydides.core.issues;

import net.thucydides.core.ThucydidesSystemProperty;
import org.apache.commons.lang.StringUtils;

import static net.thucydides.core.ThucydidesSystemProperty.getValue;

/**
 * Determine the issue tracking URL formats for a project.
 */
public class IssueTracking {

    public static String getIssueTrackerUrl() {
        if (getValue(ThucydidesSystemProperty.JIRA_URL) != null) {
            return getValue(ThucydidesSystemProperty.JIRA_URL) + "/browse/" + getJiraProjectSuffix() + "{0}";
        } else {
            return getValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL);
        }
    }

    private static String getJiraProjectSuffix() {
        if (!StringUtils.isEmpty(getValue(ThucydidesSystemProperty.JIRA_PROJECT))) {
            return getValue(ThucydidesSystemProperty.JIRA_PROJECT) + "-";
        } else {
            return "";
        }
    }

}
