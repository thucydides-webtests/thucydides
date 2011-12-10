package net.thucydides.core.matchers;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ListUtils;
import org.hamcrest.Matcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.join;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BeanMatchers {

    private static final String NEW_LINE = System.getProperty("line.separator");
    
    public static BeanMatcher the(final String fieldName, final Matcher<? extends Object> matcher) {
        return new BeanPropertyMatcher(fieldName, matcher);
    }

    public static BeanMatcher the_count(Matcher<Integer> countMatcher) {
        return new BeanCountMatcher(countMatcher);
    }

    public static BeanConstraint each(final String fieldName) {
        return new BeanConstraint(fieldName);
    }

    public static BeanMatcher max(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        return new MaxFieldValueMatcher(fieldName, valueMatcher);
    }

    public static BeanMatcher min(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        return new MinFieldValueMatcher(fieldName, valueMatcher);
    }

    public static class BeanConstraint {
        private final String fieldName;

        public BeanConstraint(String fieldName) {
            this.fieldName = fieldName;
        }
        
        public BeanMatcher isDifferent() {
            return new BeanUniquenessMatcher(fieldName);
        }
    }
    
    protected static Object getFieldValue(Object bean, String fieldName) {
        try {
            return PropertyUtils.getProperty(bean, fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }        
    }    
    
    public static <T> boolean matches(List<T> elements, BeanMatcher... matchers) {
        List<T> filteredElements = filterElements(elements, matchers);
        return apply(filteredElements, collectionMatchersIn(matchers));
    }

    private static <T> boolean apply(final List<T> elements, final List<BeanCollectionMatcher> matchers) {
        List<BeanCollectionMatcher> collectionMatchers = addEmptyTestIfNoCountChecksArePresentTo(matchers);
        
        for (BeanCollectionMatcher matcher : collectionMatchers) {
            if (!matcher.matches(elements)) {
                return false;
            }
        }
        return true;
    }

    private static List<BeanCollectionMatcher> addEmptyTestIfNoCountChecksArePresentTo(final List<BeanCollectionMatcher> matchers) {
        if (hasEntry(matchers,instanceOf(BeanCountMatcher.class)).matches(matchers)) {
            return matchers;
        } else {
            return ListUtils.union(matchers, Arrays.asList(the_count(is(not(0)))));
        }
    }

    private static List<BeanCollectionMatcher> collectionMatchersIn(final BeanMatcher[] matchers) {
        List<BeanMatcher> compatibleMatchers = Lambda.filter(instanceOf(BeanCollectionMatcher.class), matchers);
        return convert(compatibleMatchers, toBeanCollectionMatchers());
    }

    private static Converter<BeanMatcher, BeanCollectionMatcher> toBeanCollectionMatchers() {
        return new Converter<BeanMatcher, BeanCollectionMatcher>() {
            @Override
            public BeanCollectionMatcher convert(BeanMatcher from) {
                return (BeanCollectionMatcher) from; 
            }
        };
    }

    public static <T> List<T> filterElements(final List<T> elements, final BeanMatcher... matchers) {
        List<T> filteredItems = ImmutableList.copyOf(elements);

        for(BeanFieldMatcher matcher : propertyMatchersIn(matchers)) {
            filteredItems = filter(matcher.getMatcher(), filteredItems);
        }

        return filteredItems;
    }

    private static List<BeanFieldMatcher> propertyMatchersIn(BeanMatcher[] matchers) {
        List<BeanMatcher> compatibleMatchers = Lambda.filter(instanceOf(BeanFieldMatcher.class), matchers);
        return convert(compatibleMatchers, toBeanFieldMatchers());
    }

    private static Converter<BeanMatcher, BeanFieldMatcher> toBeanFieldMatchers() {
        return new Converter<BeanMatcher, BeanFieldMatcher>() {
            @Override
            public BeanFieldMatcher convert(BeanMatcher from) {
                return (BeanFieldMatcher) from;
            }
        };
    }

    public static <T> void shouldMatch(List<T> items, BeanMatcher... matchers) {
        if (!matches(items, matchers)) {
            throw new AssertionError("Failed to find matching elements for " + Arrays.toString(matchers)
                                     + NEW_LINE
                                     +"Elements where " + items);
        }
    }

    public static <T> void shouldMatch(T bean, BeanMatcher... matchers) {
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

    private static boolean isAMap(Object bean) {
        return Map.class.isAssignableFrom(bean.getClass());
    }

    public static <T> boolean matches(T bean, BeanMatcher... matchers) {
        return matches(Arrays.asList(bean), matchers);
    }

    private static String propertyValueOf(String propertyName, String value) {
        return propertyName + " = '" + value + "'";
    }

    private static <T> PropertyDescriptor[] propertiesOf(T bean) throws IntrospectionException {
        return Introspector.getBeanInfo(bean.getClass(), Object.class)
                                                              .getPropertyDescriptors();
    }

}
