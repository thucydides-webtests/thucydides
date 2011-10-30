package net.thucydides.core.pages;

import static net.thucydides.core.ThucydidesSystemProperty.BASE_URL;

/**
 * Keeps track of global configuration for the page objects, such as the application default URL.
 */
public interface PageConfiguration {

    public void setDefaultBaseUrl(final String defaultBaseUrl);

    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public String getBaseUrl();
}
