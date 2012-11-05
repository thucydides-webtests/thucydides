package net.thucydides.core.matchers

import spock.lang.Specification

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is

class BeanMatcherSpecification extends Specification {

    def "should match strings"() {
        expect:
            BeanMatchers.checkThat(value, is(comparedWith)).matches() == expectedResult
        where:
            value       | comparedWith  | expectedResult
            "Smith"     | "Smith"       | true
            "Smith"     | "Jones"       | false
    }

    def "should match booleans"() {
        expect:
         BeanMatchers.checkThat(value, org.hamcrest.Matchers.is(comparedWith)).matches() == expectedResult
        where:
        value     | comparedWith  | expectedResult
        true      | true          | true
        true      | false         | false
        false     | true          | false
        false     | false         | true
    }

    def "should match numbers"() {
        expect:
        BeanMatchers.checkThat(value, equalTo(comparedWith)).matches() == expectedResult
        where:
        value     | comparedWith             | expectedResult
        100       | 100.0                    | true
        100.0     | 100                      | true
        100.00    | 100.0                    | true
        100       | 100                      | true
        100       | 0                        | false
        0         | 100                      | false
    }

    def "should match numbers with 'is'"() {
        expect:
        BeanMatchers.checkThat(value, org.hamcrest.Matchers.is(comparedWith)).matches() == expectedResult
        where:
        value     | comparedWith             | expectedResult
        100       | 100.0                    | true
        100.0     | 100                      | true
        100.00    | 100.0                    | true
        100       | 100                      | true
        100       | 0                        | false
        0         | 100                      | false
    }
}
