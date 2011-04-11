package net.thucydides.core.reports;

import net.thucydides.core.model.AcceptanceTestRun;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;

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
}
