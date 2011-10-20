package net.thucydides.core.reports.integration;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.skippedTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenGeneratingAnHtmlReport extends AbstractReportGenerationTest {

    @Before
    public void setupWorkingDirectory() throws IOException {
        File screenshotsSourceDirectory = new File(Thread.currentThread().getContextClassLoader().getResource("screenshots").getPath());
        File[] screenshots = screenshotsSourceDirectory.listFiles();

        for(File screenshot : screenshots) {
            FileUtils.copyFileToDirectory(screenshot, outputDirectory);
        }
    }

    @Test
    public void should_generate_an_HTML_report_for_an_acceptance_test_run() throws Exception {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File htmlReport = reporter.generateReportFor(testOutcome);

        assertThat(htmlReport.exists(), is(true));
    }

    @Test
    public void should_generate_an_HTML_report_for_an_acceptance_test_run_with_spaces_in_the_test_name() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File htmlReport = reporter.generateReportFor(testOutcome);

        assertThat(htmlReport.getName(), is("a_simple_test_case.html"));
    }

    @Test
    public void should_generate_an_HTML_report_for_an_acceptance_test_run_with_an_issue_number_in_the_test_name() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case (#ISSUE-123)");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File htmlReport = reporter.generateReportFor(testOutcome);

        assertThat(htmlReport.getName(), is("a_simple_test_case.html"));
    }

    @Test
    public void should_generate_an_HTML_report_for_an_acceptance_test_run_with_issue_numbers_in_the_test_name() throws Exception {

        TestOutcome testOutcome = new TestOutcome("A simple test case (#ISSUE-123, #ISSUE-145)");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File htmlReport = reporter.generateReportFor(testOutcome);

        assertThat(htmlReport.getName(), is("a_simple_test_case.html"));
    }

    @Test
    public void css_stylesheets_should_also_be_copied_to_the_output_directory() throws Exception {
        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        reporter.generateReportFor(testOutcome);
        
        File cssDir = new File(outputDirectory, "css");
        File cssStylesheet = new File(cssDir, "core.css");
        assertThat(cssStylesheet.exists(), is(true));
    }

    @Test
    public void the_report_file_and_the_resources_should_be_together() throws Exception {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
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

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("google_page_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_should_do_this.html"));
    }

    @Test
    public void screenshots_should_have_a_separate_html_report()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("google_page_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.generateReportFor(testOutcome);

        File screenshotReport = new File(outputDirectory, "a_user_story_should_do_this_screenshots.html");
        assertThat(screenshotReport.exists(), is(true));

    }

    @Test
    public void the_screenshots_report_should_contain_a_link_to_each_screenshot_image()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("search_for_cats", SomeTestScenario.class);

        recordStepWithScreenshot(testOutcome, "Search cats on Google", "google_page_1.png");
        recordStepWithScreenshot(testOutcome, "View the results", "google_page_2.png");
        recordStepWithScreenshot(testOutcome, "Display a resulting page", "google_page_3.png");

        reporter.generateReportFor(testOutcome);

        File screenshotReport = new File(outputDirectory, "a_user_story_search_for_cats_screenshots.html");
        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, allOf(containsString("src=\"scaled_google_page_1.png\""),
                                        containsString("src=\"scaled_google_page_2.png\""),
                                        containsString("src=\"scaled_google_page_3.png\"")));
    }

    @Test
    public void the_screenshots_report_should_contain_captions_with_the_step_descriptions()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        recordStepWithScreenshot(testOutcome, "Search cats on Google", "google_page_1.png");
        recordStepWithScreenshot(testOutcome, "View the results", "google_page_2.png");
        recordStepWithScreenshot(testOutcome, "Display a resulting page", "google_page_3.png");

        reporter.generateReportFor(testOutcome);

        File screenshotReport = new File(outputDirectory, "a_user_story_should_do_this_screenshots.html");
        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, allOf(containsString("title=\"Search cats on Google\""),
                containsString("title=\"View the results\""),
                containsString("title=\"Display a resulting page\"")));
    }

    @Test
    public void the_screenshots_report_should_contain_the_overall_test_description()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        recordStepWithScreenshot(testOutcome, "Search cats on Google", "google_page_1.png");
        recordStepWithScreenshot(testOutcome, "View the results", "google_page_2.png");
        recordStepWithScreenshot(testOutcome, "Display a resulting page", "google_page_3.png");

        reporter.generateReportFor(testOutcome);

        File screenshotReport = new File(outputDirectory, "a_user_story_should_do_this_screenshots.html");
        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("Should do this"));
    }

    @Test
    public void the_screenshots_report_should_contain_a_link_back_to_the_test_results()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        recordStepWithScreenshot(testOutcome, "Search cats on Google", "google_page_1.png");
        recordStepWithScreenshot(testOutcome, "View the results", "google_page_2.png");
        recordStepWithScreenshot(testOutcome, "Display a resulting page", "google_page_3.png");

        reporter.generateReportFor(testOutcome);

        File screenshotReport = new File(outputDirectory, "a_user_story_should_do_this_screenshots.html");
        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("<a href=\"a_user_story_should_do_this.html\""));
    }

    @Test
    public void screenshot_html_should_mention_the_step_name()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("google_page_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.generateReportFor(testOutcome);

        File screenshotReport = new File(outputDirectory, "a_user_story_should_do_this_screenshots.html");
        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("step 1"));

    }


    @Test
    public void report_html_should_contain_a_link_to_the_screenshots_report()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("google_page_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.generateReportFor(testOutcome);

        File testReport = new File(outputDirectory, "a_user_story_should_do_this.html");
        String reportContents = FileUtils.readFileToString(testReport);
        assertThat(reportContents, containsString("<a href=\"a_user_story_should_do_this_screenshots.html\">"));
    }


    @Test
    public void should_have_a_qualified_filename_if_qualifier_present()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("google_page_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.setQualifier("qualifier");

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_should_do_this_qualifier.html"));

    }

    @Test
    public void spaces_in_the_qualifier_are_converted_to_underscores_for_the_report_name()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("google_page_1.png");
        step1.setScreenshot(screenshot);
        testOutcome.recordStep(step1);

        reporter.setQualifier("a b c");

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is("a_user_story_should_do_this_a_b_c.html"));

    }


    @Test
    public void the_resources_can_come_from_a_different_location_in_a_jar_file() throws Exception {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "alt-report-resources";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testOutcome);
        
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "alternative.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void the_resources_can_come_from_the_current_project() throws Exception {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
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

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        System.setProperty("thucydides.report.resources", "alt-report-resources");
        reporter.generateReportFor(testOutcome);
        
        File expectedCssStylesheet = new File(new File(outputDirectory,"css"), "alternative.css");
        assertThat(expectedCssStylesheet.exists(), is(true));
    }

    @Test
    public void when_an_alternative_resource_directory_is_used_the_default_stylesheet_is_not_copied() throws Exception {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        final String alternativeResourceDirectory = "alt-report-resources";
        reporter.setResourceDirectory(alternativeResourceDirectory);
        reporter.generateReportFor(testOutcome);
        
        File defaultCssStylesheet = new File(new File(outputDirectory,"css"), "core.css");
        assertThat(defaultCssStylesheet.exists(), is(false));
    }
    

    @Test
    public void the_report_should_list_test_groups_as_headings_in_the_table() throws Exception {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");

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

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
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
