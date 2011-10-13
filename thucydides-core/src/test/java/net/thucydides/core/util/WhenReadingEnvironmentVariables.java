package net.thucydides.core.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class WhenReadingEnvironmentVariables {

    @Test
    public void should_read_environment_variable_from_system() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("PATH");
        assertThat(value, is(not(nullValue())));
    }

    @Test
    public void should_return_null_for_inexistant_environment_variable() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("DOES_NOT_EXIST");
        assertThat(value, is(nullValue()));
    }

    @Test
    public void should_return_default_for_inexistant_environment_variable_if_specified() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("DOES_NOT_EXIST","DEFAULT");
        assertThat(value, is("DEFAULT"));
    }

}
