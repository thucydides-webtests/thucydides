package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class WhenScreenshotsAreTaken {

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private File screenshotDirectory;
    private File screenshotTaken;

    @Mock
    private FirefoxDriver driver;

    @Mock
    private HtmlUnitDriver htmlDriver;

    private Photographer photographer;


    class MockPhotographer extends Photographer {

        public MockPhotographer(final WebDriver driver, final File targetDirectory) {
            super(driver, targetDirectory);
        }

        public MockPhotographer(final WebDriver driver, final File targetDirectory, final Optional<BlurLevel> blurLevel) {
            super(driver, targetDirectory, blurLevel);
        }

        @Override
        protected boolean driverCanTakeSnapshots() {
            return (driver != null);
        }

        @Override
        protected File blur(File srcFile) throws Exception {
            return srcFile;
        }
    }

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        prepareTemporaryFilesAndDirectories();
        photographer = new Photographer(driver, screenshotDirectory);        
    }

    public void prepareTemporaryFilesAndDirectories() throws IOException {
        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        screenshotTaken = temporaryDirectory.newFile("aScreenshot.png");
    }

    
    @Test
    public void the_driver_should_not_take_screenshots_if_the_driver_is_not_available() throws Exception {

        Photographer photographer = new MockPhotographer(null, screenshotDirectory);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot("screenshot");
        waitUntilScreenshotsProcessed();

        verify(driver,times(0)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_driver_should_capture_the_image() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot("screenshot");
        waitUntilScreenshotsProcessed();

        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void should_not_take_a_snapshot_if_unsupported_by_the_driver() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        Photographer photographer = new Photographer(htmlDriver, screenshotDirectory);
        photographer.takeScreenshot("screenshot");
        waitUntilScreenshotsProcessed();

        verify(driver,never()).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_driver_should_save_the_corresponding_source_code() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        when(driver.getPageSource()).thenReturn("<html/>");

        photographer.takeScreenshot("screenshot");
        waitUntilScreenshotsProcessed();

        verify(driver,times(1)).getPageSource();
    }

    @Test
    public void the_screenshot_should_be_stored_in_the_target_directory() throws IOException, InterruptedException{

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String screenshotFile = photographer.takeScreenshot("screenshot").get().getName();
        waitUntilScreenshotsProcessed();
        File savedScreenshot = new File(screenshotDirectory, screenshotFile);
        savedScreenshot.setReadable(true);
        savedScreenshot.setWritable(true);
        assertThat(savedScreenshot.isFile(), is(true));
    }

    private void waitUntilScreenshotsProcessed() throws InterruptedException {
        photographer.getScreenshotProcessor().waitUntilDone();
        Thread.sleep(50);
    }

    @Test
    public void the_photographer_should_return_the_stored_screenshot_filename() throws IOException, InterruptedException {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String savedFileName = photographer.takeScreenshot("screenshot").get().getName();
        waitUntilScreenshotsProcessed();
        File savedScreenshot = new File(screenshotDirectory, savedFileName);
        
        assertThat(savedScreenshot.isFile(), is(true));
    }


    @Test
    public void the_photographer_should_provide_the_HTML_source_code_for_a_given_screenshot() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        when(driver.getPageSource()).thenReturn("<html/>");

        File screenshotFile = photographer.takeScreenshot("screenshot").get();
        waitUntilScreenshotsProcessed();

        File htmlSource = photographer.getMatchingSourceCodeFor(screenshotFile);

        assertThat(htmlSource.isFile(), is(true));
    }

    @Test
    public void the_photographer_should_return_null_for_the_source_code_of_a_null_screenshot() throws IOException {
        assertThat( photographer.getMatchingSourceCodeFor(null), is(nullValue()));
    }

    @Test
    public void successive_screenshots_should_have_different_names() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String screenshotName1 = photographer.takeScreenshot("screenshot").get().getName();
        String screenshotName2 = photographer.takeScreenshot("screenshot").get().getName();
        waitUntilScreenshotsProcessed();

        assertThat(screenshotName1, is(not((screenshotName2))));
    }

    @Test
    public void calling_api_generates_a_filename_safe_hashed_name_for_the_screenshot() throws Exception {
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot("test1_finished").get().getName();
        waitUntilScreenshotsProcessed();

        assertThat(screenshotFile, startsWith("screenshot-ede8d449a"));
    }
    
    @Test
    public void by_default_screenshot_files_start_with_Screenshot() throws Exception {
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot("screenshot").get().getName();
        waitUntilScreenshotsProcessed();

        assertThat(screenshotFile, startsWith("screenshot"));
    }

    @Mock
    ScreenshotProcessor screenshotProcessor;

    @Test
    public void should_send_screenshots_to_screenshot_processor() {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.setScreenshotProcessor(screenshotProcessor);

        photographer.takeScreenshot("screenshot");

        verify(screenshotProcessor).queueScreenshot((QueuedScreenshot) anyObject());
    }

    @Test
    public void should_blur_screenshots_if_blurScreenshots_option_is_present() throws Exception {
        Photographer photographer = new MockPhotographer(driver, screenshotDirectory, Optional.of(BlurLevel.HEAVY));
        photographer = spy(photographer);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot("screenshot");
        waitUntilScreenshotsProcessed();

        verify(photographer, times(1)).blur(any(File.class));
        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void should_not_blur_screenshots_if_blurScreenshots_option_is_absent() throws Exception {
        Photographer photographer = new MockPhotographer(driver, screenshotDirectory, Optional.<BlurLevel>absent());
        photographer = spy(photographer);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot("screenshot");
        waitUntilScreenshotsProcessed();

        verify(photographer, times(0)).blur(any(File.class));
        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }


}
