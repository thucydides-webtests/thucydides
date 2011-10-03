package net.thucydides.core.webdriver.integration;

import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class WhenTakingLargeScreenshots {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private File screenshotDirectory;
    private WebDriver driver;

    @After
    public void closeBrowser() {
        driver.quit();
    }

    @Before
    public void createScreenshotDir() {
        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
    }

    private static File fileInClasspathCalled(final String resourceName) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(resourceName).getPath());
    }

    private static void openStaticTestSite(WebDriver driver) {
        File testSite = fileInClasspathCalled("static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());

    }

    @Test
    public void should_take_screenshot_with_specified_dimensions()  throws Exception {

        System.setProperty("thucydides.browser.width","800");
        System.setProperty("thucydides.browser.height","400");

        driver = (new WebDriverFactory()).newInstanceOf(SupportedWebDriver.FIREFOX);

        openStaticTestSite(driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");
        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(greaterThan(750))); // In Windows the actual dimensions are slightly less
    }

    @Test
    public void should_take_screenshot_with_specified_larger_dimensions()  throws Exception {

        System.setProperty("thucydides.browser.width","1600");
        System.setProperty("thucydides.browser.height","1200");

        driver = (new WebDriverFactory()).newInstanceOf(SupportedWebDriver.FIREFOX);

        openStaticTestSite(driver);

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot("screenshot");
        ResizableImage image = ResizableImage.loadFrom(screenshotFile);


        assertThat(image.getWitdh(), greaterThan(1000));
    }

}
