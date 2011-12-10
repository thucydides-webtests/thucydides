package net.thucydides.core.matchers;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenMatchingWithCollections {

    public class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }
    }

    Person carl = new Person("Carl",50);
    Person dick = new Person("Dick", 40);
    Person tor = new Person("Tor", 30);
    Person joe = new Person("Joe", 20);

    List<Person> people = Arrays.asList(carl, dick, tor, joe);
    
    @Test
    public void should_match_maximum_value() {

        MaxFieldValueMatcher matcher = new MaxFieldValueMatcher("age", is(50));

        assertThat(matcher.matches(people)).isTrue();
    }

    @Test
    public void should_not_match_incorrect_maximum_value() {

        BeanMatcher matcher = new MaxFieldValueMatcher("age", is(40));

        assertThat(matcher.matches(people)).isFalse();
    }


    @Test
    public void should_match_minimum_value() {

        BeanMatcher matcher = new MinFieldValueMatcher("age", is(20));

        assertThat(matcher.matches(people)).isTrue();
    }

    @Test
    public void should_not_match_incorrect_minimum_value() {

        BeanMatcher matcher = new MaxFieldValueMatcher("age", is(30));

        assertThat(matcher.matches(people)).isFalse();
    }
}
