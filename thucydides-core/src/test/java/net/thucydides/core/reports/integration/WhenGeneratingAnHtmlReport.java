package net.thucydides.core.reports.integration;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;

import net.thucydides.core.resources.FileResources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
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

        assertThat(htmlReport.exists(), is(true));
    }

    @Test
    public void css_stylesheets_should_also_be_copied_to_the_output_directory() throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        reporter.generateReportFor(testRun);
        
        File cssDir = new File(outputDirectory, "css");
        File cssStylesheet = new File(cssDir, "core.css");
        assertThat(cssStylesheet.exists(), is(true));
    }

    @Test
    public void the_report_file_and_the_resources_should_be_together() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.generateReportFor(testRun);
        
        File report = new File(outputDirectory,"a_simple_test_case.html");
        File cssDir = new File(outputDirectory, "css");
        File cssStylesheet = new File(cssDir, "core.css");
        assertThat(cssStylesheet.exists(), is(true));
        assertThat(report.exists(), is(true));
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
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case.html"));
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
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case_qualifier.html"));

    }

    @Test
    public void spaces_in_the_qualifier_are_converted_to_underscores_for_the_report_name()  throws Exception {
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setUserStory(new UserStory("A user story","US1", "UserStory"));

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testRun.recordStep(step1);

        reporter.setQualifier("a b c");

        File xmlReport = reporter.generateReportFor(testRun);
        assertThat(xmlReport.getName(), is("a_user_story_a_simple_test_case_a_b_c.html"));

    }


    @Test
    public void the_resources_can_come_from_a_different_location_in_a_jar_file() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "alt-report-resources";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testRun);
        
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "alternative.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void the_resources_can_come_from_the_current_project() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "localresourcelist";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testRun);

        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "localsample.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void a_different_resource_location_can_be_specified_by_using_a_system_property() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        System.setProperty("thucydides.report.resources", "alt-report-resources");
        reporter.generateReportFor(testRun);
        
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "alternative.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void when_an_alternative_resource_directory_is_used_the_default_stylesheet_is_not_copied() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "alt-report-resources";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testRun);
        
        File defaultCssStylesheet = new File(new File(outputDirectory,"css"), "core.css");
        assertThat(defaultCssStylesheet.exists(), is(false));
    }
    

    @Test
    public void the_report_should_list_test_groups_as_headings_in_the_table() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case with groups");
        testRun.setMethodName("a_simple_test_case_with_groups");

        testRun.recordStep(successfulTestStepCalled("Step 0"));
        testRun.startGroup("A group");
        testRun.recordStep(successfulTestStepCalled("Step 1"));
        testRun.recordStep(successfulTestStepCalled("Step 2"));
        testRun.recordStep(successfulTestStepCalled("Step 3"));
        testRun.startGroup("Another group");
        testRun.recordStep(successfulTestStepCalled("Step 4"));
        testRun.recordStep(successfulTestStepCalled("Step 5"));
        testRun.startGroup("Yet another group");
        testRun.recordStep(ignoredTestStepCalled("Step 6"));
        testRun.endGroup();
        testRun.recordStep(failingTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testRun.recordStep(failingTestStepCalled("Step 8", new AssertionError("Oh bother!")));
        testRun.recordStep(skippedTestStepCalled("Step 9"));
        testRun.recordStep(pendingTestStepCalled("Step 10"));
        testRun.recordStep(pendingTestStepCalled("Step 11"));
        testRun.recordStep(pendingTestStepCalled("Step 12"));
        testRun.endGroup();
        testRun.recordStep(pendingTestStepCalled("Step 13"));
        testRun.recordStep(pendingTestStepCalled("Step 14"));
        testRun.endGroup();
        testRun.recordStep(skippedTestStepCalled("Step 15"));

        reporter.setOutputDirectory(new File("target/thucyidides"));
        reporter.generateReportFor(testRun);
    }
    
    @Test
    public void a_sample_report_should_be_generated_in_the_target_directory() throws Exception {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testRun.recordStep(TestStepFactory.failingTestStepCalled("step 4"));
        testRun.recordStep(TestStepFactory.skippedTestStepCalled("step 5"));
        testRun.recordStep(TestStepFactory.pendingTestStepCalled("step 6"));

        reporter.setOutputDirectory(new File("target/thucyidides"));
        reporter.generateReportFor(testRun);
    }

}
