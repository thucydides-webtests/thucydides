package net.thucydides.core.matchers;

import org.hamcrest.Matcher;

public class BeanPropertyMatcher implements BeanFieldMatcher {
    private final String fieldName;
    private final Matcher<? extends Object> matcher;

    protected BeanPropertyMatcher(String fieldName, Matcher<? extends Object> matcher) {
        this.fieldName = fieldName;
        this.matcher = matcher;
    }

    @Override
    public <T> boolean matches(final T bean) {
        return matcher.matches(BeanMatchers.getFieldValue(bean, fieldName));
    }

    @Override
    public <T> Matcher<T> getMatcher() {
        return new InstantiatedBeanMatcher<T>(this);
    }

    @Override
    public String toString() {
        String matcherDescription = matcher.toString();
        String htmlFriendlyMatcherDescription
                = (matcherDescription != null) ? matcherDescription.replaceAll("\"", "'") : "";
        return fieldName + " " + htmlFriendlyMatcherDescription;
    }
}