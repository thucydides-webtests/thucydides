package net.thucydides.core.util;

import java.util.Map;

/**
 * Return system environment variable values.
 */
public interface EnvironmentVariables {

    public String getValue(final String name);

    public String getValue(final String name, final String defaultValue);
}
