package net.thucydides.core.requirements

import spock.lang.Specification

class WhenListingAllKnownRequirements extends Specification {

    def "Should be able to list all the available capabilities"() {
        given: "We are using the default requirements provider"
            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider();
        when: "We obtain the list of requirements"
            def capabilities = capabilityProvider.getRequirements()
            def capabilityNames = capabilities.collect {it.name}
        then:
            capabilityNames == ["Grow potatoes", "Grow wheat", "Raise chickens"]
    }

    def "Should be able to read directories and .story files"() {
        given: "We are using the default requirements provider"
            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider();
        when: "We obtain the list of requirements"
            def capabilities = capabilityProvider.getRequirements()
            def plantPotatoesStory = capabilities.get(0).getChildren().get(0).getChildren().get(0);
        then:
            plantPotatoesStory.getName() == "Plant potatoes"
    }

}
