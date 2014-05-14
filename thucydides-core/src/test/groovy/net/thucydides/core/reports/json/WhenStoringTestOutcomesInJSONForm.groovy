package net.thucydides.core.reports.json

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import spock.lang.Specification

class WhenStoringTestOutcomesInJSONForm extends Specification {

    JSONConverter converter = new JacksonJSONConverter()

    def "should convert a simple test outcome to JSON"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.called("someStory"))
        when:
            def json = converter.toJson(outcome)
        then:
            def testOutcomeInJson = json.toString()
            println testOutcomeInJson

    }

}