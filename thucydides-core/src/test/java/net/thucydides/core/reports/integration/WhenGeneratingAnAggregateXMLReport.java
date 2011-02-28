package net.thucydides.core.reports.integration;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.reports.xml.XMLAggregateTestReporter;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingAnAggregateXMLReport {

    private XMLAggregateTestReporter reporter;

    @Rule 
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();
    
    private File outputDirectory;
    
    private File sourceDirectory;
    
    @Before
    public void setupTestReporter() {
        reporter = new XMLAggregateTestReporter();
        sourceDirectory = new File("src/test/resources/reports");
        outputDirectory = temporaryDirectory.newFolder("temp");
        reporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_test_runs_from_xml_files_in_a_directory()
            throws Exception {
        
        AggregateTestResults aggregateTestResults = reporter.loadAllReportsFrom(sourceDirectory);
        
        assertThat(aggregateTestResults.getTestRuns().size(), is(3));
    }
    
    @Test
    public void should_write_aggregate_report_to_output_directory()
            throws Exception {
        
        File sourceDirectory = new File("src/test/resources/reports");

        AggregateTestResults aggregateTestResults = reporter.loadAllReportsFrom(sourceDirectory);
        reporter.setSourceDirectory(sourceDirectory);
        File aggregateReport = reporter.generateReportFor(aggregateTestResults);
        assertThat(aggregateReport.exists(), is(true));
    }


    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
