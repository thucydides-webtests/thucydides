package net.thucydides.core.reports.integration;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingAnXMLReport {

    private AcceptanceTestReporter reporter;

    @Rule 
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();
    
    private File outputDirectory;
    
    @Before
    public void setupTestReporter() {
        reporter = new XMLAcceptanceTestReporter();
        outputDirectory = temporaryDirectory.newFolder("temp");
        reporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }
    
    @Test
    public void should_include_the_requirements_if_present()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1'"
        + " failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
        + "  <requirements>\n"
        + "    <requirement>ABC</requirement>\n"
        + "  </requirements>\n"
        + "  <test-step result='SUCCESS'>\n"
        + "    <description>step 1</description>\n"
        + "  </test-step>\n" 
        + "</acceptance-test-run>";

        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.testsRequirement("ABC");
        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    

    @Test
    public void should_cater_for_multiple_requirements_if_present()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1'"
        + " failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
        + "  <requirements>\n"
        + "    <requirement>ABC</requirement>\n"
        + "    <requirement>DEF</requirement>\n"
        + "  </requirements>\n"
        + "  <test-step result='SUCCESS'>\n"
        + "    <description>step 1</description>\n"
        + "  </test-step>\n" + "</acceptance-test-run>";

        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.testsRequirement("ABC");
        testRun.testsRequirement("DEF");
        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }      
    
    @Test
    public void should_allow_requirements_in_steps() throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1'"
        + " failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
        + "  <test-step result='SUCCESS'>\n"
        + "    <requirements>\n"
        + "      <requirement>ABC</requirement>\n"
        + "      <requirement>DEF</requirement>\n"
        + "    </requirements>\n"
        + "    <description>step 1</description>\n"
        + "  </test-step>\n" + "</acceptance-test-run>";

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.testsRequirement("ABC");
        step1.testsRequirement("DEF");
        testRun.recordStep(step1);
        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }          
    @Test
    public void should_generate_an_XML_report_with_a_name_based_on_the_test_run_title()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        File xmlReport = reporter.generateReportFor(testRun);

        assertThat(xmlReport.getName(), is("a_simple_test_case.xml"));
    }

    @Test
    public void should_generate_an_XML_report_in_the_target_directory() throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");

        File xmlReport = reporter.generateReportFor(testRun);

        assertThat(xmlReport.getPath(), startsWith(outputDirectory.getPath()));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='9' successful='2' failures='3' skipped='1' ignored='2' pending='1' result='FAILURE'>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='IGNORED'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='IGNORED'>\n"
                + "    <description>step 3</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 4</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 5</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 6</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 7</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='SKIPPED'>\n"
                + "    <description>step 8</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='PENDING'>\n"
                + "    <description>step 9</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

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
    public void should_record_test_groups_as_nested_structures()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A nested test case");
        String expectedReport = 
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='3' successful='3' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-group name='Group 1'>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 1</description>\n"
                + "    </test-step>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 2</description>\n"
                + "    </test-step>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 3</description>\n"
                + "    </test-step>\n"
                + "  </test-group>\n" 
                + "</acceptance-test-run>";

        testRun.startGroup("Group 1");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testRun.endGroup();

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    
    
    @Test
    public void should_record_nested_test_groups_as_nested_structures()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A nested test case");
        String expectedReport = 
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='5' successful='5' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-group name='Group 1'>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 1</description>\n"
                + "    </test-step>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 2</description>\n"
                + "    </test-step>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 3</description>\n"
                + "    </test-step>\n"
                + "    <test-group name='Group 1.1'>\n"
                + "      <test-step result='SUCCESS'>\n"
                + "        <description>step 4</description>\n"
                + "      </test-step>\n"
                + "      <test-step result='SUCCESS'>\n"
                + "        <description>step 5</description>\n"
                + "      </test-step>\n"
                + "    </test-group>\n" 
                + "  </test-group>\n" 
                + "</acceptance-test-run>";

        testRun.startGroup("Group 1");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testRun.startGroup("Group 1.1");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 5"));
        testRun.endGroup();
        testRun.endGroup();

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    
    
    @Test
    public void should_record_minimal_nested_test_groups_as_nested_structures()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A nested test case");
        String expectedReport = 
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <test-group name='Group 1'>\n"
                + "    <test-group name='Group 1.1'>\n"
                + "      <test-group name='Group 1.1.1'>\n"
                + "        <test-step result='SUCCESS'>\n"
                + "          <description>step 1</description>\n"
                + "        </test-step>\n"
                + "      </test-group>\n" 
                + "    </test-group>\n" 
                + "  </test-group>\n" 
                + "</acceptance-test-run>";

        testRun.startGroup("Group 1");
        testRun.startGroup("Group 1.1");
        testRun.startGroup("Group 1.1.1");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.endGroup();
        testRun.endGroup();
        testRun.endGroup();

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    
    

    @Test
    public void should_include_the_name_of_any_screenshots_where_present()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE'>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "    <screenshot>step_1.png</screenshot>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        File screenshot = temporaryDirectory.newFile("step_1.png");

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.setScreenshot(screenshot);
        testRun.recordStep(step1);
        testRun.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }
    
    @Test
    public void should_include_the_details_of_the_user_story_if_present()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        String expectedReport = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story name='A user story' code='US1' source='UserStory'/>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "    <screenshot>step_1.png</screenshot>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        testRun.setUserStory(new UserStory("A user story","US1", "UserStory"));
        
        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testRun.recordStep(step1);

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }



    @Test
    public void should_have_a_meaningful_filename()  throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setUserStory(new UserStory("A user story","US1", "UserStory"));

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testRun.recordStep(step1);

        File xmlReport = reporter.generateReportFor(testRun);
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case.xml"));
    }

    @Test
    public void should_have_a_qualified_filename_if_qualifier_present()  throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setUserStory(new UserStory("A user story","US1", "UserStory"));

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testRun.recordStep(step1);

        reporter.setQualifier("qualifier");

        File xmlReport = reporter.generateReportFor(testRun);
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case_qualifier.xml"));

    }

    @Test
    public void should_include_error_message_for_failing_test()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");

        ConcreteTestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith("Oh nose!", new IllegalArgumentException());

        testRun.recordStep(step);

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<error>Oh nose!</error>"));
    }
    
    @Test
    public void should_include_exception_stack_dump_for_failing_test()
            throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");

        ConcreteTestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith("Oh nose!", new IllegalArgumentException());

        testRun.recordStep(step);

        File xmlReport = reporter.generateReportFor(testRun);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<exception>java.lang.IllegalArgumentException"));
    }
    
    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
