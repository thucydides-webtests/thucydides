package net.thucydides.core.screenshots;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.screenshots.Photographer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class WhenScreenshotsAreTaken {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();
    private File screenshotDirectory;
    private File screenshotTaken;

    @Mock
    private TakesScreenshot driver;
    
    private Photographer photographer;
    
    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        photographer = new Photographer(driver, screenshotDirectory);        
    }

    @Before
    public void prepareTemporaryFilesAndDirectories() throws IOException {
        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        screenshotTaken = temporaryDirectory.newFile("screenshot.png");
    }
    
    @Test
    public void the_driver_should_capture_the_image() throws IOException {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        photographer.takeScreenshot("screenshot");
        
        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());        
    }
    
    @Test
    public void the_screenshot_should_be_stored_in_the_target_directory() throws IOException {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String screenshotFile = photographer.takeScreenshot("screenshot").getName();
        File savedScreenshot = new File(screenshotDirectory, screenshotFile);
        
        assertThat(savedScreenshot.isFile(), is(true));
    }

    @Test
    public void the_photographer_should_return_the_stored_screenshot_filename() throws IOException {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String savedFileName = photographer.takeScreenshot("screenshot").getName();
        
        File savedScreenshot = new File(screenshotDirectory, savedFileName);
        
        assertThat(savedScreenshot.isFile(), is(true));
    }
    
    
    @Test
    public void successive_screenshots_should_have_different_names() throws IOException {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String screenshotName1 = photographer.takeScreenshot("screenshot").getName();
        String screenshotName2 = photographer.takeScreenshot("screenshot").getName();
        
        assertThat(screenshotName1, is(not((screenshotName2))));
    }

    @Test
    public void calling_api_generates_a_filename_safe_hashed_name_for_the_screenshot() throws IOException {
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot("test1_finished").getName();
        
        assertThat(screenshotFile, startsWith("screenshot-989da2d4"));
    }
    
    @Test
    public void by_default_screenshot_files_start_with_Screenshot() throws IOException {
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot("screenshot").getName();
        
        assertThat(screenshotFile, startsWith("screenshot"));
    }
    
}
