package net.thucydides.core.matchers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.join;

public class PropertyMatcher {
    private final String fieldName;
    private final Matcher<? extends Object> matcher;

    protected PropertyMatcher(String fieldName, Matcher<? extends Object> matcher) {
        this.fieldName = fieldName;
        this.matcher = matcher;
    }

    public <T> boolean matches(final T bean) {
        Object fieldValue;
        try {

            fieldValue = PropertyUtils.getProperty(bean, fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
        return matcher.matches(fieldValue);
    }

    public <T> Matcher<T> getMatcher() {
        return new InstantiatedPropertyMatcher<T>(this);
    }

    @Override
    public String toString() {
        String matcherDescription = matcher.toString();
        String htmlFriendlyMatcherDescription
                = (matcherDescription != null) ? matcherDescription.replaceAll("\"", "'") : "";
        return fieldName + " " + htmlFriendlyMatcherDescription;
    }

    public static PropertyMatcher the(final String fieldName, final Matcher<? extends Object> matcher) {
        return new PropertyMatcher(fieldName, matcher);
    }

    public static <T> boolean matches(List<T> elements, PropertyMatcher... matchers) {
        return !filterElements(elements, matchers).isEmpty();
    }

    public static <T> void shouldMatch(List<T> items, PropertyMatcher... matchers) {
        if (!matches(items, matchers)) {
            throw new AssertionError("Failed to find matching entries for " + Arrays.toString(matchers));
        }
    }

    public static <T> void shouldMatch(T bean, PropertyMatcher... matchers) {
        if (!matches(bean, matchers)) {
            throw new AssertionError("Expected " + Arrays.toString(matchers) +
                                     " but was " + descriptionOf(bean));


        }
    }

    private static String descriptionOf(Object bean) {

        if (isAMap(bean)) {
            return mapDescription((Map<String, ? extends Object>) bean);
        } else {
            return beanDescription(bean);
        }
    }

    private static String beanDescription(Object bean) {
        List<String> propertyTerms = new ArrayList<String>();
        try {
            for(PropertyDescriptor descriptor : propertiesOf(bean)) {
                Method getter = descriptor.getReadMethod();
                if (getter != null) {
                    propertyTerms.add(propertyValueOf(descriptor.getDisplayName(), getter.invoke(bean).toString()));
                }
            }
            return join(propertyTerms);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not read bean properties", e);
        }
    }

    private static String mapDescription(Map<String, ? extends Object> map) {
        List<String> propertyTerms = new ArrayList<String>();

        for (String key : map.keySet()) {
            propertyTerms.add(propertyValueOf(key, map.get(key).toString()));
        }
        return join(propertyTerms);
    }

    private static String propertyValueOf(String propertyName, String value) {
        return propertyName + " = '" + value + "'";
    }

    private static boolean isAMap(Object bean) {
        return Map.class.isAssignableFrom(bean.getClass());
    }

    private static <T> PropertyDescriptor[] propertiesOf(T bean) throws IntrospectionException {
        return Introspector.getBeanInfo(bean.getClass(), Object.class)
                                                              .getPropertyDescriptors();
    }

    public static <T> boolean matches(T bean, PropertyMatcher... matchers) {
        return matches(Arrays.asList(bean), matchers);
    }

    public static <T> List<T> filterElements(final List<T> elements, final PropertyMatcher... matchers) {
        List<T> filteredItems = ImmutableList.copyOf(elements);
        for(PropertyMatcher matcher : matchers) {
            filteredItems = filter(matcher.getMatcher(), filteredItems);
        }
        return filteredItems;
    }
}