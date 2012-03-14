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

    public void setDefaultBaseUrl(final String defaultBaseUrl);

    public int getRestartFrequency();
    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public String getBaseUrl();
}
