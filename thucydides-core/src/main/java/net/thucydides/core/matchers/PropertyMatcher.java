package net.thucydides.core.matchers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.filter;

public class PropertyMatcher {
    private final String fieldName;
    private final Matcher<String> matcher;

    protected PropertyMatcher(String fieldName, Matcher<String> matcher) {
        this.fieldName = fieldName;
        this.matcher = matcher;
    }

    public <T> boolean matches(final T bean) {
        String fieldValue;
        try {
            fieldValue = BeanUtils.getProperty(bean, fieldName);
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
        String htmlFriendlyMatcherDescription = matcher.toString().replaceAll("\"","'");
        return fieldName + " " + htmlFriendlyMatcherDescription;
    }

    public static PropertyMatcher the(final String fieldName, final Matcher<String> matcher) {
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

        List<String> propertyTerms = new ArrayList<String>();
        try {
            for(PropertyDescriptor descriptor : propertiesOf(bean)) {
                Method getter = descriptor.getReadMethod();
                if (getter != null) {
                    propertyTerms.add(propertyDescription(descriptor.getDisplayName(), bean, getter));
                }
            }
            return StringUtils.join(propertyTerms);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not read bean properties", e);
        }
    }

    private static <T> PropertyDescriptor[] propertiesOf(T bean) throws IntrospectionException {
        return Introspector.getBeanInfo(bean.getClass(), Object.class)
                                                              .getPropertyDescriptors();
    }

    private static <T> String propertyDescription(String name, T bean, Method getter)
            throws InvocationTargetException, IllegalAccessException {
        return name + " = '" + getter.invoke(bean) + "'";
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