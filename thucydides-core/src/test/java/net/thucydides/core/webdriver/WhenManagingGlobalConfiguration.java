package net.thucydides.core.webdriver;

import static net.thucydides.core.util.FileSeparatorUtil.changeSeparatorIfRequired;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

public class WhenManagingGlobalConfiguration {

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void the_step_delay_value_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("thucycides.step.delay","1000");

        assertThat(configuration.getStepDelay(), is(1000));
    }

    @Test
    public void the_browser_restart_value_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("thucydides.restart.browser.frequency", "5");

        assertThat(configuration.getRestartFrequency(), is(5));
    }



    @Test
    public void there_is_no_step_delay_by_default() {
        assertThat(configuration.getStepDelay(), is(0));
    }

    @Test
    public void the_unique_browser_value_can_be_defined_in_a_system_property() {
        String outputDirectory = changeSeparatorIfRequired("build/reports/thucydides");
        environmentVariables.setProperty("thucydides.outputDirectory",outputDirectory);

        assertThat(configuration.getOutputDirectory().getAbsoluteFile().toString(), endsWith(outputDirectory));
    }

    @Test
    public void the_output_directory_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("thucydides.use.unique.browser","true");

        assertThat(configuration.getUseUniqueBrowser(), is(true));
    }

    @Test
    public void the_default_unique_browser_value_should_be_false() {
        assertThat(configuration.getUseUniqueBrowser(), is(false));
    }
}
