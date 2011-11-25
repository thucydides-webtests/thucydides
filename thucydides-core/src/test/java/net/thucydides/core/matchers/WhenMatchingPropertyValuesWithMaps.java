package net.thucydides.core.matchers;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenMatchingPropertyValuesWithMaps {

    @Test
    public void should_match_field_by_name() {
        PropertyMatcher matcher = new PropertyMatcher("firstName", is("Bill"));
        Map<String, String> person = mappedPerson("Bill", "Oddie");

        assertThat(matcher.matches(person)).isTrue();
    }

    private Map<String,String> mappedPerson(String firstname, String lastname) {
        Map<String, String> person = new HashMap<String, String>();
        person.put("firstName", firstname);
        person.put("lastName", lastname);
        return person;
    }

    @Test
    public void should_fail_if_match_is_not_successful() {
        PropertyMatcher matcher = new PropertyMatcher("firstName", is("Bill"));
        Map<String, String> person = mappedPerson("Graeam", "Garden");

        assertThat(matcher.matches(person)).isFalse();
    }

    @Test
    public void should_obtain_matcher_from_fluent_static_method() {
        PropertyMatcher matcher = PropertyMatcher.the("firstName", is("Bill"));
        Map<String, String> person = mappedPerson("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void should_obtain_instanciated_matcher_from_matcher() {
        Matcher<Object> matcher = PropertyMatcher.the("firstName", is("Bill")).getMatcher();
        Map<String, String> person = mappedPerson("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void instanciated_matcher_should_provide_meaningful_description() {
        Matcher<Object> matcher = PropertyMatcher.the("firstName", is("Bill")).getMatcher();
        assertThat(matcher.toString()).isEqualTo("firstName is 'Bill'");
    }

    @Test
    public void should_filter_list_of_beans_by_matchers() {
        List<Map<String, String>> persons = Arrays.asList(mappedPerson("Bill", "Oddie"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(persons, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_fail_filter_if_no_matching_elements_found() {
        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(persons, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_return_matching_element() {
        Map<String,String> bill = mappedPerson("Bill", "Oddie");
        Map<String,String> graham = mappedPerson("Graeam", "Garden");
        Map<String,String> tim = mappedPerson("Tim", "Brooke-Taylor");
        List<Map<String,String>> persons = Arrays.asList(bill, graham, tim);

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.filterElements(persons, firstNameIsBill, lastNameIsOddie)).contains(bill);
    }

    @Test
    public void should_return_no_elements_if_no_matching_elements_found() {
        Map<String,String> billoddie = mappedPerson("Bill", "Oddie");
        Map<String,String> billkidd = mappedPerson("Bill", "Kidd");
        Map<String,String> graham = mappedPerson("Graeam", "Garden");
        Map<String,String> tim = mappedPerson("Tim", "Brooke-Taylor");
        List<Map<String,String>> persons = Arrays.asList(billoddie, billkidd, graham, tim);

        PropertyMatcher firstNameIsJoe = PropertyMatcher.the("firstName", is("Joe"));

        assertThat(PropertyMatcher.filterElements(persons, firstNameIsJoe)).isEmpty();
    }

    @Test
    public void should_return_multiple_matching_elements() {
        Map<String,String> billoddie = mappedPerson("Bill", "Oddie");
        Map<String,String> billkidd = mappedPerson("Bill", "Kidd");
        Map<String,String> graham = mappedPerson("Graeam", "Garden");
        Map<String,String> tim = mappedPerson("Tim", "Brooke-Taylor");
        List<Map<String,String>> persons = Arrays.asList(billoddie, billkidd, graham, tim);

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));

        assertThat(PropertyMatcher.filterElements(persons, firstNameIsBill)).contains(billkidd, billoddie);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_fail_filter_with_descriptive_message_if_no_matching_elements_found() {

        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(containsString("firstName is 'Bill'"));

        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        PropertyMatcher.shouldMatch(persons, firstNameIsBill, lastNameIsOddie);
    }

    @Test
    public void should_match_against_a_single_bean() {
        Map<String, String> person = mappedPerson("Bill", "Oddie");

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(person, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_not_match_against_non_matching_single_bean() {
        Map<String, String> person = mappedPerson("Bill", "Kidd");

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        assertThat(PropertyMatcher.matches(person, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_display_detailed_diagnostics_when_a_single_bean_fails_to_match() {
        Map<String, String> person = mappedPerson("Bill", "Kidd");

        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(allOf(containsString("Expected [firstName is 'Bill', lastName is 'Oddie'] but was"),
                                              containsString("firstName = 'Bill'"),
                                              containsString("lastName = 'Kidd")));

        PropertyMatcher firstNameIsBill = PropertyMatcher.the("firstName", is("Bill"));
        PropertyMatcher lastNameIsOddie = PropertyMatcher.the("lastName", is("Oddie"));

        PropertyMatcher.shouldMatch(person, firstNameIsBill, lastNameIsOddie);
    }

}
