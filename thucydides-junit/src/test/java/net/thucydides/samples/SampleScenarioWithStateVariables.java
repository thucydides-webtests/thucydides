package net.thucydides.samples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ThucydidesRunner.class)
public class SampleScenarioWithStateVariables {
       
    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void joes_test() throws Throwable {
        steps.store_name("joe");
        assertThat(steps.get_name(), is("joe"));
    }

    @Test
    public void jills_test() throws Throwable {
        steps.store_name("jill");
        assertThat(steps.get_name(), is("jill"));
    }

    @Test
    public void no_ones_test() throws Throwable {
        assertThat(steps.hasName(), is(false));
    }
}
