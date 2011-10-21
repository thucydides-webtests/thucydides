package net.thucydides.core.util;

import net.thucydides.core.ThucydidesSystemProperty;

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

    public Integer getIntegerValue(String property, Integer defaultValue) {
        String value = System.getProperty(property);
        if (value != null) {
            return Integer.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    public Boolean getBooleanValue(String name, boolean defaultValue) {
        if (System.getProperty(name) == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(System.getProperty(name,"false"));
        }
    }

    public String getProperty(final String name) {
        return System.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return System.getProperty(name, defaultValue);
    }

}
