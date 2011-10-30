package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenManagingGlobalConfiguration {

    @Mock
    EnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void the_step_delay_value_can_be_defined_in_a_system_property() {
        when(environmentVariables.getProperty("thucycides.step.delay")).thenReturn("1000");

        assertThat(configuration.getStepDelay(), is(1000));
    }

    @Test
    public void there_is_no_step_delay_by_default() {
        assertThat(configuration.getStepDelay(), is(0));
    }

    @Test
    public void the_unique_browser_value_can_be_defined_in_a_system_property() {
        when(environmentVariables.getProperty("thucydides.use.unique.browser")).thenReturn("true");

        assertThat(configuration.getUseUniqueBrowser(), is(true));
    }

   @Test
    public void the_default_unique_browser_value_should_be_false() {
        assertThat(configuration.getUseUniqueBrowser(), is(false));
    }
}
