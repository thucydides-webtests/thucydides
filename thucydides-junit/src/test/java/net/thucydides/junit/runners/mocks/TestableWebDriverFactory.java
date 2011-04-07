package net.thucydides.junit.runners.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.webdriver.WebDriverFactory;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A mock web driver factory for tests.
 * It behaves just like a normal WebDriverFactory, except that it
 * does not create any web driver instances. Instead, it returns
 * a Mockito mock WebDriver instance. This lets tests
 * check that the right drivers are created and invoked, without
 * actually opening any browsers.
 * @author johnsmart
 *
 */
public class TestableWebDriverFactory extends WebDriverFactory {

    private WebDriver driver;
    private File screenshotFile;
    private int firefoxCount = 0;
    private int chromeCount = 0;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestableWebDriverFactory.class);

    public TestableWebDriverFactory() {
    }

    public TestableWebDriverFactory(File temporaryDirectory) {
        screenshotFile = new File(temporaryDirectory, "screenshot.png");
        try {
            screenshotFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error("Failed to create screenshot file", e);
        }
    }

    public WebDriver getDriver() {
        if (driver == null) {
            getFirefoxDriver();
        }
        return driver;
    }
    
    public WebDriver getFirefoxDriver() {
        if (driver == null) {
            FirefoxDriver mockDriver = mock(FirefoxDriver.class);            
            when(mockDriver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotFile);
            
            driver = mockDriver;
        }
        return driver;
    }

    public WebDriver getChromeDriver() {
        if (driver == null) {
            ChromeDriver mockDriver = mock(ChromeDriver.class);            
            when(mockDriver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotFile);
            
            driver = mockDriver;
        }
        return driver;
    }
    
    @Override
    protected WebDriver newChromeDriver() {
        chromeCount++;
        return (WebDriver) getChromeDriver();
    }

    public int fireFoxOpenedCount() {
        return firefoxCount;
    }
    
    @Override
    protected WebDriver newFirefoxDriver() {
        firefoxCount++;
        return (WebDriver) getFirefoxDriver();
    }
        
    public int createdFirefoxDrivers() {
        return firefoxCount;
    }
    
    public int createdChromeDrivers() {
        return chromeCount;
    }
}
