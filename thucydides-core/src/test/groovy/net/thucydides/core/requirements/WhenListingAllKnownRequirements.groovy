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
}
