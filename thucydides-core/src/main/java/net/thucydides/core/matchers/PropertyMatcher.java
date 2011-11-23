package net.thucydides.core.matchers;

import org.apache.commons.beanutils.BeanUtils;
import org.hamcrest.Matcher;

public class PropertyMatcher {
    private final String fieldName;
    private final Matcher<String> matcher;

    protected PropertyMatcher(String fieldName, Matcher<String> matcher) {
        this.fieldName = fieldName;
        this.matcher = matcher;
    }

    public boolean matches(final Object bean) {
        String fieldValue = null;
        try {
            fieldValue = BeanUtils.getProperty(bean, fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
        return matcher.matches(fieldValue);
    }

    public Matcher<String> getMatcher() {
        return null;
    }

    @Override
    public String toString() {
        return fieldName + " " + matcher;
    }

    public static PropertyMatcher the(final String fieldName, final Matcher<String> matcher) {
        return new PropertyMatcher(fieldName, matcher);
    }
}