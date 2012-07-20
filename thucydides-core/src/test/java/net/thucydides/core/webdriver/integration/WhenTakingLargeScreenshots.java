package net.thucydides.core.webdriver.integration;

import com.google.common.base.Function;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.screenshots.MultithreadScreenshotProcessor;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.screenshots.ScreenshotProcessor;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.StaticTestSite;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenTakingLargeScreenshots {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private File screenshotDirectory;

    private WebDriver driver;
    private StaticTestSite testSite;

    private EnvironmentVariables environmentVariables;

    @Before
    public void createScreenshotDir() {

        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();

        testSite = new StaticTestSite();
    }

    @After
    public void closeBrowser() {
        try {
            driver.quit();
        } catch (Exception e) { // Ignore - we don't really care
        }
    }

    @Test
    public void should_take_screenshot_with_specified_dimensions() throws Exception {

        environmentVariables.setProperty("thucydides.browser.width", "800");
        environmentVariables.setProperty("thucydides.browser.height", "400");

        driver = testSite.open();

        ScreenshotProcessor screenshotProcessor = new MultithreadScreenshotProcessor(environmentVariables);
        Photographer photographer = new Photographer(driver, screenshotDirectory,screenshotProcessor);
        File screenshotFile = photographer.takeScreenshot("screenshot");

		waitUntilFileIsWritten(screenshotFile);

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(greaterThan(750))); // In Windows the actual dimensions are slightly less
    }


    @Test
    public void should_resize_screenshot_if_requested() throws Exception {

        environmentVariables.setProperty("thucydides.browser.width", "1000");
        environmentVariables.setProperty("thucydides.browser.height", "800");

        environmentVariables.setProperty("thucydides.resized.image.width", "600");

        driver = testSite.open();

        ScreenshotProcessor screenshotProcessor = new MultithreadScreenshotProcessor(environmentVariables);
        Photographer photographer = new Photographer(driver, screenshotDirectory,screenshotProcessor);
        File screenshotFile = photographer.takeScreenshot("screenshot");

        waitUntilFileIsWritten(screenshotFile);

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(600));
    }


    @Test
    public void should_take_screenshots_correctly() throws IOException {
        driver = testSite.open("http:www.google.com", "screenshots/google.html");

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");

		waitUntilFileIsWritten(screenshotFile);

        assertThat(screenshotFile.exists(), is(true));
    }

    @Test
    public void should_take_screenshots_correctly_in_chrome() throws IOException {

        driver = testSite.open("http://www.google.com", "screenshots/google.html", "chrome");

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");

		waitUntilFileIsWritten(screenshotFile);

        assertThat(screenshotFile.exists(), is(true));
    }

	private void waitUntilFileIsWritten(File screenshotFile) {
        Wait<File> wait = new FluentWait<File>(screenshotFile)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(250, TimeUnit.MILLISECONDS);

        wait.until(new Function<File, Boolean>() {
            public Boolean apply(File file) {
                return file.exists();
            }
        });
    }

    @Mock
    Logger logger;

    @Mock
    FirefoxDriver mockDriver;

    @Test
    public void should_not_explode_when_firefox_cannot_take_a_large_screenshot() {

        when(mockDriver.getScreenshotAs(OutputType.BYTES)).thenThrow(new WebDriverException());

        Photographer photographer = new Photographer(mockDriver, screenshotDirectory) {
            @Override
            protected Logger getLogger() {
                return logger;
            }
        };
        File screenshot = photographer.takeScreenshot("screenshot");  // should not throw an exception
        if (screenshot == null) {
            verify(logger).warn(contains("Failed to write screenshot"));
        }
    }
}
