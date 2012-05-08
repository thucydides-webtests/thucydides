package net.thucydides.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class MockEnvironmentVariables implements EnvironmentVariables {

    private Properties properties = new Properties();
    private Properties values = new Properties();

    public MockEnvironmentVariables() {
        this.properties.setProperty("user.home", System.getProperty("user.home"));
    }

    protected MockEnvironmentVariables(Properties properties) {
        this.properties = new Properties(properties);
    }

    public static EnvironmentVariables fromSystemEnvironment() {
        return new MockEnvironmentVariables(System.getProperties());
    }
    
    public boolean propertySetIsEmpty() {
        return properties.isEmpty();
    }

    public String getValue(String name) {
        return values.getProperty(name);
    }

    @Override
    public String getValue(Enum<?> property) {
        return getValue(property.toString());
    }

    public String getValue(String name, String defaultValue) {
        return values.getProperty(name, defaultValue);
    }

    @Override
    public String getValue(Enum<?> property, String defaultValue) {
        return getValue(property.toString(), defaultValue);
    }

    public Integer getPropertyAsInteger(String name, Integer defaultValue) {
        String value = (String) properties.get(name);
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(properties.getProperty(name));
        } else {
            return defaultValue;
        }
    }

    @Override
    public Integer getPropertyAsInteger(Enum<?> property, Integer defaultValue) {
        return getPropertyAsInteger(property.toString(), defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, boolean defaultValue) {
        if (properties.getProperty(name) == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(properties.getProperty(name,"false"));
        }
    }

    @Override
    public Boolean getPropertyAsBoolean(Enum<?> property, boolean defaultValue) {
        return getPropertyAsBoolean(property.toString(), defaultValue);
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public String getProperty(Enum<?> property) {
        return getProperty(property.toString());
    }

    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    @Override
    public String getProperty(Enum<?> property, String defaultValue) {
        return getProperty(property.toString(), defaultValue);
    }

    public void setProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    @Override
    public void clearProperty(String name) {
        properties.remove(name);
    }

    public void setValue(String name, String value) {
        values.setProperty(name, value);
    }

}
