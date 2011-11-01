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

    @Test
    public void should_read_integer_system_properties_from_the_system() {
        System.setProperty("some.integer.property","10");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        int value = environmentVariables.getPropertyAsInteger("some.integer.property",5);
        assertThat(value, is(10));
    }

    @Test
    public void should_read_integer_system_properties_with_default_from_the_system() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        int value = environmentVariables.getPropertyAsInteger("some.default.integer.property",5);
        assertThat(value, is(5));
    }

    @Test
    public void should_read_system_properties_from_the_system() {
        System.setProperty("some.property","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("some.property");
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_system_properties_with_default_values_from_the_system() {
        System.setProperty("some.other.property","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("some.other.property", "DEFAULT");
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_default_system_properties_with_default_values_from_the_system() {
        System.clearProperty("another.property");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("another.property", "DEFAULT");
        assertThat(value, is("DEFAULT"));
    }

    @Test
    public void mock_environment_variables_can_be_used_for_testing_in_other_modules() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("a.property","value");
        assertThat(environmentVariables.getProperty("a.property"), is("value"));
    }

    @Test
    public void mock_environment_variables_allow_defaults() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getProperty("property","default"), is("default"));
    }

    @Test
    public void mock_environment_variables_allow_integer_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("integer.property","30");
        assertThat(environmentVariables.getPropertyAsInteger("integer.property", 0), is(30));
    }

    @Test
    public void mock_environment_variables_allow_default_integer_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getPropertyAsInteger("integer.property", 10), is(10));
    }

    @Test
    public void mock_environment_variables_allow_boolean_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("boolean.property","true");
        assertThat(environmentVariables.getPropertyAsBoolean("boolean.property", false), is(true));
    }

    @Test
    public void mock_environment_variables_allow_default_boolean_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getPropertyAsBoolean("property", true), is(true));
    }

    @Test
    public void mock_environment_variables_can_be_used_for_testing_environment_values_in_other_modules() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setValue("env","value");
        assertThat(environmentVariables.getValue("env"), is("value"));
    }

    @Test
    public void mock_environment_values_allow_defaults() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getValue("env","default"), is("default"));
    }

}
