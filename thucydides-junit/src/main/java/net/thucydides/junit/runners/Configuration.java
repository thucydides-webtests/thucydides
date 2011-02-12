package net.thucydides.junit.runners;

import java.io.File;

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
     * Use this property to define the output directory in which reports will be stored.
     */
    public static final String OUTPUT_DIRECTORY_PROPERTY = "thucydides.outputDirectory";

    /**
     * By default, reports will go here.
     */
    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/thucydides";

    /**
     * Get the currently-configured browser type.
     */
    public SupportedWebDriver getDriverType() {        
        String driverType = System.getProperty(WEBDRIVER_DRIVER, DEFAULT_WEBDRIVER_DRIVER);
        return lookupSupportedDriverTypeFor(driverType);
    }
    
    /**
     * Where should the reports go?
     */
    public File getOutputDirectory() {
        String systemDefinedDirectory = System.getProperty(OUTPUT_DIRECTORY_PROPERTY);
        if (systemDefinedDirectory == null) {
            systemDefinedDirectory = DEFAULT_OUTPUT_DIRECTORY;
        }
        return new File(systemDefinedDirectory);
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
