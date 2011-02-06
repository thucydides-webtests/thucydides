package net.thucydides.core.webdriver;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverFactory {

    public WebDriver newInstanceOf(SupportedWebDriver driverType)  {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }
        
        switch (driverType) {
            case FIREFOX:
                return newFirefoxDriver();
            case CHROME:
                return newChromeDriver();
            default:
                throw new IllegalArgumentException(driverType + " support hasn't been implemented yet - this is a bug.");
        }
    }

    protected WebDriver newChromeDriver() {
        return new ChromeDriver();
    }

    protected WebDriver newFirefoxDriver() {
        return new FirefoxDriver();
    }
    
}
