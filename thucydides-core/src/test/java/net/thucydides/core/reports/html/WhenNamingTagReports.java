package net.thucydides.core.reports.html;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenNamingTagReports {

    @Test
    public void a_tag_should_give_a_simple_report_name() {
        ReportNameProvider reportName = new ReportNameProvider();

        assertThat(reportName.forTag("some tag"), is("tag_some_tag.html"));
    }

    @Test
    public void a_tag_type_should_give_a_simple_report_name() {
        ReportNameProvider reportName = new ReportNameProvider();

        assertThat(reportName.forTagType("some tag type"), is("tagtype_some_tag_type.html"));
    }

    @Test
    public void a_tag_inside_a_tag_type_should_give_a_composite_report_name() {
        ReportNameProvider reportName = new ReportNameProvider("some tag type");

        assertThat(reportName.forTag("some tag"), is("context_some_tag_type_tag_some_tag.html"));
    }


}

