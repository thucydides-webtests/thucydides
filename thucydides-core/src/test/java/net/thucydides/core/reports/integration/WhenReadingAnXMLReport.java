package net.thucydides.core.reports.integration;

import com.google.common.base.Optional;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class WhenReadingAnXMLReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private XMLTestOutcomeReporter outcomeReporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        outcomeReporter = new XMLTestOutcomeReporter();

        outputDirectory = temporaryDirectory.newFolder("target/site/thucydides");

        outcomeReporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <issues>\n"
            + "    <issue>#456</issue>\n"
            + "    <issue>#789</issue>\n"
            + "    <issue>#123</issue>\n"
            + "  </issues>\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getIssues(), hasItems("#123", "#456", "#789"));
    }

    @Test
    public void should_load_tags_from_xml_file() throws Exception {
        String storedReportXML =
        "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' />\n"
            + "  <tags>\n"
            + "    <tag name='important feature' type='feature' />\n"
            + "    <tag name='simple story' type='story' />\n"
            + "  </tags>\n"
            + "  <test-step result='SUCCESS' duration='0'>\n"
            + "    <description>step 1</description>\n"
            + "  </test-step>\n"
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTags().size(), is(2));
    }

    @Test
    public void should_load_acceptance_test_report_including_issues() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTitle(), is("Should do this"));
    }

    @Test
    public void should_load_test_step_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <screenshots>"
          + "      <screenshot image='step_1.png' source='step_1.html' />"
          + "    </screenshots>"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        TestStep testStep = (TestStep) testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
        assertThat(testStep.getResult(), is(TestResult.SUCCESS));
        assertThat(testStep.getDescription(), is("step 1"));
        assertThat(testStep.getScreenshots().get(0).getScreenshotFile().getName(), is("step_1.png"));
        assertThat(testStep.getScreenshots().get(0).getSourcecode().getName(), is("step_1.html"));
    }


    @Test
    public void should_load_user_story_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getUserStory(), is(Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory", "A user story")));
    }

    @Test
    public void should_load_feature_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
          + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
          + "  </user-story>"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        testOutcome.get().getFeature();

        ApplicationFeature expectedFeature = new ApplicationFeature("myapp.myfeatures.SomeFeature", "Some feature");
        assertThat(testOutcome.get().getFeature().getId(), is("myapp.myfeatures.SomeFeature"));
        assertThat(testOutcome.get().getFeature().getName(), is("Some feature"));
    }

    @Test
    public void should_load_the_session_id_from_xml_file() throws Exception {
        String storedReportXML =
                  "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' session-id='1234'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
                + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
                + "  </user-story>"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getSessionId(), is("1234"));
    }
    
    @Test
    public void should_return_null_feature_if_no_feature_is_present() {
        TestOutcome testOutcome = new TestOutcome("aTestMethod");
        assertThat(testOutcome.getFeature(), is(nullValue()));
    }

    @Test
    public void should_load_acceptance_test_report_with_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
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

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A nested test case"));
        
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
    }

    @Test
    public void should_load_acceptance_test_report_with_simple_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
              "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-group name='Group 1' result='SUCCESS'>\n"
            + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
            + "      <test-step result='SUCCESS'>\n"
            + "        <description>step 1</description>\n"
            + "      </test-step>\n"
            + "    </test-group>\n" 
            + "  </test-group>\n" 
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A nested test case"));
        
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
    }


    @Test
    public void should_load_acceptance_test_report_with_multiple_test_steps_from_xml_file() throws Exception {
        String storedReportXML =
                  "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A simple test case"));
        assertThat(testOutcome.get().getTestSteps().size(), is(2));
        assertThat(testOutcome.get().getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.get().getTestSteps().get(0).getDescription(), is("step 1"));
        assertThat(testOutcome.get().getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome.get().getTestSteps().get(1).getDescription(), is("step 2"));
    }


}
