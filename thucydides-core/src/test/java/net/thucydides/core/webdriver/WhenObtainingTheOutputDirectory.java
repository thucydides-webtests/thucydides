package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WhenObtainingTheOutputDirectory {

    SystemPropertiesConfiguration configuration;
    EnvironmentVariables environmentVariables;

    @Before
    public void setupEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void the_default_output_directory_is_in_the_default_maven_site_directory() {
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();

        assertThat(outputDirectory.getPath(), is("target/site/thucydides"));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_if_the_maven_output_directory_is_overridden() {
        environmentVariables.setProperty("project.build.directory","build");
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();

        assertThat(outputDirectory.getPath(), is("build/site/thucydides"));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_if_the_maven_site_output_directory_is_overridden() {
        environmentVariables.setProperty("project.reporting.OutputDirectory","custom-reports-directory");
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();

        assertThat(outputDirectory.getPath(), is("custom-reports-directory/thucydides"));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_using_a_thucydides_system_property() {
        environmentVariables.setProperty("thucydides.outputDirectory","thucydides-reports");
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();

        assertThat(outputDirectory.getPath(), is("thucydides-reports"));
    }

    @Test
    public void the_thucydides_system_property_always_takes_priority() {
        environmentVariables.setProperty("project.build.directory","build");
        environmentVariables.setProperty("project.reporting.OutputDirectory","custom-reports-directory");
        environmentVariables.setProperty("thucydides.outputDirectory","thucydides-reports");
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();

        assertThat(outputDirectory.getPath(), is("thucydides-reports"));
    }

}
