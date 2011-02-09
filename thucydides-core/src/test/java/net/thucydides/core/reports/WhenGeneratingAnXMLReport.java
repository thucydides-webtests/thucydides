package net.thucydides.core.reports;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenGeneratingAnXMLReport {

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run() throws IOException {
        AcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        
        AcceptanceTestRun testRun = new AcceptanceTestRun("A simple test case");
        testRun.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        File xmlReport = reporter.generateReportFor(testRun);
        assertThat(xmlReport.exists(), is(true));
    }
   

    
}
