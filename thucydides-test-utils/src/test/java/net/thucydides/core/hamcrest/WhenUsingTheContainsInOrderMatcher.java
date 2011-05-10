package net.thucydides.core.hamcrest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class WhenUsingTheContainsInOrderMatcher {

    @Test
    public void should_match_list_of_strings_respecting_the_order() {
        assertThat(Arrays.asList("a","b","c"), containsInOrder("a","b","c"));
    }

    @Test
    public void should_not_match_list_of_strings_that_does_not_respect_the_order() {
        assertThat(Arrays.asList("c","b","a"), not(containsInOrder("a", "b", "c")));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_display_expected_list_in_error_message() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(containsString("an ordered list containing [a, b, c]"));
        assertThat(Arrays.asList("c", "b", "a"), containsInOrder("a", "b", "c"));
    }

}
