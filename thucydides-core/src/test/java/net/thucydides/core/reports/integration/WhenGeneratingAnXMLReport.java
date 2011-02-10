package net.thucydides.core.reports.integration;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingAnXMLReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();
    
    private AcceptanceTestReporter reporter;
    
    @Before
    public void setupTestReporter() {
        reporter = new XMLAcceptanceTestReporter();        
        reporter.setOutputDirectory(temporaryDirectory.newFolder("thucydides"));
    }
    
    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run() throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = "<acceptance-test-run title='A simple test case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n" +
                                "  <test-step result='SUCCESS'>\n" +
                                "    <description>step 1</description>\n" +
                                "  </test-step>\n" +
                                "</acceptance-test-run>";
      
      
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testRun);        
        String generatedReportText = getStringFrom(xmlReport);
        
        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run() throws Exception {
        AcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();        
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' steps='9' successful='2' failures='3' skipped='1' ignored='2' pending='1' result='FAILURE'>\n" +
        "  <test-step result='SUCCESS'>\n" +
        "    <description>step 1</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='IGNORED'>\n" +
        "    <description>step 2</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='IGNORED'>\n" +
        "    <description>step 3</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='SUCCESS'>\n" +
        "    <description>step 4</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='FAILURE'>\n" +
        "    <description>step 5</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='FAILURE'>\n" +
        "    <description>step 6</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='FAILURE'>\n" +
        "    <description>step 7</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='SKIPPED'>\n" +
        "    <description>step 8</description>\n" +
        "  </test-step>\n" +
        "  <test-step result='PENDING'>\n" +
        "    <description>step 9</description>\n" +
        "  </test-step>\n" +
        "</acceptance-test-run>";
      
      
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.recordStep(TestStepFactory.ignoredTestStepCalled("step 2"));
        testRun.recordStep(TestStepFactory.ignoredTestStepCalled("step 3"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testRun.recordStep(TestStepFactory.failingTestStepCalled("step 5"));
        testRun.recordStep(TestStepFactory.failingTestStepCalled("step 6"));
        testRun.recordStep(TestStepFactory.failingTestStepCalled("step 7"));
        testRun.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
        testRun.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

        File xmlReport = reporter.generateReportFor(testRun);        
        String generatedReportText = getStringFrom(xmlReport);
        
        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }
        
    @Test
    public void should_include_the_name_of_any_screenshots_where_present() throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = "<acceptance-test-run title='A simple test case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE'>\n" +
                                "  <test-step result='SUCCESS'>\n" +
                                "    <description>step 1</description>\n" +
                                "    <screenshot>step_1.png</screenshot>\n" +
                                "  </test-step>\n" +
                                "  <test-step result='FAILURE'>\n" +
                                "    <description>step 2</description>\n" +
                                "  </test-step>\n" +
                                "</acceptance-test-run>";
      
        File screenshot = temporaryDirectory.newFile("step_1.png");
        
        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.setScreenshot(screenshot);
        testRun.recordStep(step1);
        testRun.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testRun);        
        String generatedReportText = getStringFrom(xmlReport);
        
        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }
    
    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }
   

    
}
