package net.thucydides.core.reports.html;

import net.thucydides.core.model.NumericalFormatter;
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
