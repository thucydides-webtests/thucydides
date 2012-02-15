package net.thucydides.core.matchers;

import org.hamcrest.Matcher;

import static net.thucydides.core.matchers.dates.BeanFields.fieldValueIn;

public class SimpleValueMatcher {
    private final Object value;
    private final Matcher<? extends Object> matcher;

    protected SimpleValueMatcher(Object value, Matcher<? extends Object> matcher) {
        this.value = value;
        this.matcher = matcher;
    }

    public boolean matches() {
        return matcher.matches(value);
    }

    @Override
    public String toString() {
        return matcher.toString();
    }
}