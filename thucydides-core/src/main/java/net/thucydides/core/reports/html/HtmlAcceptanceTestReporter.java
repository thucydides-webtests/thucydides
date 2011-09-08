package net.thucydides.core.reports.html;

import com.google.common.base.Preconditions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.model.Screenshot;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "freemarker/default.ftl";
    private static final String DEFAULT_ACCEPTANCE_TEST_SCREENSHOT = "freemarker/screenshots.ftl";

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
        Formatter formatter = new Formatter(ThucydidesSystemProperty.getValue(ThucydidesSystemProperty.ISSUE_TRACKER_URL));
        context.put("formatter", formatter);

    }

    private void generateScreenshotReportsFor(final TestOutcome testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        List<Screenshot> screenshots = expandScreenshots(testOutcome.getScreenshots());

        String screenshotReport = withoutType(testOutcome.getReportName() + "_screenshots") + ".html";

        Map<String,Object> context = new HashMap<String,Object>();
        context.put("screenshots", screenshots);
        context.put("testOutcome", testOutcome);
        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_SCREENSHOT).usingContext(context);
        writeReportToOutputDirectory(screenshotReport, htmlContents);

    }

    private List<Screenshot> expandScreenshots(List<Screenshot> screenshots) throws IOException {
        List<Screenshot> expandScreenshotList = new ArrayList<Screenshot>();

        int maxWidth = maxScreenshotWidthIn(screenshots);
        int maxHeight = maxScreenshotHeightIn(screenshots);

        for(Screenshot screenshot : screenshots) {
            File screenshotFile = new File(getOutputDirectory(), screenshot.getFilename());
            if (screenshotFile.exists()) {
                ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxWidth, maxHeight);
                File scaledFile = new File(getOutputDirectory(), "scaled_" + screenshot.getFilename());
                scaledImage.saveTo(scaledFile);
                expandScreenshotList.add(new Screenshot(scaledFile.getName(),screenshot.getDescription()));
            } else {
                expandScreenshotList.add(screenshot);
            }
        }
        return expandScreenshotList;
    }

    private int maxScreenshotWidthIn(List<Screenshot> screenshots) throws IOException {
        int maxWidth = 0;
        for (Screenshot screenshot : screenshots) {
            File screenshotFile = new File(getOutputDirectory(),screenshot.getFilename());
            if (screenshotFile.exists()) {
                maxWidth = maxWidthOf(maxWidth, screenshotFile);
            }
        }
        return maxWidth;
    }

    private int maxWidthOf(int maxWidth, File screenshotFile) throws IOException {
        int width = ResizableImage.loadFrom(screenshotFile).getWitdh();
        if (width > maxWidth) {
            maxWidth = width;
        }
        return maxWidth;
    }

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
        if (height > maxHeight) {
            maxHeight = height;
        }
        return maxHeight;
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
