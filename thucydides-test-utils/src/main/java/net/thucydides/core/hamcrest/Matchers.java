package net.thucydides.core.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.List;

/**
 * General-purpose Hamcrest matchers.
 */
public class Matchers {

    @Factory
    public static Matcher<List<String>> containsInOrder(final String... values) {
        return new ContainsInOrderMatcher(values);
    }
}
