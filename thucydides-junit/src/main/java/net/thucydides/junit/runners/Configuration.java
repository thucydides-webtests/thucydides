package net.thucydides.junit.runners;

import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.UnsupportedDriverException;

/**
 * Centralized configuration of the test runner.
 * Most configuration elements can be set using system properties.
 *
 */
public class Configuration {

    /**
     * Use the 'webdriver.driver' property to tell Thucydides what browser to run the tests in.
     */
    public static final String WEBDRIVER_DRIVER = "webdriver.driver";

    /**
     * The default browser is Firefox.
     */
    public static final String DEFAULT_WEBDRIVER_DRIVER = "firefox";
    
    /**
     * Get the currently-configured browser type.
     */
    public SupportedWebDriver findDriverType() {        
        String driverType = System.getProperty(WEBDRIVER_DRIVER, DEFAULT_WEBDRIVER_DRIVER);
        return lookupSupportedDriverTypeFor(driverType);
    }

    /**
     * Transform a driver type into the SupportedWebDriver enum. Driver type can
     * be any case.
     * 
     * @throws UnsupportedDriverException
     */
    private SupportedWebDriver lookupSupportedDriverTypeFor(final String driverType) {
        SupportedWebDriver driver = null;
        try {
            driver = SupportedWebDriver.valueOf(driverType.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throwUnsupportedDriverExceptionFor(driverType);
        }
        return driver;
    } 
    
    private void throwUnsupportedDriverExceptionFor(final String driverType) {
        throw new UnsupportedDriverException(driverType
                + " is not a supported browser. Supported driver values are: "
                + SupportedWebDriver.listOfSupportedDrivers());
    }

    
}
