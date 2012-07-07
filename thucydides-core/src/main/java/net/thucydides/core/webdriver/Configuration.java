package net.thucydides.core.webdriver;

import java.io.File;

public interface Configuration {
    SupportedWebDriver getDriverType();

    File loadOutputDirectoryFromSystemProperties();

    int getStepDelay();

    int getElementTimeout();

    boolean getUseUniqueBrowser();

    void setOutputDirectory(File outputDirectory);

    File getOutputDirectory();

    double getEstimatedAverageStepCount();

    boolean onlySaveFailingScreenshots();

    void setDefaultBaseUrl(final String defaultBaseUrl);

    int getRestartFrequency();
    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    String getBaseUrl();

    boolean takeVerboseScreenshots();

    void setIfUndefined(String property, String value);
}
