package net.thucydides.core.webdriver.firefox;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FirefoxProfileEnhancer {

    private static final String FIREBUGS_VERSION = "1.9.0b1";
    private static final String MAX_FIREBUGS_VERSION = "999.99.0";
    private static final String FIREBUGS_XPI_FILE = "/firefox/firebug-" + FIREBUGS_VERSION + ".xpi";

    private static final String FIREFINDER_VERSION = "1.1-fx";
    private static final String MAX_FIREFINDER_VERSION = "999.9";
    private static final String FIREFINDER_XPI_FILE = "/firefox/firefinder_for_firebug-" + FIREFINDER_VERSION + ".xpi";

    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxProfileEnhancer.class);
    private final EnvironmentVariables environmentVariables;

    public FirefoxProfileEnhancer(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean shouldActivateFirebugs() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.ACTIVATE_FIREBUGS.getPropertyName(), false);
    }

    public void addFirebugsTo(final FirefoxProfile profile) {
        try {
            profile.addExtension(this.getClass(), FIREBUGS_XPI_FILE);
            profile.setPreference("extensions.firebug.currentVersion", MAX_FIREBUGS_VERSION); // Avoid startup screen

            profile.addExtension(this.getClass(), FIREFINDER_XPI_FILE);
            profile.setPreference("extensions.firebug.currentVersion", MAX_FIREFINDER_VERSION); // Avoid startup screen

        } catch (IOException e) {
            LOGGER.warn("Failed to add Firebugs extension to Firefox");
        }
    }


    public void enableNativeEventsFor(final FirefoxProfile profile) {
        profile.setEnableNativeEvents(true);
    }
}
