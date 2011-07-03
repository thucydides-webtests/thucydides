package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;

import java.io.File;
import java.util.Locale;

/**
 * Centralized configuration of the test runner. You can configure the output
 * directory, the browser to use, and the reports to generate. Most
 * configuration elements can be set using system properties.
 * 
 */
public class Configuration {

    /**
     * Use the 'webdriver.driver' property to tell Thucydides what browser to
     * run the tests in.
     */
    public static final String WEBDRIVER_DRIVER = ThucydidesSystemProperty.DRIVER.getPropertyName();

    /**
     * The default browser is Firefox.
     */
    public static final String DEFAULT_WEBDRIVER_DRIVER = "firefox";

    /**
     * Default timeout when waiting for AJAX elements in pages, in milliseconds.
     */
    public static final int DEFAULT_ELEMENT_TIMEOUT_SECONDS = 5;

    /**
     * Use this property to define the output directory in which reports will be
     * stored.
     */
    public static final String OUTPUT_DIRECTORY_PROPERTY = ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName();

    /**
     * By default, reports will go here.
     */
    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/thucydides";

    /**
     * HTML and XML reports will be generated in this directory.
     */
    private File outputDirectory;    

    /**
     * Get the currently-configured browser type.
     */
    public static SupportedWebDriver getDriverType() {
        String driverType = System.getProperty(WEBDRIVER_DRIVER, DEFAULT_WEBDRIVER_DRIVER);
        return lookupSupportedDriverTypeFor(driverType);
    }

    /**
     * Where should the reports go?
     */
    public static File loadOutputDirectoryFromSystemProperties() {
        String systemDefinedDirectory = System.getProperty(OUTPUT_DIRECTORY_PROPERTY);
        if (systemDefinedDirectory == null) {
            systemDefinedDirectory = DEFAULT_OUTPUT_DIRECTORY;
        }
        return new File(systemDefinedDirectory);
    }

    public static int getStepDelay() {
        int stepDelay = 0;

        String stepDelayValue = System.getProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName());
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            stepDelay = Integer.valueOf(stepDelayValue);
        }
        return stepDelay;

    }

    public static int getElementTimeout() {
        int elementTimeout = DEFAULT_ELEMENT_TIMEOUT_SECONDS;

        String stepDelayValue = System.getProperty(ThucydidesSystemProperty.ELEMENT_TIMEOUT.getPropertyName());
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            elementTimeout = Integer.valueOf(stepDelayValue);
        }
        return elementTimeout;

    }

    public static boolean getUseUniqueBrowser() {
        boolean uniqueBrowser = false;
        String uniqueBrowserValue = System.getProperty(ThucydidesSystemProperty.UNIQUE_BROWSER.getPropertyName());
        if (uniqueBrowserValue != null) {
            uniqueBrowser = Boolean.valueOf(uniqueBrowserValue);
        }
        return uniqueBrowser;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * The output directory is where the test runner writes the XML and HTML
     * reports to. By default, it will be in 'target/thucydides', but you can
     * override this value either programmatically or by providing a value in
     * the <b>thucydides.output.dir</b> system property.
     * 
     */
    public File getOutputDirectory() {
        outputDirectory = loadOutputDirectoryFromSystemProperties();
        outputDirectory.mkdirs();
        return outputDirectory;
    }


    
    /**
     * Transform a driver type into the SupportedWebDriver enum. Driver type can
     * be any case.
     * 
     * @throws UnsupportedDriverException
     */
    private static SupportedWebDriver lookupSupportedDriverTypeFor(final String driverType) {
        SupportedWebDriver driver = null;
        try {
            driver = SupportedWebDriver.valueOf(driverType.toUpperCase(Locale.getDefault()));
        } catch (IllegalArgumentException iae) {
            throwUnsupportedDriverExceptionFor(driverType);
        }
        return driver;
    }

    private static void throwUnsupportedDriverExceptionFor(final String driverType) {
        throw new UnsupportedDriverException(driverType
                + " is not a supported browser. Supported driver values are: "
                + SupportedWebDriver.listOfSupportedDrivers());
    }

}
