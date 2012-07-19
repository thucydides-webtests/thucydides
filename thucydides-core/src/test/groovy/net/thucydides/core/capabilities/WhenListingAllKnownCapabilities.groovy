package net.thucydides.core.capabilities

import spock.lang.Specification

class WhenListingAllKnownCapabilities extends Specification {

    def "Should be able to list all the available capabilities"() {
        given: "We are using the default capabilities provider"
            CapabilityProvider capabilityProvider = new FileSystemCapabilityProvider();
        when: "We obtain the list of capabilities"
            def capabilities = capabilityProvider.getCapabilities()
            def capabilityNames = capabilities.collect {it.name}
        then:
            capabilityNames == ["Grow potatoes", "Grow wheat", "Raise chickens"]
    }
}
