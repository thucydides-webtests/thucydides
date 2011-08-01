package net.thucydides.core.reports.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format text for HTML reports.
 * In particular, this integrates JIRA links into the generated reports.
 */
public class Formatter {

    private final String issueTrackerUrl;

    private final Pattern issueNumberPattern = Pattern.compile("#\\d+");
    private final String issueUrlFormat = "%s%s";
    private final String issueLinkFormat = "<a href=\"%s\">%s</a>";
    public Formatter(final String issueTrackerUrl) {
        this.issueTrackerUrl = issueTrackerUrl;
    }


    public List<String> issuesIn(final String value) {
        Matcher matcher = issueNumberPattern.matcher(value);

        List<String> issues = new ArrayList<String>();
		while (matcher.find()) {
			issues.add(matcher.group());
		}

        return issues;
    }

    public String addLinks(final String value) {
        String formattedValue = value;
        if (issueTrackerUrl != null) {
            formattedValue = insertIssueTrackingUrls(value);
        }
        return formattedValue;
    }

    private String insertIssueTrackingUrls(String value) {
        String formattedValue = value;
        List<String> issues = issuesIn(value);
        for(String issue : issues) {
            String issueUrl = String.format(issueUrlFormat, issueTrackerUrl, stripLeadingHashFrom(issue));
            String issueLink = String.format(issueLinkFormat, issueUrl, issue);
            formattedValue = formattedValue.replaceAll(issue, issueLink);
        }
        return formattedValue;
    }

    private String stripLeadingHashFrom(final String issue) {
        return issue.substring(1);
    }
}
