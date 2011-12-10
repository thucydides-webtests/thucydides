package net.thucydides.core.matchers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenMatchingPropertyValueCollections {

    public class Person {
        private final String firstName;
        private final String lastName;
        private final DateTime birthday;
        private final int age;
        
        Person(String firstName, String lastName, DateTime birthday, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.age = age;
        }

        Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = new DateTime();
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public DateTime getBirthday() {
            return birthday;
        }
        
        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", birthday=" + birthday +
                    ", age=" + age +
                    '}';
        }
    }

    Person billoddie;
    Person billkidd;
    Person graeme;
    Person tim;

    @Before
    public void setupPeople() {
        tim = new Person("Tim", "Brooke-Taylor", 25);
        billoddie = new Person("Bill", "Oddie", 30);
        billkidd = new Person("Bill", "Kidd", 35);
        graeme = new Person("Graeme", "Garden", 40);
    }

    @Test
    public void should_filter_list_of_beans_by_matchers() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatchers.matches(persons, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_fail_filter_if_no_matching_elements_found() {
        List<Person> persons = Arrays.asList(tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatchers.matches(persons, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_return_matching_element() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatchers.filterElements(persons, firstNameIsBill, lastNameIsOddie)).contains(billoddie);
    }

    @Test
    public void should_return_matching_elements_with_count_restrictions() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));
        BeanMatcher countIsOne = BeanMatchers.the_count(is(1));

        assertThat(BeanMatchers.filterElements(persons, firstNameIsBill, lastNameIsOddie, countIsOne)).contains(billoddie);
    }

    @Test
    public void should_match_elements_with_uniqueness_restrictions() {
        List<Person> persons = Arrays.asList(billoddie, billkidd, tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNamesAreDifferent = BeanMatchers.each("lastName").isDifferent();

        BeanMatchers.matches(persons, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test
    public void should_return_no_elements_if_no_matching_elements_found() {
        List<Person> persons = Arrays.asList(billoddie, billkidd, tim, graeme);

        BeanMatcher firstNameIsJoe = BeanMatchers.the("firstName", is("Joe"));

        assertThat(BeanMatchers.filterElements(persons, firstNameIsJoe)).isEmpty();
    }

    @Test
    public void should_return_multiple_matching_elements() {
        List<Person> persons = Arrays.asList(billoddie, billkidd, tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        assertThat(BeanMatchers.filterElements(persons, firstNameIsBill)).contains(billkidd, billoddie);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_fail_filter_with_descriptive_message_if_no_matching_elements_found() {

        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(containsString("firstName is 'Bill'"));

        List<Person> persons = Arrays.asList(billkidd, tim, graeme);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        BeanMatchers.shouldMatch(persons, firstNameIsBill, lastNameIsOddie);
    }

    @Test
    public void should_check_the_size_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher containsThreeEntries = BeanMatchers.the_count(is(3));

        BeanMatchers.shouldMatch(persons, containsThreeEntries);
    }

    @Test
    public void should_check_the_min_value_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatchers.shouldMatch(persons, BeanMatchers.min("age",is(25)));
    }

    @Test(expected = AssertionError.class)
    public void should_check_the_min_value_of_a_collection_with_failing_case() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatchers.shouldMatch(persons, BeanMatchers.min("age",is(30)));
    }

    @Test
    public void should_use_natural_order_for_non_numerical_min() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatchers.shouldMatch(persons, BeanMatchers.min("firstName",is("Bill")));
    }

    @Test
    public void should_check_the_max_value_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs40 = BeanMatchers.max("age",is(40));

        BeanMatchers.shouldMatch(persons, maxAgeIs40);
    }

    @Test(expected = AssertionError.class)
    public void should_check_the_max_value_of_a_collection_with_failing_case() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs30 = BeanMatchers.max("age",is(30));

        BeanMatchers.shouldMatch(persons, maxAgeIs30);
    }

    @Test
    public void should_use_natural_order_for_non_numerical_max() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs40 = BeanMatchers.max("firstName",is("Tim"));

        BeanMatchers.shouldMatch(persons, maxAgeIs40);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_detect_invalid_fieldname_when_checking_the_max_value_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs30 = BeanMatchers.max("doesNotExist",is(30));

        BeanMatchers.shouldMatch(persons, maxAgeIs30);
    }

    @Test(expected = AssertionError.class)
    public void should_fail_if_the_size_of_a_collection_is_incorrect() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));

        BeanMatchers.shouldMatch(persons, containsTwoEntries);
    }

    @Test
    public void should_check_the_size_of_a_collection_and_its_contents() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        BeanMatchers.shouldMatch(persons, containsTwoEntries, firstNameIsBill);
    }

    @Test
    public void should_check_field_uniqueness() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher lastNamesAreDifferent = BeanMatchers.each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        BeanMatchers.shouldMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test(expected = AssertionError.class)
    public void should_check_field_uniqueness_when_not_unique() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billoddie);

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher lastNamesAreDifferent = BeanMatchers.each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        BeanMatchers.shouldMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test
    public void should_check_multiple_different_types_of_matches() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher lastNamesAreDifferent = BeanMatchers.each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher maxAgeIs35 = BeanMatchers.max("age", is(35));
        BeanMatchers.shouldMatch(persons, 
                                 containsTwoEntries, 
                                 firstNameIsBill, 
                                 lastNamesAreDifferent,
                                 maxAgeIs35);
    }

    @Test(expected = AssertionError.class)
    public void should_fail_correctly_when_checking_multiple_different_types_of_matches() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher lastNamesAreDifferent = BeanMatchers.each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher maxAgeIs35 = BeanMatchers.max("age", is(45));
        BeanMatchers.shouldMatch(persons,
                containsTwoEntries,
                firstNameIsBill,
                lastNamesAreDifferent,
                maxAgeIs35);
    }

}
