package net.thucydides.core.pages;

import static net.thucydides.core.ThucydidesSystemProperty.BASE_URL;

/**
 * Keeps track of global configuration for the page objects, such as the application default URL.
 */
public class PageConfiguration {

    private static PageConfiguration pageConfiguration;

    /**
     * The current system page configuration.
     */
    public static PageConfiguration getCurrentConfiguration() {
        if (pageConfiguration == null) {
            pageConfiguration = new PageConfiguration();
        }

        return pageConfiguration;
    }

    private String defaultBaseUrl;

    /**
     * Override the default base URL manually.
     * Normally only needed for testing.
     */
    protected void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public String getBaseUrl() {
        String systemDefinedBaseUrl = System.getProperty(BASE_URL.getPropertyName());
        if (systemDefinedBaseUrl != null) {
            return systemDefinedBaseUrl;
        } else {
            return defaultBaseUrl;
        }
    }
}
