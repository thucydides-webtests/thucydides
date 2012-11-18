package net.thucydides.core.reports.html.screenshots;

import net.thucydides.core.images.SimpleImageInfo;
import net.thucydides.core.model.Screenshot;
import net.thucydides.core.util.ExtendedTemporaryFolder;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenReformattingScreenshots {

    @Rule
    public ExtendedTemporaryFolder folder = new ExtendedTemporaryFolder();

    private File screenshotDirectory;

    @Before
    public void setupWorkingDirectory() throws IOException {
        screenshotDirectory = folder.newFolder("screenshots");
        File screenshotsSourceDirectory = new File(Thread.currentThread().getContextClassLoader().getResource("screenshots").getPath());
        File[] screenshots = screenshotsSourceDirectory.listFiles();
        for(File screenshot : screenshots) {
            FileUtils.copyFileToDirectory(screenshot, screenshotDirectory);
        }
    }

    @Test
    public void should_resize_image_to_a_specified_height() throws IOException {

        Screenshot screenshot = new Screenshot("google_page_1.png", "Google", 1200);
        Screenshot expandedScreenshot = ScreenshotFormatter.forScreenshot(screenshot).inDirectory(screenshotDirectory).expandToHeight(1500);

        int resultingHeight = new SimpleImageInfo(new File(screenshotDirectory, expandedScreenshot.getFilename())).getHeight();
        assertThat(resultingHeight, is(1500));
        assertThat(expandedScreenshot.getWidth(), is(1200));
    }

    @Test
    public void should_limit_image_resize_to_the_maximum_supported_height() throws IOException {

        Screenshot screenshot = new Screenshot("google_page_1.png", "Google", 1200);
        Screenshot expandedScreenshot = ScreenshotFormatter.forScreenshot(screenshot).inDirectory(screenshotDirectory).expandToHeight(30000);

        int resultingHeight = new SimpleImageInfo(new File(screenshotDirectory, expandedScreenshot.getFilename())).getHeight();
        assertThat(resultingHeight, is(4000));
        assertThat(expandedScreenshot.getWidth(), is(1200));
    }

    @Test
    public void should_not_resize_image_that_is_larger_than_the_specified_height() throws IOException {

        Screenshot screenshot = new Screenshot("amazon.png", "Amazon", 1495);
        Screenshot expandedScreenshot = ScreenshotFormatter.forScreenshot(screenshot).inDirectory(screenshotDirectory).expandToHeight(2000);

        int resultingHeight = new SimpleImageInfo(new File(screenshotDirectory, expandedScreenshot.getFilename())).getHeight();
        assertThat(resultingHeight, is(2236));
        assertThat(expandedScreenshot.getWidth(), is(1495));
    }

    @Test
    public void should_not_resize_image_if_target_height_is_larger_than_the_maximum_height() throws IOException {

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805);
        Screenshot expandedScreenshot = ScreenshotFormatter.forScreenshot(screenshot).inDirectory(screenshotDirectory).expandToHeight(4000);

        int resultingHeight = new SimpleImageInfo(new File(screenshotDirectory, expandedScreenshot.getFilename())).getHeight();
        assertThat(resultingHeight, is(29107));
        assertThat(expandedScreenshot.getWidth(), is(805));
    }

    @Test
    public void should_only_display_the_first_line_of_an_error_message_in_the_UI() {
        String errorMessage = "<org.openqa.selenium.ElementNotVisibleException: Unable to locate element: {\"method\":\"name\",\"selector\":\"fieldDoesNotExist\"}; duration or timeout: 8 milliseconds\n" +
                "For documentation on this error, please visit: http://seleniumhq.org/exceptions/no_such_element.html\n" +
                "Build info: version: '2.6.0', revision: '13840', time: '2011-09-13 16:51:41'\n" +
                "System info: os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.7.1', java.version: '1.6.0_26'\n" +
                "Driver info: driver.version: RemoteWebDriver\n" +
                "Build info: version: '2.6.0', revision: '13840', time: '2011-09-13 16:51:41'\n" +
                "System info: os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.7.1', java.version: '1.6.0_26'\n" +
                "Driver info: driver.version: unknown>";

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805, new AssertionError(errorMessage));

        assertThat(screenshot.getShortErrorMessage(), is("Unable to locate element: {'method':'name','selector':'fieldDoesNotExist'}; duration or timeout: 8 milliseconds"));
    }

    @Test
    public void should_only_display_the_first_line_of_a_simple_error_message_in_the_UI() {
        String errorMessage = "Something broke";

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805, new AssertionError(errorMessage));

        assertThat(screenshot.getShortErrorMessage(), is("Something broke"));
    }

    @Test
    public void should_display_message_from_original_cause_if_present() {
        Exception exception = new Exception("Oops", new Exception("Something went wrong"));
        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805, exception);

        assertThat(screenshot.getShortErrorMessage(), is("Something went wrong"));
    }
}
