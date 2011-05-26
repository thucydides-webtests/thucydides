package net.thucydides.core.reports;

import net.thucydides.core.model.AcceptanceTestRun;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenUsingAReportService {

    @Mock
    File outputDirectory;

    @Mock
    AcceptanceTestReporter reporter;

    @Mock
    AcceptanceTestRun acceptanceTestRun;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void a_report_service_should_generate_reports_for_each_subscribed_reporter() throws Exception {
        List<AcceptanceTestRun> testRunResults = new ArrayList<AcceptanceTestRun>();
        testRunResults.add(acceptanceTestRun);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testRunResults);

        verify(reporter).generateReportFor(acceptanceTestRun);
    }


    @Test
    public void a_report_service_uses_the_provided_output_directory_for_all_reports() throws Exception {
        List<AcceptanceTestRun> testRunResults = new ArrayList<AcceptanceTestRun>();
        testRunResults.add(acceptanceTestRun);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testRunResults);

        verify(reporter).setOutputDirectory(outputDirectory);
    }

    @Test(expected = ReportGenerationFailedError.class)
    public void a_report_service_should_raise_an_error_if_report_generation_fails() throws Exception {
        List<AcceptanceTestRun> testRunResults = new ArrayList<AcceptanceTestRun>();
        testRunResults.add(acceptanceTestRun);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        when(reporter.generateReportFor(acceptanceTestRun)).thenThrow(new IOException());
        reportService.subscribe(reporter);

        reportService.generateReportsFor(testRunResults);

        verify(reporter).setOutputDirectory(outputDirectory);
    }

    @Test
    public void default_reporters_should_include_xml_and_html() {
        List reporters = ReportService.getDefaultReporters();
        assertThat(reporters.size(), is(2));

        Matcher calledXml = hasProperty("name", is("xml"));
        Matcher calledHtml = hasProperty("name", is("html"));
        assertThat(reporters, allOf(hasItem(calledXml), hasItem(calledHtml)));
    }

    @Test
    public void new_reporters_should_be_instantiated_at_each_request() {
        List reporters = ReportService.getDefaultReporters();
        List reporters2 = ReportService.getDefaultReporters();
        assertThat(reporters, is(not(reporters2)));
    }

}
