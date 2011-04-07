package net.thucydides.core.webdriver;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

import com.google.common.collect.ImmutableList;

import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

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
    public static final String WEBDRIVER_DRIVER = "webdriver.driver";

    /**
     * The default browser is Firefox.
     */
    public static final String DEFAULT_WEBDRIVER_DRIVER = "firefox";

    /**
     * Use this property to define the output directory in which reports will be
     * stored.
     */
    public static final String OUTPUT_DIRECTORY_PROPERTY = "thucydides.outputDirectory";

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
    public File loadOutputDirectoryFromSystemProperties() {
        String systemDefinedDirectory = System.getProperty(OUTPUT_DIRECTORY_PROPERTY);
        if (systemDefinedDirectory == null) {
            systemDefinedDirectory = DEFAULT_OUTPUT_DIRECTORY;
        }
        return new File(systemDefinedDirectory);
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
        if (outputDirectory == null) {
            outputDirectory = loadOutputDirectoryFromSystemProperties();
            outputDirectory.mkdirs();
        }
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

    /**
     * The default reporters applicable for standard test runs.
     */
    public Collection<? extends AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLAcceptanceTestReporter(),
                                new HtmlAcceptanceTestReporter());
    }

}
