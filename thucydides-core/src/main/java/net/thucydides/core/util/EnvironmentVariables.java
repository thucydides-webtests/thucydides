package net.thucydides.core.util;

/**
 * Return system environment variable values.
 */
public interface EnvironmentVariables {

    public String getValue(final String name);

    public String getValue(final String name, final String defaultValue);

    Integer getPropertyAsInteger(final String name, final Integer defaultValue);

    Boolean getPropertyAsBoolean(final String name, boolean defaultValue);

    public String getProperty(final String name);

    public String getProperty(final String name, final String defaultValue);

}
