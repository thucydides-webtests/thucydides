package net.thucydides.core.reports.html;

import com.google.common.base.Preconditions;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.IOException;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "velocity/default.vm";
    
    private String qualifier;


    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public HtmlAcceptanceTestReporter() {
        setTemplatePath(DEFAULT_ACCEPTANCE_TEST_REPORT);
    }

    public String getName() {
        return "html";
    }

    /**
     * Generate an XML report for a given test run.
     */
    public File generateReportFor(final AcceptanceTestRun testRun) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        VelocityContext context = new VelocityContext();
        context.put("testrun", testRun);
        String htmlContents = mergeVelocityTemplate(context);

        copyResourcesToOutputDirectory();

        String reportFilename = reportFor(testRun);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private String reportFor(final AcceptanceTestRun testRun) {
        if (qualifier != null) {
            return testRun.getReportName(HTML, qualifier);
        } else {
            return testRun.getReportName(HTML);
        }
    }
}
