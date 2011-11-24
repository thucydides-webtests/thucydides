package net.thucydides.core.matchers;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
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
        Person person = new Person("Graeam", "Garden");

        assertThat(matcher.matches(person)).isFalse();
    }

    @Test
    public void should_display_expected_values_when_printed() {
        PropertyMatcher matcher = new PropertyMatcher("firstName", is("Bill"));
        assertThat(matcher.toString()).isEqualTo("firstName is 'Bill'");
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
        assertThat(matcher.toString()).isEqualTo("firstName is 'Bill'");
    }

    @Test
    public void should_filter_list_of_beans_by_matchers() {
        List<Person> persons = Arrays.asList(new Person("Bill", "Oddie"),
                new Person("Graeam", "Garden"),
                new Person("Tim", "Brooke-Taylor"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(persons, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_fail_filter_if_no_matching_elements_found() {
        List<Person> persons = Arrays.asList(new Person("Bill", "Kidd"),
                new Person("Graeam", "Garden"),
                new Person("Tim", "Brooke-Taylor"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(persons, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_return_matching_element() {
        Person bill = new Person("Bill", "Oddie");
        Person graham = new Person("Graeam", "Garden");
        Person tim = new Person("Tim", "Brooke-Taylor");
        List<Person> persons = Arrays.asList(bill, graham, tim);

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.filterElements(persons, firstNameIsBill, lastNameIsOddie)).contains(bill);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_fail_filter_with_descriptive_message_if_no_matching_elements_found() {

        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(containsString("firstName is 'Bill'"));

        List<Person> persons = Arrays.asList(new Person("Bill", "Kidd"),
                new Person("Graeam", "Garden"),
                new Person("Tim", "Brooke-Taylor"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        PropertyMatcher.shouldMatch(persons, firstNameIsBill, lastNameIsOddie);
    }

    @Test
    public void should_match_against_a_single_bean() {
        Person person = new Person("Bill", "Oddie");

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(person, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_not_match_against_non_matching_single_bean() {
        Person person = new Person("Bill", "Kidd");

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(person, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_display_detailed_diagnostics_when_a_single_bean_fails_to_match() {
        Person person = new Person("Bill", "Kidd");

        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected [firstName is 'Bill', lastName is 'Oddie'] but was [firstName = 'Bill', lastName = 'Kidd']");

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        PropertyMatcher.shouldMatch(person, firstNameIsBill, lastNameIsOddie);
    }

    @Test
    public void should_fail_test_if_field_does_not_exist() {
        DodgyBean person = new DodgyBean("Bill", "Oddie");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("Could not find property value for field-does-not-exist"));

        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("field-does-not-exist", is("Oddie"));

        PropertyMatcher.shouldMatch(person, lastNameIsOddie);
    }

    public class DodgyBean {
        private final String firstName;
        private final String lastName;

        DodgyBean(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            throw new IllegalAccessError();
        }

        public String getLastName() {
            return lastName;
        }
    }

    @Test
    public void should_report_dodgy_field_if_cant_read_field_value() {
        DodgyBean person = new DodgyBean("Bill", "Kidd");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("Could not find property value for firstName"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        PropertyMatcher.shouldMatch(person, firstNameIsBill, lastNameIsOddie);
    }

    @Test
    public void should_raise_issue_if_fields_cant_be_introspected() {
        DodgyBean person = new DodgyBean("Bill", "Kidd");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("Could not read bean properties"));

        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        PropertyMatcher.shouldMatch(person, lastNameIsOddie);
    }


}
