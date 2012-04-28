package net.thucydides.core.screenshots;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenScreenshotsAreTaken {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private File screenshotDirectory;
    private byte[] screenshotTaken;

    @Mock
    private FirefoxDriver driver;

    @Mock
    private HtmlUnitDriver htmlDriver;

    private Photographer photographer;


    class MockPhotographer extends Photographer {

        public MockPhotographer(final WebDriver driver, final File targetDirectory) {
            super(driver, targetDirectory);
        }

        @Override
        protected boolean driverCanTakeSnapshots() {
            return true;
        }
    }
    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        photographer = new Photographer(driver, screenshotDirectory);        
    }

    @Before
    public void prepareTemporaryFilesAndDirectories() throws IOException {
        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        screenshotTaken = new byte[10000];
    }

    
    @Test
    public void the_driver_should_not_take_screenshots_if_the_driver_is_not_available() throws IOException {

        Photographer photographer = new MockPhotographer(null, screenshotDirectory);
        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        photographer.takeScreenshot("screenshot");
        
        verify(driver,times(0)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_driver_should_capture_the_image() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        photographer.takeScreenshot("screenshot");

        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }


    @Test
    public void should_not_take_a_snapshot_if_unsupported_by_the_driver() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        Photographer photographer = new Photographer(htmlDriver, screenshotDirectory);
        photographer.takeScreenshot("screenshot");

        verify(driver,never()).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_driver_should_save_the_corresponding_source_code() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        when(driver.getPageSource()).thenReturn("<html/>");
        photographer.takeScreenshot("screenshot");

        verify(driver,times(1)).getPageSource();
    }

    @Test
    public void the_screenshot_should_be_stored_in_the_target_directory() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        
        String screenshotFile = photographer.takeScreenshot("screenshot").getName();
        File savedScreenshot = new File(screenshotDirectory, screenshotFile);
        
        assertThat(savedScreenshot.isFile(), is(true));
    }

    @Test
    public void the_photographer_should_return_the_stored_screenshot_filename() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        
        String savedFileName = photographer.takeScreenshot("screenshot").getName();
        
        File savedScreenshot = new File(screenshotDirectory, savedFileName);
        
        assertThat(savedScreenshot.isFile(), is(true));
    }


    @Test
    public void the_photographer_should_provide_the_HTML_source_code_for_a_given_screenshot() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        when(driver.getPageSource()).thenReturn("<html/>");

        File screenshotFile = photographer.takeScreenshot("screenshot");

        File htmlSource = photographer.getMatchingSourceCodeFor(screenshotFile);

        assertThat(htmlSource.isFile(), is(true));
    }

    @Test
    public void the_photographer_should_return_null_for_the_source_code_of_a_null_screenshot() throws IOException {
        assertThat( photographer.getMatchingSourceCodeFor(null), is(nullValue()));
    }

    @Test
    public void successive_screenshots_should_have_different_names() throws IOException {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        
        String screenshotName1 = photographer.takeScreenshot("screenshot").getName();
        String screenshotName2 = photographer.takeScreenshot("screenshot").getName();
        
        assertThat(screenshotName1, is(not((screenshotName2))));
    }

    @Test
    public void calling_api_generates_a_filename_safe_hashed_name_for_the_screenshot() throws IOException {
        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot("test1_finished").getName();
        
        assertThat(screenshotFile, startsWith("screenshot-989da2d4"));
    }
    
    @Test
    public void by_default_screenshot_files_start_with_Screenshot() throws IOException {
        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot("screenshot").getName();
        
        assertThat(screenshotFile, startsWith("screenshot"));
    }

    @Mock
    ScreenshotProcessor screenshotProcessor;

    @Test
    public void should_send_screenshots_to_screenshot_processor() {

        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshotTaken);
        photographer.setScreenshotProcessor(screenshotProcessor);
        String screenshotFile = photographer.takeScreenshot("screenshot").getName();

    }

}
