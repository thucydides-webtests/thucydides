package net.thucydides.core.reports.integration;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenReadingAnXMLReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private XMLAcceptanceTestReporter reporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        reporter = new XMLAcceptanceTestReporter();

        outputDirectory = temporaryDirectory.newFolder("target/thucydides");

        reporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "    <screenshot>step_1.png</screenshot>\n"
                + "  </test-step>\n" 
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        AcceptanceTestRun testRun = reporter.loadReportFrom(report);

        assertThat(testRun.getTitle(), is("A simple test case"));
        assertThat(testRun.getTestSteps().size(), is(1));
        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testRun.getTestSteps().get(0).getDescription(), is("step 1"));
        assertThat(testRun.getTestSteps().get(0).getScreenshotPath(), is("step_1.png"));
    }

    @Test
    public void should_load_acceptance_test_report_with_multiple_test_steps_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        AcceptanceTestRun testRun = reporter.loadReportFrom(report);

        assertThat(testRun.getTitle(), is("A simple test case"));
        assertThat(testRun.getTestSteps().size(), is(2));
        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testRun.getTestSteps().get(0).getDescription(), is("step 1"));
        assertThat(testRun.getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testRun.getTestSteps().get(1).getDescription(), is("step 2"));
    }

    @Test
    public void should_load_acceptance_test_report_with_top_level_requirement_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <requirements>\n"
                + "    <requirement>12</requirement>\n"
                + "  </requirements>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        AcceptanceTestRun testRun = reporter.loadReportFrom(report);

        assertThat(testRun.getTitle(), is("A simple test case"));
        assertThat(testRun.getTestedRequirements().size(), is(1));
        assertThat(testRun.getTestedRequirements(), hasItem("12"));
        assertThat(testRun.getTestSteps().size(), is(1));
        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testRun.getTestSteps().get(0).getDescription(), is("step 1"));
    }

    @Test
    public void should_load_acceptance_test_report_with_top_level_requirements_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <requirements>\n"
                + "    <requirement>12</requirement>\n"
                + "    <requirement>32</requirement>\n"
                + "  </requirements>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        AcceptanceTestRun testRun = reporter.loadReportFrom(report);

        assertThat(testRun.getTitle(), is("A simple test case"));
        assertThat(testRun.getTestedRequirements().size(), is(2));
        assertThat(testRun.getTestedRequirements(), hasItem("12"));
        assertThat(testRun.getTestedRequirements(), hasItem("32"));
        assertThat(testRun.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testRun.getTestSteps().get(0).getDescription(), is("step 1"));
    }

    @Test
    public void should_load_acceptance_test_report_with_step_level_requirements_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <requirements>\n"
                + "      <requirement>12</requirement>\n"
                + "      <requirement>32</requirement>\n"
                + "    </requirements>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        AcceptanceTestRun testRun = reporter.loadReportFrom(report);

        assertThat(testRun.getTestSteps().get(0).getTestedRequirements().size(), is(2));
        assertThat(testRun.getTestSteps().get(0).getTestedRequirements(), hasItem("12"));
        assertThat(testRun.getTestSteps().get(0).getTestedRequirements(), hasItem("32"));
    }
    
    @Test
    public void should_load_a_set_of_all_acceptance_tests_in_a_given_directory() throws IOException {
        File reportsDirectory = temporaryDirectory.newFolder("stored-reports");
        saveSomeXMLReportsIn(reportsDirectory);
        
        List<AcceptanceTestRun> testRuns = reporter.loadAllReportsFrom(reportsDirectory);
        assertThat(testRuns.size(), is(3));
    }
    
    
    private void saveSomeXMLReportsIn(File outputDirectory) throws IOException {
        String storedReportXML1 = "<acceptance-test-run title='A simple test case 1' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <test-step result='SUCCESS'>\n"
            + "    <description>step 1</description>\n"
            + "    <screenshot>step_1.png</screenshot>\n"
            + "  </test-step>\n" 
            + "</acceptance-test-run>";        
        FileUtils.writeStringToFile(new File(outputDirectory,"report1.xml"), storedReportXML1);

        String storedReportXML2 = "<acceptance-test-run title='A simple test case 2' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <test-step result='SUCCESS'>\n"
            + "    <description>step 1</description>\n"
            + "    <screenshot>step_1.png</screenshot>\n"
            + "  </test-step>\n" 
            + "</acceptance-test-run>";        
        FileUtils.writeStringToFile(new File(outputDirectory,"report2.xml"), storedReportXML2);
    
        String storedReportXML3 = "<acceptance-test-run title='A simple test case 3' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <test-step result='SUCCESS'>\n"
            + "    <description>step 1</description>\n"
            + "    <screenshot>step_1.png</screenshot>\n"
            + "  </test-step>\n" 
            + "</acceptance-test-run>";        
        FileUtils.writeStringToFile(new File(outputDirectory,"report3.xml"), storedReportXML3);
    }
}
