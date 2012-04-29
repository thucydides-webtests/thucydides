package net.thucydides.core.util;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Thucydides options can be loaded from the thucydides.properties file in the home directory and/or in the working directory.
 * <p/>
 * User: johnsmart
 * Date: 24/12/11
 * Time: 11:15 AM
 */
public class PropertiesFileLocalPreferences implements LocalPreferences {

    private File workingDirectory;
    private File homeDirectory;
    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileLocalPreferences.class);

    @Inject
    public PropertiesFileLocalPreferences(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.homeDirectory = new File(System.getProperty("user.home"));
        this.workingDirectory = new File(System.getProperty("user.dir"));
    }

    public File getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(File homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    @Override
    public void loadPreferences() throws IOException {
        File homeDirectoryPreferencesFile = getLocalPreferencesFile();
        File workingDirectoryPreferencesFile = getLocalWorkingPreferencesFile();
        updatePreferencesFromClasspath();
        updatePreferencesFrom(workingDirectoryPreferencesFile);
        updatePreferencesFrom(homeDirectoryPreferencesFile);
    }

    private void updatePreferencesFromClasspath() throws IOException {
        InputStream propertiesOnClasspath = getClass().getClassLoader().getResourceAsStream("/thucydides.properties");
        if (propertiesOnClasspath != null) {
            Properties localPreferences = new Properties();
            localPreferences.load(propertiesOnClasspath);
            setUndefinedSystemPropertiesFrom(localPreferences);
        }
    }

    private void updatePreferencesFrom(File preferencesFile) throws IOException {
        LOGGER.debug("Loading local Thucydides properties from " + preferencesFile.getAbsolutePath());
        if (preferencesFile.exists()) {
            Properties localPreferences = new Properties();
            localPreferences.load(new FileInputStream(preferencesFile));
            setUndefinedSystemPropertiesFrom(localPreferences);
        }
    }

    private void setUndefinedSystemPropertiesFrom(Properties localPreferences) {
        for (ThucydidesSystemProperty thucydidesProperty : ThucydidesSystemProperty.values()) {
            String propertyName = thucydidesProperty.getPropertyName();
            String localPropertyValue = localPreferences.getProperty(propertyName);
            String currentPropertyValue = environmentVariables.getProperty(propertyName);

            if ((currentPropertyValue == null) && (localPropertyValue != null)) {
                LOGGER.debug(propertyName + "=" + localPropertyValue);
                environmentVariables.setProperty(propertyName, localPropertyValue);
            }
        }
    }

    private File getLocalPreferencesFile() {
        return new File(homeDirectory, "thucydides.properties");
    }

    private File getLocalWorkingPreferencesFile() {
        return new File(workingDirectory, "thucydides.properties");
    }
}
