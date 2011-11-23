package net.thucydides.core.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class InstantiatedPropertyMatcher extends TypeSafeMatcher<Object> {

    private final PropertyMatcher propertyMatcher;

    public InstantiatedPropertyMatcher(final PropertyMatcher propertyMatcher) {
        this.propertyMatcher = propertyMatcher;
    }

    @Override
    public boolean matchesSafely(Object bean) {
        return propertyMatcher.matches(bean);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(propertyMatcher.toString());
    }
}
