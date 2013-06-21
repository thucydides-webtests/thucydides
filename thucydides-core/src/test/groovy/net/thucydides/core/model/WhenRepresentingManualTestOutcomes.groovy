package net.thucydides.core.model

import spock.lang.Specification

class WhenRepresentingManualTestOutcomes extends Specification {

    def "a test outcome should be considered automated by default"() {
        when:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
        then:
            !outcome.isManual()
    }

    def "a manual test outcome can be defined"() {
        when:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).asManualTest();
        then:
            outcome.isManual()
    }

}
