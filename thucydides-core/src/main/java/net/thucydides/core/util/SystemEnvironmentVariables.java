package net.thucydides.core.util;

import org.apache.commons.lang.StringUtils;

/**
 * Return system environment variable values.
 */
public class SystemEnvironmentVariables implements EnvironmentVariables {

    public String getValue(final String name) {
        return getValue(name, null);
    }

    @Override
    public String getValue(Enum<?> property) {
        return getValue(property.toString());
    }

    public String getValue(final String name, final String defaultValue) {
        String value = System.getenv(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public String getValue(Enum<?> property, String defaultValue) {
        return getValue(property.toString(), defaultValue);
    }

    public Integer getPropertyAsInteger(String property, Integer defaultValue) {
        String value = System.getProperty(property);
        if (value != null) {
            return Integer.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    @Override
    public Integer getPropertyAsInteger(Enum<?> property, Integer defaultValue) {
        return getPropertyAsInteger(property.toString(), defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, boolean defaultValue) {
        if (System.getProperty(name) == null) {
            return defaultValue;
        } else if (StringUtils.isBlank(System.getProperty(name))) {
            return true;
        } else {
            return Boolean.parseBoolean(System.getProperty(name,"false"));
        }
    }

    @Override
    public Boolean getPropertyAsBoolean(Enum<?> property, boolean defaultValue) {
        return getPropertyAsBoolean(property.toString(), defaultValue);
    }

    public String getProperty(final String name) {
        return System.getProperty(name);
    }

    @Override
    public String getProperty(Enum<?> property) {
        return getProperty(property.toString());
    }

    public String getProperty(final String name, final String defaultValue) {
        return System.getProperty(name, defaultValue);
    }

    @Override
    public String getProperty(Enum<?> property, String defaultValue) {
        return getProperty(property.toString(), defaultValue);
    }

    @Override
    public void setProperty(String name, String value) {
        System.setProperty(name, value);
    }

    @Override
    public void clearProperty(String name) {
        System.clearProperty(name);
    }
}
