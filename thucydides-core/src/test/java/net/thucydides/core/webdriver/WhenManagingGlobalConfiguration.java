package net.thucydides.core.webdriver;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenManagingGlobalConfiguration {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();


    @Test
    public void the_step_delay_value_can_be_defined_in_a_system_property() {
        System.setProperty("thucycides.step.delay", "1000");

        assertThat(Configuration.getStepDelay(), is(1000));
    }

    @Test
    public void there_is_no_step_delay_by_default() {
        assertThat(Configuration.getStepDelay(), is(0));
    }

    @Test
    public void the_unique_browser_value_can_be_defined_in_a_system_property() {
        System.setProperty("thucydides.use.unique.browser", "true");

        assertThat(Configuration.getUseUniqueBrowser(), is(true));
    }

   @Test
    public void the_default_unique_browser_value_should_be_false() {
        assertThat(Configuration.getUseUniqueBrowser(), is(false));
    }
}
