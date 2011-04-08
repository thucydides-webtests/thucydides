package net.thucydides.core.webdriver;

import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 * 
 * @author johnsmart
 *
 */
public class WebDriverFactory {

    private static final int TIMEOUT = 120;

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

    /**
     * Initialize a page object's fields using the specified WebDriver instance.
     */
    public static void initElementsWithAjaxSupport(final Object pageObject, final WebDriver driver) {
        ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, TIMEOUT);
        PageFactory.initElements(finder, pageObject);
    }
}
