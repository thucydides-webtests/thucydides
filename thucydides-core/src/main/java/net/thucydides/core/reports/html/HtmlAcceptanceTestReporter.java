package net.thucydides.core.reports.html;

import com.google.common.base.Preconditions;
import net.thucydides.core.model.Screenshot;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "velocity/default.vm";
    private static final String DEFAULT_ACCEPTANCE_TEST_SCREENSHOT = "velocity/screenshots.vm";

    private String qualifier;


    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public HtmlAcceptanceTestReporter() {
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
        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_REPORT).usingContext(context);

        copyResourcesToOutputDirectory();

        generateScreenshotReportsFor(testOutcome);

        String reportFilename = reportFor(testOutcome);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private void generateScreenshotReportsFor(final TestOutcome testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        List<Screenshot> screenshots = testOutcome.getScreenshots();

        String screenshotReport = withoutType(testOutcome.getReportName() + "_screenshots") + ".html";

        VelocityContext context = new VelocityContext();
        context.put("screenshots", screenshots);
        context.put("testOutcome", testOutcome);
        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_SCREENSHOT).usingContext(context);
        writeReportToOutputDirectory(screenshotReport, htmlContents);

    }

    private String withoutType(final String screenshot) {
        int dot = screenshot.lastIndexOf('.');
        if (dot > 0) {
            return screenshot.substring(0, dot);
        } else {
            return screenshot;
        }
    }

    private String reportFor(final TestOutcome testOutcome) {
        if (qualifier != null) {
            return testOutcome.getReportName(HTML, qualifier);
        } else {
            return testOutcome.getReportName(HTML);
        }
    }
}
