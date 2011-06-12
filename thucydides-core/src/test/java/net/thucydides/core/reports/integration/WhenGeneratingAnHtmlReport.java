package net.thucydides.core.reports.integration;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.TestsStory;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenGeneratingAnHtmlReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private AcceptanceTestReporter reporter;

    private File outputDirectory;

    // TODO: check the output more throroughly
    class AUserStory {};

    @TestsStory(AUserStory.class)
    class SomeTestScenario {
        public void a_simple_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Feature
    class AFeature {
        class AUserStoryInAFeature {};
    }

    @TestsStory(AFeature.AUserStoryInAFeature.class)
    class SomeTestScenarioInAFeature {
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Before
    public void setupTestReporter() {
        reporter = new HtmlAcceptanceTestReporter();
        outputDirectory = temporaryDirectory.newFolder("target/thucydides");
        reporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_generate_an_HTML_report_for_an_acceptance_test_run() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File htmlReport = reporter.generateReportFor(testOutcome);

        assertThat(htmlReport.exists(), is(true));
    }

    @Test
    public void css_stylesheets_should_also_be_copied_to_the_output_directory() throws Exception {
        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        reporter.generateReportFor(testOutcome);
        
        File cssDir = new File(outputDirectory, "css");
        File cssStylesheet = new File(cssDir, "core.css");
        assertThat(cssStylesheet.exists(), is(true));
    }

    @Test
    public void the_report_file_and_the_resources_should_be_together() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.generateReportFor(testOutcome);
        
        File report = new File(outputDirectory,"a_simple_test_case.html");
        File cssDir = new File(outputDirectory, "css");
        File cssStylesheet = new File(cssDir, "core.css");
        assertThat(cssStylesheet.exists(), is(true));
        assertThat(report.exists(), is(true));
    }
    
    @Test
    public void should_have_a_meaningful_filename()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_should_do_this.html"));
    }

    @Test
    public void should_have_a_qualified_filename_if_qualifier_present()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.setQualifier("qualifier");

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_should_do_this_qualifier.html"));

    }

    @Test
    public void spaces_in_the_qualifier_are_converted_to_underscores_for_the_report_name()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        ConcreteTestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.setQualifier("a b c");

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_should_do_this_a_b_c.html"));

    }


    @Test
    public void the_resources_can_come_from_a_different_location_in_a_jar_file() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "alt-report-resources";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testOutcome);
        
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "alternative.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void the_resources_can_come_from_the_current_project() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "localresourcelist";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testOutcome);

        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "localsample.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void a_different_resource_location_can_be_specified_by_using_a_system_property() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        System.setProperty("thucydides.report.resources", "alt-report-resources");
        reporter.generateReportFor(testOutcome);
        
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "alternative.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void when_an_alternative_resource_directory_is_used_the_default_stylesheet_is_not_copied() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "alt-report-resources";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testOutcome);
        
        File defaultCssStylesheet = new File(new File(outputDirectory,"css"), "core.css");
        assertThat(defaultCssStylesheet.exists(), is(false));
    }
    

    @Test
    public void the_report_should_list_test_groups_as_headings_in_the_table() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case with groups");
        testOutcome.setMethodName("a_simple_test_case_with_groups");

        testOutcome.recordStep(successfulTestStepCalled("Step 0"));
        testOutcome.startGroup("A group");
        testOutcome.recordStep(successfulTestStepCalled("Step 1"));
        testOutcome.recordStep(successfulTestStepCalled("Step 2"));
        testOutcome.recordStep(successfulTestStepCalled("Step 3"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(successfulTestStepCalled("Step 4"));
        testOutcome.recordStep(successfulTestStepCalled("Step 5"));
        testOutcome.startGroup("Yet another group");
        testOutcome.recordStep(ignoredTestStepCalled("Step 6"));
        testOutcome.endGroup();
        testOutcome.recordStep(failingTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testOutcome.recordStep(failingTestStepCalled("Step 8", new AssertionError("Oh bother!")));
        testOutcome.recordStep(skippedTestStepCalled("Step 9"));
        testOutcome.recordStep(pendingTestStepCalled("Step 10"));
        testOutcome.recordStep(pendingTestStepCalled("Step 11"));
        testOutcome.recordStep(pendingTestStepCalled("Step 12"));
        testOutcome.endGroup();
        testOutcome.recordStep(pendingTestStepCalled("Step 13"));
        testOutcome.recordStep(pendingTestStepCalled("Step 14"));
        testOutcome.endGroup();
        testOutcome.recordStep(skippedTestStepCalled("Step 15"));

        reporter.setOutputDirectory(new File("target/thucyidides"));
        reporter.generateReportFor(testOutcome);
    }
    
    @Test
    public void a_sample_report_should_be_generated_in_the_target_directory() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.setMethodName("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 5"));
        testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 6"));

        reporter.setOutputDirectory(new File("target/thucyidides"));
        reporter.generateReportFor(testOutcome);
    }

}
