package net.thucydides.core.reports;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;
import static net.thucydides.core.model.ReportNamer.ReportType.XML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.UserStory;

import org.junit.Test;

public class WhenNamingTheReports {

    @Test
    public void the_report_filename_should_be_based_on_the_test_case_name() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("ASimpleTestCase");
        testRun.setMethodName("a_simple_test_case");
        assertThat(testRun.getReportName(), is("a_simple_test_case"));
    }
    
    @Test
    public void the_report_filename_should_replace_spaces_with_underscores() {

        
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.setMethodName("a_simple_test_case");
        String reportName = testRun.getReportName(XML);
        
        assertThat(reportName, is("a_simple_test_case.xml"));
    }
    
    @Test
    public void the_html_report_filename_should_have_the_html_suffix() {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case: exception case");
        testRun.setMethodName("a_simple_test_case");
        String reportName = testRun.getReportName(HTML);
        
        assertThat(reportName, is("a_simple_test_case.html"));
    }

    @Test
    public void the_html_report_filename_should_refer_to_the_user_story_name_if_present() {

        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case: exception case");
        testRun.setMethodName("a_simple_test_case");
        testRun.setUserStory(new UserStory("A user story","US1","some.UserStory"));

        String reportName = testRun.getReportName(HTML);
        
        assertThat(reportName, is("a_user_story_a_simple_test_case.html"));
    }

    @Test
    public void a_user_story_can_provide_its_own_html_report_name() {
        UserStory story = new UserStory("A user story", "US1", "UserStory1");
        
        String reportName = story.getReportName(HTML);
        
        assertThat(reportName, is("a_user_story.html"));
    }

    @Test
    public void a_user_story_can_provide_its_own_xml_report_name() {
        UserStory story = new UserStory("A user story", "US1", "UserStory1");
        
        String reportName = story.getReportName(XML);
        
        assertThat(reportName, is("a_user_story.xml"));
    }

    @Test
    public void a_user_story_can_provide_its_own_base_report_name() {
        UserStory story = new UserStory("A user story", "US1", "UserStory1");
        
        String reportName = story.getReportName();
        
        assertThat(reportName, is("a_user_story"));
    }
}
