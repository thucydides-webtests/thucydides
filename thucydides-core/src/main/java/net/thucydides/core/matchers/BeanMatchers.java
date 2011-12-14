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


}
