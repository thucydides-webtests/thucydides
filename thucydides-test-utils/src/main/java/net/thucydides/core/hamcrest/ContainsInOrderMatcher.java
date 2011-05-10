package net.thucydides.core.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;

public class ContainsInOrderMatcher extends TypeSafeMatcher<List<String>> {

    private List<String> values;

    public ContainsInOrderMatcher(final String... values) {
        this.values = Arrays.asList(values);
    }

    public boolean matchesSafely(final List<String> orderedListOfValues) {
        return values.equals(orderedListOfValues);
    }


    public void describeTo(final Description description) {
        description.appendText("an ordered list containing ").appendText(values.toString());
    }
}
