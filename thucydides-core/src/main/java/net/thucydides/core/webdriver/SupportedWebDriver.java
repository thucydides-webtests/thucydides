package net.thucydides.core.webdriver;

import com.google.common.base.Joiner;

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
    FIREFOX, 
    
    /**
     * Chrome  WebDriver driver.
     */
    CHROME;

    /**
     * HTMLUnit - mainly for testing, as this driver does not support screenshots or much AJAX.
     */
    public static String listOfSupportedDrivers() {
        return Joiner.on(", ").join(SupportedWebDriver.values());
    }
}