package net.thucydides.core.util;

/**
 * Return system environment variable values.
 */
public class SystemEnvironmentVariables implements EnvironmentVariables {

    public String getValue(final String name) {
        return getValue(name, null);
    }

    public String getValue(final String name, final String defaultValue) {
        String value = System.getenv(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }
}
