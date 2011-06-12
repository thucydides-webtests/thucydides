package net.thucydides.core.reports.html;

import com.google.common.base.Preconditions;
import net.thucydides.core.model.TestOutcome;
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
    public File generateReportFor(final TestOutcome testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        VelocityContext context = new VelocityContext();
        context.put("testrun", testOutcome);
        String htmlContents = mergeVelocityTemplate(context);

        copyResourcesToOutputDirectory();

        String reportFilename = reportFor(testOutcome);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private String reportFor(final TestOutcome testOutcome) {
        if (qualifier != null) {
            return testOutcome.getReportName(HTML, qualifier);
        } else {
            return testOutcome.getReportName(HTML);
        }
    }
}
