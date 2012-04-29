package net.thucydides.core.matchers.dates;

import org.apache.commons.beanutils.PropertyUtils;

public class BeanFields {

    private final Object bean;

    private BeanFields(Object bean) {
        this.bean = bean;
    }

    public static BeanFields fieldValueIn(Object bean) {
        return new BeanFields(bean);
    }
    
    public Object forField(String fieldName) {
        try {
            return PropertyUtils.getProperty(bean, fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
    }

}
