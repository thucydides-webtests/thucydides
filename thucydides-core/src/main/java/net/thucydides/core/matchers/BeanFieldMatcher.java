package net.thucydides.core.matchers;

import org.hamcrest.Matcher;

public interface BeanFieldMatcher extends BeanMatcher{
    <T> boolean matches(T bean);

    <T> Matcher<T> getMatcher();
}
