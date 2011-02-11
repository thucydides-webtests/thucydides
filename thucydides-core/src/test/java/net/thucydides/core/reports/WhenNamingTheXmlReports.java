package net.thucydides.core.reports;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

import org.junit.Test;

public class WhenNamingTheXmlReports {

    @Test
    public void the_report_filename_should_be_based_on_the_test_case_name() {
        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        
        AcceptanceTestRun testRun = new AcceptanceTestRun("ASimpleTestCase");

        String reportName = reporter.getNormalizedTestNameFor(testRun);
        
        assertThat(reportName, is("a_simple_test_case.xml"));
    }
    
    @Test
    public void the_report_filename_should_replace_spaces_with_underscores() {

        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");

        String reportName = reporter.getNormalizedTestNameFor(testRun);
        
        assertThat(reportName, is("a_simple_test_case.xml"));
    }
    
}
