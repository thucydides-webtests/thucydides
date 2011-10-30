package net.thucydides.core.pages;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.BASE_URL;

/**
 * Keeps track of global configuration for the page objects, such as the application default URL.
 */
public class SystemPropertiesPageConfiguration implements PageConfiguration {

    private final EnvironmentVariables environmentVariables;

    private String defaultBaseUrl;

    @Inject
    public SystemPropertiesPageConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    /**
     * Override the default base URL manually.
     * Normally only needed for testing.
     */
    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public String getBaseUrl() {
        return environmentVariables.getProperty(BASE_URL.getPropertyName(), defaultBaseUrl);
    }
}
