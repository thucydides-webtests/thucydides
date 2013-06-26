package net.thucydides.core.reports.adaptors.specflow

import spock.lang.Specification


class WhenObtainingASpecflowScenarioTitle extends Specification {

    def "should find the scenario and story titles"() {
        given:
            def simpleSpecflowTitleLine = "***** my.SpecFlow.Features.MyFeature.MyScenario()"
        when:
            def titleLine = new SpecflowScenarioTitleLine(simpleSpecflowTitleLine)
        then:
            titleLine.scenarioTitle == "MyScenario"
        and:
            titleLine.storyTitle == "MyFeature"
    }

}