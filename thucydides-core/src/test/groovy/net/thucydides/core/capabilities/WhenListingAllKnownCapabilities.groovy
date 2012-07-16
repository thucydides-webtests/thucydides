package net.thucydides.core.capabilities

import spock.lang.Specification
import net.thucydides.core.statistics.service.TagProvider

class WhenListingAllKnownCapabilities extends Specification {

    def "Should be able to list all the available capabilities"() {
        given: "We are using the default capabilities provider"
            CapabilityProvider capabilityProvider = new DefaultCapabilityProvider();
        when: "We obtain the list of capabilities"
            def capabilities = capabilityProvider.getCapabilities()
            def capabilityNames = capabilities.collect {it.name}
        then: "the list of capabilities should come from a file called application.capabilities"
            capabilityNames == ["Grow potatoes", "Grow wheat", "Raise chickens"]

    }

//    def "The default capability provider should load capabilities from a text file"() {
//        given: "We are using the default tag provider"
//            TagProvider tagProvider = new DefaultTagProvider()
//    }
//
//    def "Should obtain a list of all specified capabilities"() {
//        given: "We have set up a Tag Provider implementation that can return the list of all capabilities"
//            TagProvider someTagProvider = Mock(TagProvider)
//            someTagProvider.getCapabilityTags() >> ["Grow potatoes", "Grow wheat", "Raise chickens"]
//        when: "We retrieve the capability list"
//            def capabilities = []
//        then: "All capabilities should be in the list"
//            capabilities == ["Grow potatoes", "Grow wheat", "Raise chickens"]
//    }
}
