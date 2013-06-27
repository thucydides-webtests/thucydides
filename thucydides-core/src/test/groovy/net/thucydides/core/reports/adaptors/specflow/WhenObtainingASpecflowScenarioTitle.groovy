package net.thucydides.core.reports.adaptors.specflow

import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification


class WhenObtainingASpecflowScenarioTitle extends Specification {

    def "should find the scenario title"() {
        given:
            def simpleSpecflowTitleLine = "***** my.SpecFlow.Features.MyFeature.MyScenario()"
        when:
            def titleLine = new SpecflowScenarioTitleLine(simpleSpecflowTitleLine)
        then:
            titleLine.scenarioTitle == "MyScenario"
    }

    def "should find the story title"() {
        given:
            def simpleSpecflowTitleLine = "***** my.SpecFlow.Features.MyFeature.MyScenario()"
        when:
            def titleLine = new SpecflowScenarioTitleLine(simpleSpecflowTitleLine)
        then:
            titleLine.storyTitle == "My feature"
    }

    def "should find the story path"() {
        given:
            def simpleSpecflowTitleLine = "***** my.SpecFlow.Features.MyFeature.MyScenario()"
        when:
            def titleLine = new SpecflowScenarioTitleLine(simpleSpecflowTitleLine)
        then:
            titleLine.storyPath == "my.SpecFlow.Features.MyFeature"
    }

    def "should remove redundant inner packages from the story path"() {
        given:
            def simpleSpecflowTitleLine = "***** app.capability.SpecFlow.Features.MyFeature.MyScenario()"
            def environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("thucydides.requirement.exclusions", "SpecFlow,Features")
            def titleLine = new SpecflowScenarioTitleLine(simpleSpecflowTitleLine,environmentVariables)
        then:
            titleLine.storyPath == "app.capability.MyFeature"
    }

}