package net.thucydides.core.webdriver;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 * 
 * @author johnsmart
 *
 */
public class WebDriverFactory {

    /***
     * Create a new WebDriver instance of a given type.
     */
    public WebDriver newInstanceOf(final SupportedWebDriver driverType)  {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }
        
        switch (driverType) {
            case FIREFOX:
                return newFirefoxDriver();
            case CHROME:
                return newChromeDriver();
            default:
                throw new IllegalArgumentException(driverType 
                          + " support hasn't been implemented yet - this is a bug.");
        }
    }

    protected WebDriver newChromeDriver() {
        return new ChromeDriver();
    }

    protected WebDriver newFirefoxDriver() {
        return new FirefoxDriver();
    }
    
}
