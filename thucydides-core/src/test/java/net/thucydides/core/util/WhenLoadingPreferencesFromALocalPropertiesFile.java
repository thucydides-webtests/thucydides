package net.thucydides.core.util;

import net.thucydides.core.Thucydides;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenLoadingPreferencesFromALocalPropertiesFile {

    @Rule
    public TemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    File homeDirectory;
    File thucydidesPropertiesFile;
    EnvironmentVariables environmentVariables;
    PropertiesFileLocalPreferences localPreferences;
    
    @Before
    public void setupDirectories() throws IOException {
        environmentVariables = new MockEnvironmentVariables();
        localPreferences = new PropertiesFileLocalPreferences(environmentVariables);

        homeDirectory = temporaryFolder.newFolder();
        localPreferences.setHomeDirectory(homeDirectory);
    }

    @Test
    public void the_default_preferences_directory_is_the_users_home_directory() throws Exception {
        PropertiesFileLocalPreferences localPreferences = new PropertiesFileLocalPreferences(environmentVariables);
        
        String homeDirectory = System.getProperty("user.home");

        assertThat(localPreferences.getHomeDirectory().getPath(), is(homeDirectory));
    }

    @Test
    public void should_load_property_values_from_local_preferences() throws Exception {
        writeToPropertiesFile("webdriver.driver = opera");

        localPreferences.setHomeDirectory(homeDirectory);
        
        localPreferences.loadPreferences();

        assertThat(environmentVariables.getProperty("webdriver.driver"), is("opera"));
    }

    @Test
    public void local_preferences_should_not_override_system_preferences() throws Exception {
        writeToPropertiesFile("webdriver.driver = opera");

        environmentVariables.setProperty("webdriver.driver", "iexplorer");
        localPreferences.setHomeDirectory(homeDirectory);

        localPreferences.loadPreferences();

        assertThat(environmentVariables.getProperty("webdriver.driver"), is("iexplorer"));
    }

    @Test
    public void local_preferences_are_instantiated_using_guice() throws IOException {
        Thucydides.loadLocalPreferences();

    }
    private void writeToPropertiesFile(String... lines) throws IOException {
        thucydidesPropertiesFile = new File(homeDirectory, "thucydides.properties");
        thucydidesPropertiesFile.setReadable(true);
        thucydidesPropertiesFile.setWritable(true);
        thucydidesPropertiesFile.setExecutable(true);
        
        try {
        	thucydidesPropertiesFile.createNewFile();
        } catch (IOException e) {
        	System.err.println(e);
		}
        FileWriter outFile = new FileWriter(thucydidesPropertiesFile);
        PrintWriter out = new PrintWriter(outFile);
        for(String line : lines) {
            out.println(line);
        }
        out.close();
    }
}
