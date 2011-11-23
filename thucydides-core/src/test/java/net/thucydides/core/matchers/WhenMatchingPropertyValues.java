package net.thucydides.core.matchers;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenMatchingPropertyValues {

    public class Person {
        private final String firstName;
        private final String lastName;

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @Test
    public void should_match_field_by_name() {
        PropertyMatcher matcher = new PropertyMatcher("firstName", is("Bill"));
        Person person = new Person("Bill", "Oddie");

        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void should_fail_if_match_is_not_successful() {
        PropertyMatcher matcher = new PropertyMatcher("firstName", is("Bill"));
        Person person = new Person("Graham", "Garden");

        assertThat(matcher.matches(person)).isFalse();
    }

    @Test
    public void should_display_expected_values_when_printed() {
        PropertyMatcher matcher = new PropertyMatcher("firstName", is("Bill"));
        assertThat(matcher.toString()).isEqualTo("firstName is \"Bill\"");
    }

    @Test
    public void should_obtain_matcher_from_fluent_static_method() {
        PropertyMatcher matcher = PropertyMatcher.the("firstName", is("Bill"));
        Person person = new Person("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void should_obtain_instanciated_matcher_from_matcher() {
        Matcher<Object> matcher = PropertyMatcher.the("firstName", is("Bill")).getMatcher();
        Person person = new Person("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void instanciated_matcher_should_provide_meaningful_description() {
        Matcher<Object> matcher = PropertyMatcher.the("firstName", is("Bill")).getMatcher();
        assertThat(matcher.toString()).isEqualTo("firstName is \"Bill\"");
    }

}
