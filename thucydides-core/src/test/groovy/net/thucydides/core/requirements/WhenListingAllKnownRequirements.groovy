package net.thucydides.core.requirements

import spock.lang.Specification
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables

import static net.thucydides.core.requirements.FileSystemRequirementsTagProvider.getDefaultRootDirectoryPathFrom

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

    def "Should be able to list all the available capabilities from a package structure"() {
        given: "we have stored requirements in a package structure with the JUnit tests"
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        and: "We are using the default requirements provider"
            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(getDefaultRootDirectoryPathFrom(environmentVariables))
        when: "We obtain the list of requirements"
            def capabilities = capabilityProvider.getRequirements()
            def capabilityNames = capabilities.collect {it.name}
        then:
            capabilityNames == ["Grow potatoes", "Grow turnips", "Nocapacities"]
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
