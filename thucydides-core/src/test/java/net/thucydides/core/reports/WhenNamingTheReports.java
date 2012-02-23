package net.thucydides.core.reports;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.Stories;
import net.thucydides.core.model.TestOutcome;
import org.junit.Test;

import static net.thucydides.core.model.ReportType.HTML;
import static net.thucydides.core.model.ReportType.XML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenNamingTheReports {


    class AUserStory {};

    @Feature
    class AFeature {
        class AUserStoryInAFeature {}
    }

    @Story(AUserStory.class)
    class SomeTestScenario {
        public void a_simple_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Story(AFeature.AUserStoryInAFeature.class)
    class SomeOtherTestScenario {
        public void a_simple_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Test
    public void the_report_filename_should_be_based_on_the_test_case_name() {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        assertThat(testOutcome.getReportName(), is("a_user_story_a_simple_test_case"));
    }
    
    @Test
    public void the_report_filename_should_replace_spaces_with_underscores() {

        TestOutcome testOutcome = TestOutcome.forTestInStory("A simple test case", net.thucydides.core.model.Story.from(AUserStory.class));
        String reportName = testOutcome.getReportName(XML);
        
        assertThat(reportName, is("a_user_story_a_simple_test_case.xml"));
    }

    @Test
    public void the_report_filename_should_be_determined_even_if_no_method_is_named() {

        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String reportName = testOutcome.getReportName(XML);

        assertThat(reportName, is("a_user_story_a_simple_test_case.xml"));
    }

    @Test
    public void the_html_report_filename_should_have_the_html_suffix() {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        String reportName = testOutcome.getReportName(HTML);
        
        assertThat(reportName, is("a_simple_test_case.html"));
    }

    @Test
    public void the_html_report_filename_should_refer_to_the_user_story_name_if_present() {

        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML);
        
        assertThat(reportName, is("a_user_story_should_do_this.html"));
    }

    @Test
    public void a_qualifier_can_be_provided_to_distinguish_html_reports_from_other_similar_reports() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML,"qualifier");

        assertThat(reportName, is("a_user_story_should_do_this_qualifier.html"));
    }

    @Test
    public void a_null_qualifier_should_be_ignored() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML,null);

        assertThat(reportName, is("a_user_story_should_do_this.html"));
    }

    @Test
    public void when_no_qualifier_is_provided_the_normal_report_name_is_used() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML);

        assertThat(reportName, is("a_user_story_should_do_this.html"));
    }

    @Test
    public void a_qualifier_can_be_provided_to_distinguish_xml_reports_from_other_similar_reports() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(XML,"qualifier");

        assertThat(reportName, is("a_user_story_should_do_this_qualifier.xml"));
    }


    @Test
    public void a_user_story_can_provide_its_own_html_report_name() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(AUserStory.class);
        
        String reportName = story.getReportName(HTML);
        
        assertThat(reportName, is("a_user_story.html"));
    }

    @Test
    public void a_user_story_can_provide_its_own_xml_report_name() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(AUserStory.class);
        
        String reportName = story.getReportName(XML);
        
        assertThat(reportName, is("a_user_story.xml"));
    }

    @Test
    public void a_user_story_can_provide_its_own_base_report_name() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(AUserStory.class);
        
        String reportName = story.getReportName();
        
        assertThat(reportName, is("a_user_story"));
    }

    @Test
    public void the_stories_class_can_provide_the_report_name_directly() {

        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(AUserStory.class);

        String reportName = Stories.reportFor(story, ReportType.HTML);

        assertThat(reportName, is("a_user_story.html"));
    }

}
