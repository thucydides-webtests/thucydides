package net.thucydides.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class MockEnvironmentVariables implements EnvironmentVariables {

    private Properties properties = new Properties();
    private Properties values = new Properties();

    public MockEnvironmentVariables() {}

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

    public String getValue(String name, String defaultValue) {
        return values.getProperty(name, defaultValue);
    }

    public Integer getPropertyAsInteger(String name, Integer defaultValue) {
        String value = (String) properties.get(name);
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(properties.getProperty(name));
        } else {
            return defaultValue;
        }
    }

    public Boolean getPropertyAsBoolean(String name, boolean defaultValue) {
        if (properties.getProperty(name) == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(properties.getProperty(name,"false"));
        }
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
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
