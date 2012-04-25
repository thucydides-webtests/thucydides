package net.thucydides.core.util

import net.thucydides.core.reports.TestOutcomeLoader

import spock.lang.Specification

class WhenDisplayingTagNamesInAReadableForm extends Specification {

    def loader = new TestOutcomeLoader()

    def "should transform singular nouns into plurals"() {
        given:
            def inflection = Inflector.instance
        when:
            def pluralForm = inflection.of(singleForm).inPluralForm().toString();
        then:
            pluralForm == expectedPluralForm
        where:
            singleForm          | expectedPluralForm
            'epic'              | 'epics'
            'feature'           | 'features'
            'story'             | 'stories'
            'stories'           | 'stories'
            'octopus'           | 'octopi'
            'sheep'             | 'sheep'
    }

    def "should transform plural nouns into singles"() {
        given:
            def inflection = Inflector.instance
        when:
            def singleForm = inflection.of(pluralForm).inSingularForm().toString()
        then:
            singleForm == expectedSingleForm
        where:
            pluralForm          | expectedSingleForm
                'epics'             | 'epic'
                'features'          | 'feature'
                'stories'           | 'story'
                'story'             | 'story'
                'octopi'            | 'octopus'
                'sheep'             | 'sheep'
    }

    def "should transform a number of object nouns into plural or singular based on the number"() {
        given:
            def inflection = Inflector.instance
        when:
            def pluralForm = inflection.of(count).times(singleForm).inPluralForm().toString()
        then:
            pluralForm == expectedPluralForm
        where:
            count   | singleForm        | expectedPluralForm
            0       | 'epic'            | 'epics'
            1       | 'epic'            | 'epic'
            2       | 'epic'            | 'epics'
    }

    def "should transform camel-case to underscore"() {
        given:
            def inflection = Inflector.instance
        when:
            def underscoreForm = inflection.of(word).withUnderscores().toString()
        then:
            underscoreForm == expectedUnderscoreForm
        where:
                word              | expectedUnderscoreForm
                    'aWord'        | 'a_word'
                    'AnotherWord'  | 'another_word'
    }


    def "should captialize first word"() {
        given:
            def inflection = Inflector.instance
        when:
            def capitalized = inflection.of(word).startingWithACapital().toString()
        then:
            capitalized == expectedCapitalizedForm
        where:
            word          | expectedCapitalizedForm
            'epic'        | 'Epic'
            'features'    | 'Features'
            'some story'  | 'Some story'
    }

    def "should captialize all words"() {
        given:
            def inflection = Inflector.instance
        when:
            def capitalized = inflection.of(word).asATitle().toString()
        then:
            capitalized == expectedCapitalizedForm
        where:
        word                           | expectedCapitalizedForm
            'epic'                     | 'Epic'
            'x-men: the last stand'    | 'X-Men: The Last Stand'
    }

    def "should convert variable expressions into human-readable form"() {
        given:
             def inflection = Inflector.instance
        when:
            def humanized = inflection.of(word).inHumanReadableForm().toString()
        then:
            humanized == expectedHumanizedForm
        where:
        word          | expectedHumanizedForm
            'employee_salary'   | 'Employee salary'
            'author_id'         | 'Author'
            'someTest'          | 'Some test'
            'AnotherTest'       | 'Another test'
    }
}
