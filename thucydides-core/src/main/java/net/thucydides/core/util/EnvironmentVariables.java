package net.thucydides.core.util;

/**
 * Return system environment variable values.
 */
public interface EnvironmentVariables {

    public String getValue(final String name);

    public String getValue(final String name, final String defaultValue);
}
