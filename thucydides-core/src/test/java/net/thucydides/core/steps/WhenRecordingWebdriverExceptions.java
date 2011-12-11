package net.thucydides.core.steps;

import org.junit.Test;
import org.openqa.selenium.WebDriverException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenRecordingWebdriverExceptions {

    @Test
    public void should_retrieve_error_message_from_hamcrest_assertion_error() {
        try {
            assertThat("a", is("b"));
        } catch(AssertionError error) {
            AssertionError convertedError = ErrorConvertor.forError(error).convertToAssertion();
            assertThat(convertedError.getMessage(), allOf(
                    containsString("Expected: is \"b\""),
                    containsString("but: was \"a\"")));

        }
    }

    @Test
    public void should_retrieve_error_message_from_hamcrest_assertion_error_when_the_assertion_is_the_cause() {
        try {
            assertThat("a", is("b"));
        } catch(AssertionError assertionError) {
            WebDriverException exception = new WebDriverException(assertionError);
            AssertionError convertedError = ErrorConvertor.forError(exception).convertToAssertion();
            assertThat(convertedError.getMessage(), allOf(
                    containsString("Expected: is \"b\""),
                    containsString("but: was \"a\"")));

        }
    }


    @Test
    public void should_retrieve_error_message_from_hamcrest_assertion_error_for_more_complex_errors() {
        try {
            List<String> colors = Arrays.asList("red","blue","green");
            assertThat(colors, containsInAnyOrder("red","green","orange"));
        } catch(AssertionError assertionError) {
            WebDriverException exception = new WebDriverException(assertionError);
            AssertionError convertedError = ErrorConvertor.forError(exception).convertToAssertion();
            assertThat(convertedError.getMessage(), allOf(
                    containsString("Expected: iterable over [\"red\", \"green\", \"orange\"] in any order"),
                    containsString("but: Not matched: \"blue\"")));

        }
    }
}
