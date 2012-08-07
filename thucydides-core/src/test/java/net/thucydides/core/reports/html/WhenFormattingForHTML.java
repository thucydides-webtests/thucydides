package net.thucydides.core.reports.html;

import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.NumericalFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenFormattingForHTML {

    @Mock
    IssueTracking issueTracking;

    @Before
    public void prepareFormatter() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_include_issue_tracking_link_using_a_shortened_url() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        Formatter formatter = new Formatter(issueTracking);
        String formattedValue = formatter.addLinks("Fixes issue #123");

        assertThat(formattedValue, is("Fixes issue <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">#123</a>"));
    }

    @Test
    public void should_include_issue_tracking_link_using_a_full_url() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        Formatter formatter = new Formatter(issueTracking);
        String formattedValue = formatter.addLinks("Fixes issue ISSUE-123");

        assertThat(formattedValue, is("Fixes issue <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">ISSUE-123</a>"));
    }

    @Test
    public void should_include_issue_tracking_link_for_both_full_and_shortened_ids() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/MYPROJECT-{0}");
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("Fixes issue #1 and MYPROJECT-2");

        assertThat(formattedValue, is("Fixes issue <a href=\"http://my.issue.tracker/MY-PROJECT/browse/MYPROJECT-1\">#1</a> and <a href=\"http://my.issue.tracker/MY-PROJECT/browse/MYPROJECT-2\">MYPROJECT-2</a>"));
    }

    @Test
    public void should_include_multiple_issue_tracking_links() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">#123</a> and <a href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-456\">#456</a>"));
    }

    @Test
    public void should_allow_letters_and_numbers_in_issue_number() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MYPROJECT-123,#MYPROJECT-456)");

        assertThat(formattedValue, is("A big story (<a href=\"http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-123\">#MYPROJECT-123</a>,<a href=\"http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-456\">#MYPROJECT-456</a>)"));
    }

    @Test
    public void should_allow_dashes_in_issue_number() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MY-PROJECT-123,#MY-PROJECT-456)");

        assertThat(formattedValue, is("A big story (<a href=\"http://my.issue.tracker/MYPROJECT/browse/MY-PROJECT-123\">#MY-PROJECT-123</a>,<a href=\"http://my.issue.tracker/MYPROJECT/browse/MY-PROJECT-456\">#MY-PROJECT-456</a>)"));
    }

    @Test
    public void should_allow_underscores_in_issue_number() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MY_PROJECT_123,#MY_PROJECT_456)");

        assertThat(formattedValue, is("A big story (<a href=\"http://my.issue.tracker/MYPROJECT/browse/MY_PROJECT_123\">#MY_PROJECT_123</a>,<a href=\"http://my.issue.tracker/MYPROJECT/browse/MY_PROJECT_456\">#MY_PROJECT_456</a>)"));
    }

    @Test
    public void should_identify_issues_in_a_text() {
        List<String> issues = Formatter.shortenedIssuesIn("A scenario about issue #123");

        assertThat(issues, hasItem("#123"));
    }

    @Test
    public void should_identify_multiple_issues_in_a_text() {
        List<String> issues = Formatter.shortenedIssuesIn("A scenario about issue #123,#456, #789");

        assertThat(issues, hasItems("#123", "#456", "#789"));
    }

    @Test
    public void should_not_format_issues_if_no_issue_manage_url_is_provided() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues #123 and #456"));
    }

    @Test
    public void should_not_format_issues_if_the_issue_manage_url_is_empty() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues #123 and #456"));
    }

    @Test
    public void should_insert_line_breaks_into_text_values() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLineBreaks("Line one\nLine two\nLine three");

        assertThat(formattedValue, is("Line one<br>Line two<br>Line three"));
    }

    @Test
    public void should_insert_line_breaks_into_text_values_with_windows_line_breaks() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter(issueTracking);

        String formattedValue = formatter.addLineBreaks("Line one\r\nLine two\r\nLine three");

        assertThat(formattedValue, is("Line one<br>Line two<br>Line three"));
    }

    @Test
    public void formatter_should_round_doubles_to_a_given_precision() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.234,1), is("1.2"));
    }

    @Test
    public void formatter_should_round_doubles_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.234,0), is("1"));
    }

    @Test
    public void formatter_should_round_doubles_up_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.634,0), is("2"));
    }

    @Test
    public void formatter_should_round_doubles_up() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.678,1), is("1.7"));
    }

    @Test
    public void formatter_should_drop_training_zeros() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.0,2), is("1"));
    }

    @Test
    public void formatter_should_round_percentages_to_a_given_precision() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.1234,1), is("12.3%"));
    }

    @Test
    public void formatter_should_round_percentages_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.1234,0), is("12%"));
    }

    @Test
    public void formatter_should_round_percentages_up_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.1254,0), is("13%"));
    }

    @Test
    public void formatter_should_round_percentages_up() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.16789, 1), is("16.8%"));
    }

    @Test
    public void formatter_should_drop_training_zeros_for_percentages() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.5, 1), is("50%"));
    }
}
