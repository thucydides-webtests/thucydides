package net.thucydides.core.util;

import net.thucydides.core.ThucydidesSystemProperty;

/**
 * Return system environment variable values.
 */
public interface EnvironmentVariables {

    public String getValue(final String name);

    public String getValue(final String name, final String defaultValue);

    Integer getIntegerValue(final String name, final Integer defaultValue);

    public String getProperty(final String name);

    public String getProperty(final String name, final String defaultValue);

}
