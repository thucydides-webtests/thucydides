package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

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
        environmentVariables.setProperty("thucydides.restart.browser.frequency","5");

        assertThat(configuration.getRestartFrequency(), is(5));
    }



    @Test
    public void there_is_no_step_delay_by_default() {
        assertThat(configuration.getStepDelay(), is(0));
    }

    @Test
    public void the_unique_browser_value_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("thucydides.use.unique.browser","true");

        assertThat(configuration.getUseUniqueBrowser(), is(true));
    }

   @Test
    public void the_default_unique_browser_value_should_be_false() {
        assertThat(configuration.getUseUniqueBrowser(), is(false));
    }
}
