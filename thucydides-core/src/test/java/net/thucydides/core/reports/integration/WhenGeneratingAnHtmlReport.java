package net.thucydides.core.reports.integration;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingAnHtmlReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private AcceptanceTestReporter reporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        reporter = new HtmlAcceptanceTestReporter();

        outputDirectory = temporaryDirectory.newFolder("target/thucydides");

        reporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_generate_an_HTML_report_for_an_acceptance_test_run() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File htmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(htmlReport);

        assertThat(htmlReport.exists(), is(true));
        System.out.println(generatedReportText);
    }

    @Test
    public void css_stylesheets_should_also_be_copied_to_the_output_directory() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.generateReportFor(testRun);
        
        File cssStylesheet = new File(outputDirectory,"default.css");
        assertThat(cssStylesheet.exists(), is(true));

    }

    @Test
    public void the_report_file_and_the_resources_should_be_together() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.generateReportFor(testRun);
        
        File report = new File(outputDirectory,"a_simple_test_case.html");
        File cssStylesheet = new File(outputDirectory,"default.css");
        assertThat(cssStylesheet.exists(), is(true));
        assertThat(report.exists(), is(true));

    }
    @Test
    public void resources_should_also_be_copied_from_the_classpath_to_the_output_directory() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setResourceDirectory("scripts.selenium");
        reporter.generateReportFor(testRun);

        File jsScriptOnClasspath = new File(outputDirectory,"findElement.js");
        assertThat(jsScriptOnClasspath.exists(), is(true));

    }
    
    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
