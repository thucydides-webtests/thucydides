package net.thucydides.core.reports.html.screenshots;

import net.thucydides.core.images.SimpleImageInfo;
import net.thucydides.core.model.Screenshot;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenReformattingScreenshots {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        assertThat(resultingHeight, is(8000));
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

}
