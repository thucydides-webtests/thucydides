package net.thucydides.core.matchers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.beanutils.BeanUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import static ch.lambdaj.Lambda.filter;

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

    public Matcher<Object> getMatcher() {
        return new InstantiatedPropertyMatcher(this);
    }

    @Override
    public String toString() {
        String htmlFriendlyMatcherDescription = matcher.toString().replaceAll("\"","'");
        return fieldName + " " + htmlFriendlyMatcherDescription;
    }

    public static PropertyMatcher the(final String fieldName, final Matcher<String> matcher) {
        return new PropertyMatcher(fieldName, matcher);
    }

    public static void shouldMatch(List<? extends Object> items, PropertyMatcher... matchers) {
        List<? extends Object> filteredItems = ImmutableList.copyOf(items);
        for(PropertyMatcher matcher : matchers) {
            filteredItems = filter(matcher.getMatcher(), filteredItems);
            if (filteredItems.isEmpty()) {
                throw new AssertionError("Failed to find matching entries for " + matcher);
            }
        }
    }

    public static boolean matches(List<? extends Object> items, PropertyMatcher... matchers) {
        try {
            shouldMatch(items, matchers);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
}