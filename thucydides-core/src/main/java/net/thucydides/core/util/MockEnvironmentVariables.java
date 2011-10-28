package net.thucydides.core.util;

import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class MockEnvironmentVariables implements EnvironmentVariables {

    private Properties properties = new Properties();
    private Properties values = new Properties();

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

    public void setValue(String name, String value) {
        values.setProperty(name, value);
    }

}
