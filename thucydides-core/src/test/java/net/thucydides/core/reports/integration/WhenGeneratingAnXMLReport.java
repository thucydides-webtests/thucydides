package net.thucydides.core.reports.integration;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class WhenGeneratingAnXMLReport {

    private AcceptanceTestReporter reporter;

    @Rule 
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();
    
    private File outputDirectory;
    
    @Before
    public void setupTestReporter() {
        reporter = new XMLTestOutcomeReporter();
        outputDirectory = temporaryDirectory.newFolder("temp");
        reporter.setOutputDirectory(outputDirectory);
    }

    class AUserStory {};

    @Story(AUserStory.class)
    class SomeTestScenario {
        public void a_simple_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Feature
    class AFeature {
        class AUserStoryInAFeature {};
    }

    @Story(AFeature.AUserStoryInAFeature.class)
    class SomeTestScenarioInAFeature {
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        String expectedReport =
              "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-step result='SUCCESS'>\n"
            + "    <description>step 1</description>\n"
            + "  </test-step>\n"
            + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void the_xml_report_should_contain_the_feature_if_provided()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        String expectedReport =
              "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature'>\n"
            + "    <feature id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature' name='A feature'/>\n"
            + "  </user-story>\n"
            + "  <test-step result='SUCCESS'>\n"
            + "    <description>step 1</description>\n"
            + "  </test-step>\n"
            + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }


    @Test
    public void should_generate_a_qualified_XML_report_for_an_acceptance_test_run_if_the_qualifier_is_specified()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        String expectedReport = "<acceptance-test-run title='A simple test case [qualifier]' name='a_simple_test_case_qualifier' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("qualifier");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }
    
    @Test
    public void should_generate_a_qualified_XML_report_with_formatted_parameters_if_the_qualifier_is_specified()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String expectedReport = "<acceptance-test-run title='A simple test case [a/b]' name='a_simple_test_case_a_b' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("a_b");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }


    @Test
    public void should_include_the_requirements_if_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1'"
        + " failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
        + "  <requirements>\n"
        + "    <requirement>ABC</requirement>\n"
        + "  </requirements>\n"
        + "  <test-step result='SUCCESS'>\n"
        + "    <description>step 1</description>\n"
        + "  </test-step>\n" 
        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.testsRequirement("ABC");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    

    @Test
    public void should_cater_for_multiple_requirements_if_present()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1'"
        + " failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
        + "  <requirements>\n"
        + "    <requirement>ABC</requirement>\n"
        + "    <requirement>DEF</requirement>\n"
        + "  </requirements>\n"
        + "  <test-step result='SUCCESS'>\n"
        + "    <description>step 1</description>\n"
        + "  </test-step>\n" + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.testsRequirement("ABC");
        testOutcome.testsRequirement("DEF");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }      
    
    @Test
    public void should_allow_requirements_in_steps() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        
        String expectedReport = 
        "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1'"
        + " failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
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
        testOutcome.recordStep(step1);
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }          
    @Test
    public void should_generate_an_XML_report_with_a_name_based_on_the_test_run_title()
            throws Exception {
        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        File xmlReport = reporter.generateReportFor(testOutcome);

        assertThat(xmlReport.getName(), is("a_simple_test_case.xml"));
    }

    @Test
    public void should_generate_an_XML_report_in_the_target_directory() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        File xmlReport = reporter.generateReportFor(testOutcome);

        assertThat(xmlReport.getPath(), startsWith(outputDirectory.getPath()));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String expectedReport = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='9' successful='2' failures='3' skipped='1' ignored='2' pending='1' result='FAILURE'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
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

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 3"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 5"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 6"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 7"));
        testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
        testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }


    @Story(AUserStory.class)
    class SomeNestedTestScenario {
        public void a_nested_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Test
    public void should_record_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport =
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='3' successful='3' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-group name='Group 1' result='SUCCESS'>\n"
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

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    
    
    @Test
    public void should_record_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport = 
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='5' successful='5' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-group name='Group 1' result='SUCCESS'>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 1</description>\n"
                + "    </test-step>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 2</description>\n"
                + "    </test-step>\n"
                + "    <test-step result='SUCCESS'>\n"
                + "      <description>step 3</description>\n"
                + "    </test-step>\n"
                + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                + "      <test-step result='SUCCESS'>\n"
                + "        <description>step 4</description>\n"
                + "      </test-step>\n"
                + "      <test-step result='SUCCESS'>\n"
                + "        <description>step 5</description>\n"
                + "      </test-step>\n"
                + "    </test-group>\n" 
                + "  </test-group>\n" 
                + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.startGroup("Group 1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 5"));
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    
    
    @Test
    public void should_record_minimal_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport = 
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-group name='Group 1' result='SUCCESS'>\n"
                + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                + "        <test-step result='SUCCESS'>\n"
                + "          <description>step 1</description>\n"
                + "        </test-step>\n"
                + "      </test-group>\n" 
                + "    </test-group>\n" 
                + "  </test-group>\n" 
                + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }    
    
    @Test
    public void should_record_minimal_nested_test_steps_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport =
                  "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-group name='Group 1' result='SUCCESS'>\n"
                + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                + "        <test-step result='SUCCESS'>\n"
                + "          <description>step 1</description>\n"
                + "        </test-step>\n"
                + "      </test-group>\n"
                + "    </test-group>\n"
                + "  </test-group>\n"
                + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_include_the_name_of_any_screenshots_where_present()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String expectedReport = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS' screenshot='step_1.png'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        File screenshot = temporaryDirectory.newFile("step_1.png");

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }


    @Test
    public void should_have_a_meaningful_filename()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case.xml"));
    }

    @Test
    public void should_have_a_qualified_filename_if_qualifier_present()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.setQualifier("qualifier");

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case_qualifier.xml"));

    }


    @Test
    public void spaces_in_the_qualifer_should_be_converted_to_underscores_in_the_test_run_name()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.setQualifier("a b c");

        File xmlReport = reporter.generateReportFor(testOutcome);

        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("name=\"a_simple_test_case_a_b_c\""));



    }

    @Test
    public void should_include_error_message_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        ConcreteTestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith("Oh nose!", new IllegalArgumentException());

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<error>Oh nose!</error>"));
    }
    
    @Test
    public void should_include_exception_stack_dump_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        ConcreteTestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith("Oh nose!", new IllegalArgumentException());

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<exception>java.lang.IllegalArgumentException"));
    }
    
    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
