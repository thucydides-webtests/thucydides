package net.thucydides.core.reports.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
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
    public void should_load_acceptance_test_report_with_user_story_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story name='A user story' code='US1' />\n"
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

        assertThat(testRun.getUserStory(), is(notNullValue()));
    }

}
