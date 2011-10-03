package net.thucydides.core.reports.html;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Preconditions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.Screenshot;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.html.screenshots.ScreenshotFormatter;
import net.thucydides.core.screenshots.ScreenshotException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "freemarker/default.ftl";
    private static final String DEFAULT_ACCEPTANCE_TEST_SCREENSHOT = "freemarker/screenshots.ftl";
    private static final int MAXIMUM_SCREENSHOT_WIDTH = 1000;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HtmlAcceptanceTestReporter.class);

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

        Map<String,Object> context = new HashMap<String,Object>();
        addTestOutcomeToContext(testOutcome, context);
        addFormattersToContext(context);
        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_REPORT).usingContext(context);

        copyResourcesToOutputDirectory();

        generateScreenshotReportsFor(testOutcome);

        String reportFilename = reportFor(testOutcome);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private void addTestOutcomeToContext(final TestOutcome testOutcome, final Map<String,Object> context) {
        context.put("testOutcome", testOutcome);
    }

    private void addFormattersToContext(final Map<String,Object> context) {
        Formatter formatter = new Formatter(IssueTracking.getIssueTrackerUrl());
        context.put("formatter", formatter);

    }

    private void generateScreenshotReportsFor(final TestOutcome testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        List<Screenshot> screenshots = expandScreenshots(testOutcome.getScreenshots());

        String screenshotReport = testOutcome.getReportName() + "_screenshots.html";

        Map<String,Object> context = new HashMap<String,Object>();
        context.put("screenshots", screenshots);
        context.put("testOutcome", testOutcome);
        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_SCREENSHOT).usingContext(context);
        writeReportToOutputDirectory(screenshotReport, htmlContents);

    }

    private List<Screenshot> expandScreenshots(List<Screenshot> screenshots) throws IOException {
        return convert(screenshots, new ExpandedScreenshotConverter(maxScreenshotHeightIn(screenshots)));
    }

    private class ExpandedScreenshotConverter implements Converter<Screenshot, Screenshot> {
        private final int maxHeight;

        public ExpandedScreenshotConverter(int maxHeight) {
            this.maxHeight = maxHeight;
        }

        public Screenshot convert(Screenshot screenshot) {
            try {
                return ScreenshotFormatter.forScreenshot(screenshot)
                                          .inDirectory(getOutputDirectory())
                                          .expandToHeight(maxHeight);
            } catch (IOException e) {
                LOGGER.error("Failed to write scaled screenshot for {}: {}", screenshot, e);
                throw new ScreenshotException("Failed to write scaled screenshot", e);
            }
        }
    };

    private int maxScreenshotHeightIn(List<Screenshot> screenshots) throws IOException {
        int maxHeight = 0;
        for (Screenshot screenshot : screenshots) {
            File screenshotFile = new File(getOutputDirectory(),screenshot.getFilename());
            if (screenshotFile.exists()) {
                maxHeight = maxHeightOf(maxHeight, screenshotFile);
            }
        }
        return maxHeight;
    }

    private int maxHeightOf(int maxHeight, File screenshotFile) throws IOException {
        int height = ResizableImage.loadFrom(screenshotFile).getHeight();
        int width = ResizableImage.loadFrom(screenshotFile).getWitdh();
        if (width > MAXIMUM_SCREENSHOT_WIDTH) {
            height = (int) ((height * 1.0) * (MAXIMUM_SCREENSHOT_WIDTH * 1.0 / width));
        }
        if (height > maxHeight) {
            maxHeight = height;
        }
        return maxHeight;
    }

    private String reportFor(final TestOutcome testOutcome) {
        if (qualifier != null) {
            return testOutcome.getReportName(HTML, qualifier);
        } else {
            return testOutcome.getReportName(HTML);
        }
    }

}
