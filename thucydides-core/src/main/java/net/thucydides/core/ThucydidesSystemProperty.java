package net.thucydides.core;

/**
 * Properties that can be passed to a web driver test to customize its behaviour.
 * This class is mainly for internal use.
 * 
 * @author johnsmart
 *
 */
public enum ThucydidesSystemProperty {

    /**
     * The WebDriver driver - firefox or chrome.
     */
    DRIVER("webdriver.driver"),    
    
    /**
     * The default starting URL for the application, and base URL for relative paths.
     */
    BASE_URL("webdriver.base.url"),
    
    /**
     * Indicates a directory from which the resources for the HTML reports should be copied.
     * This directory currently needs to be provided in a JAR file.
     */
    REPORT_RESOURCE_PATH("thucydides.report.resources"),

    /**
     * Where should reports be generated.
     */
    OUTPUT_DIRECTORY("thucydides.outputDirectory"),

    /**
     * Should Thucydides only store screenshots for failing steps?
     * This can save disk space and speed up the tests somewhat. Useful for data-driven testing.
     */
    ONLY_SAVE_FAILING_SCREENSHOTS("thucydides.only.save.failing.screenshots"),

    /**
     * Restart the browser every so often during data-driven tests.
     */
    RESTART_BROWSER_FREQUENCY("thucydides.restart.browser.frequency"),

    /**
     * Pause (in ms) between each test step.
     */
    STEP_DELAY("thucycides.step.delay"),

    /**
     * How long should the driver wait for elements not immediately visible.
     */
    ELEMENT_TIMEOUT("thucydides.timeout"),

    /**
     * For Firefox, use a profile with the AssumeUntrustedCertificateIssuer property set.
     * This will allow Firefox to use secured web sites with invalid certificates.
     */
    UNTRUSTED_CERTIFICATES("untrusted.certificates"),

    /**
     * Use the same browser for all tests (the "Highlander" rule)
     */
    UNIQUE_BROWSER("thucydides.use.unique.browser");

    private String propertyName;

    private ThucydidesSystemProperty(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static boolean getBooleanValue(final ThucydidesSystemProperty property) {
        String value = System.getProperty(property.getPropertyName());
        if (value != null) {
            return Boolean.valueOf(value);
        } else {
            return false;
        }
    }

    public static String getValue(final ThucydidesSystemProperty property, final String defaultValue) {
        return System.getProperty(property.getPropertyName(), defaultValue);
    }

}
