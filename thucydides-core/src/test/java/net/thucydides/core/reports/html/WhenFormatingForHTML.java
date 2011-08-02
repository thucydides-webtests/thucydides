package net.thucydides.core.reports.html;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenFormatingForHTML {

    @Test
    public void should_include_issue_tracking_link() {
        Formatter formatter = new Formatter("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        String formattedValue = formatter.addLinks("Fixes issue #123");

        assertThat(formattedValue, is("Fixes issue <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">#123</a>"));
    }

    @Test
    public void should_include_multiple_issue_tracking_links() {
        Formatter formatter = new Formatter("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">#123</a> and <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-456\">#456</a>"));
    }

    @Test
    public void should_identify_issues_in_a_text() {
        Formatter formatter = new Formatter("http://my.issue.tracker");

        List<String> issues = formatter.issuesIn("A scenario about issue #123");

        assertThat(issues, hasItem("#123"));
    }

    @Test
    public void should_identify_multiple_issues_in_a_text() {
        Formatter formatter = new Formatter("http://my.issue.tracker");

        List<String> issues = formatter.issuesIn("A scenario about issue #123,#456, #789");

        assertThat(issues, hasItems("#123", "#456", "#789"));
    }

    @Test
    public void should_not_format_issues_if_no_issue_manage_url_is_provided() {
        Formatter formatter = new Formatter(null);

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues #123 and #456"));
    }

}
