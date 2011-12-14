package net.thucydides.core.webdriver.integration;

import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;

public class WhenTakingLargeScreenshots {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private File screenshotDirectory;
    private WebDriver driver;

    private MockEnvironmentVariables environmentVariables;

    @After
    public void closeBrowser() {
        try {
            driver.quit();
        } catch (Exception e) { // Ignore - we don't really care
        }
    }

    @Before
    public void createScreenshotDir() {
        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
    }

    private static File fileInClasspathCalled(final String resourceName) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(resourceName).getPath());
    }

    private static void openStaticTestSite(WebDriver driver) {
        File testSite = fileInClasspathCalled("static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }

    @Test
    public void should_take_screenshot_with_specified_dimensions() throws Exception {

        environmentVariables.setProperty("thucydides.browser.width", "800");
        environmentVariables.setProperty("thucydides.browser.height", "400");

        driver = (new WebDriverFactory(environmentVariables)).newInstanceOf(SupportedWebDriver.FIREFOX);

        openStaticTestSite(driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");
        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(greaterThan(750))); // In Windows the actual dimensions are slightly less
    }

    @Test
    public void should_take_screenshot_with_specified_larger_dimensions() throws Exception {

        environmentVariables.setProperty("thucydides.browser.width", "1600");
        environmentVariables.setProperty("thucydides.browser.height", "1200");

        driver = (new WebDriverFactory(environmentVariables)).newInstanceOf(SupportedWebDriver.FIREFOX);

        openStaticTestSite(driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");
        ResizableImage image = ResizableImage.loadFrom(screenshotFile);


        assertThat(image.getWitdh(), greaterThan(800));
    }

    @Test
    public void should_take_screenshots_correctly() throws IOException {
        driver = (new WebDriverFactory(environmentVariables)).newInstanceOf(SupportedWebDriver.FIREFOX);
        openPage("google.html", driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");

        assertThat(screenshotFile.exists(), is(true));
    }

    @Ignore("Screenshots do not work in Chrome in Selenium 2.1.15")
    @Test
    public void should_take_screenshots_correctly_in_chrome() throws IOException {
        driver = (new WebDriverFactory(environmentVariables)).newInstanceOf(SupportedWebDriver.CHROME);
        openPage("google.html", driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");

        assertThat(screenshotFile, is(notNull()));
        assertThat(screenshotFile.exists(), is(true));
    }

    @Mock
    Logger logger;

    @Test
    public void should_not_explode_when_firefox_cannot_take_a_large_screenshot() {
        driver = (new WebDriverFactory(environmentVariables)).newInstanceOf(SupportedWebDriver.FIREFOX);
        openPage("big-page.html", driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory) {
            @Override
            protected Logger getLogger() {
                return logger;
            }
        };
        File screenshot = photographer.takeScreenshot("screenshot");  // should not throw an exception
        if (screenshot == null) {
            verify(logger).warn(contains("Failed to write screenshot"), any(WebDriverException.class));
        }
    }

    @Test
    public void should_not_explode_when_chrome_cannot_take_a_large_screenshot() throws IOException {
        driver = (new WebDriverFactory(environmentVariables)).newInstanceOf(SupportedWebDriver.CHROME);
        openPage("big-page.html", driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory) {
            @Override
            protected Logger getLogger() {
                return logger;
            }
        };
        File screenshot = photographer.takeScreenshot("screenshot");  // should not throw an exception
        if (screenshot == null) {
            verify(logger).warn(contains("Failed to write screenshot"), any(WebDriverException.class));
        }
    }

    private void openPage(String pageName, WebDriver driver) {
        File largePage = fileInClasspathCalled("screenshots/" + pageName);
        driver.get("file://" + largePage.getAbsolutePath());

    }

}
