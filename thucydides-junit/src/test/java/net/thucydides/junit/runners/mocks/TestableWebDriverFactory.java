package net.thucydides.junit.runners.mocks;

import net.thucydides.junit.runners.WebDriverFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import static org.mockito.Mockito.mock;

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

    private ChromeDriver chromeDriver = null;
    private FirefoxDriver firefoxDriver = null;
    private InternetExplorerDriver internetExplorerDriver = null;
    
    private int firefoxCount = 0;
    private int chromeCount = 0;
    private int internetExplorerCount = 0;
    
    
    @Override
    protected WebDriver newChromeDriver() {
        chromeCount++;
        chromeDriver = mock(ChromeDriver.class);
        return chromeDriver;
    }

    @Override
    protected WebDriver newFirefoxDriver() {
        firefoxCount++;
        firefoxDriver = mock(FirefoxDriver.class);
        return firefoxDriver;
    }
        
    public int createdFirefoxDrivers() {
        return firefoxCount;
    }
    
    public int createdChromeDrivers() {
        return chromeCount;
    }
    
    public int createdInternetExplorerDrivers() {
        return internetExplorerCount;
    }

    public ChromeDriver getChromeDriver() {
        return chromeDriver;
    }

    public FirefoxDriver getFirefoxDriver() {
        return firefoxDriver;
    }

    public InternetExplorerDriver getInternetExplorerDriver() {
        return internetExplorerDriver;
    }
}
