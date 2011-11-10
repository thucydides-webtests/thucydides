package net.thucydides.core.webdriver;

import com.google.common.base.Joiner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * The list of supported web drivers.
 * These are the drivers that support screenshots. Note that
 * Internet Explorer does not currenty support screenshots.
 *
 */
public enum SupportedWebDriver {
    /**
     * Firefox WebDriver driver.
     */
    FIREFOX(FirefoxDriver.class),
    
    /**
     * Chrome  WebDriver driver.
     */
    CHROME(ChromeDriver.class),

    /**
     * Not sure about this one...
     */
    IEXPLORER(InternetExplorerDriver.class);

    private final Class<? extends WebDriver> webdriverClass;

    private SupportedWebDriver(Class<? extends WebDriver> webdriverClass) {
        this.webdriverClass = webdriverClass;
    }

    public Class<? extends WebDriver> getWebdriverClass() {
        return webdriverClass;
    }
    /**
     * HTMLUnit - mainly for testing, as this driver does not support screenshots or much AJAX.
     */
    public static String listOfSupportedDrivers() {
        return Joiner.on(", ").join(SupportedWebDriver.values());
    }

    public static SupportedWebDriver getDriverTypeFor(final String value) throws UnsupportedDriverException {
        try {
            return SupportedWebDriver.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedDriverException("Unsupported browser type: " + value, e);
        }

    }
}