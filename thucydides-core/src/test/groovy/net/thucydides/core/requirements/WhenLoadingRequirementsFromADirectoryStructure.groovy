package net.thucydides.core.requirements

import spock.lang.Specification
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables

class WhenLoadingRequirementsFromADirectoryStructure extends Specification {

    def "Should be able to load capabilities from the default directory structure"() {
        given: "We are using the default requirements provider"
            RequirementsProvider capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features");
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements()
            def capabilityNames = capabilities.collect {it.name}
        then: "the requirements should be loaded from the first-level sub-directories"
            capabilityNames == ["Grow more apples", "Grow potatoes", "Grow zuchinnis"]
    }

    def "Should derive title from directory name if not present in narrative"() {
        given: "there is a capability.narrativeText file in a directory that does not have a title line"
            def capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features")
        when: "We try to load the capability from the directory"
            def capabilities = capabilityProvider.getRequirements()
        then: "the capability should be found"
            def potatoeGrowingCapability = capabilities.get(1)
        then: "the title should be a human-readable version of the directory name"
            potatoeGrowingCapability.name == "Grow potatoes"
    }

    def "the capability type is derived from the .narrative file name"() {
        given: "We are using the default requirements provider"
            RequirementsProvider capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features");
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements()
        then: "the requirements should be of type 'capability"
            capabilities.get(0).type == "capability"

    }

    def "when there is no .narrative file the capability is determined by a configurable convention"() {
        given: "We are using the default requirements provider"
            RequirementsProvider capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features");
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements()
        then: "the requirements should be of type 'capability"
            capabilities.get(2).type == "capability"

    }

    def "capabilities can be nested"() {
        given: "We are using the default requirements provider"
            RequirementsProvider capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features");
        when: "We load requirements with nested capability directories"
            def capabilities = capabilityProvider.getRequirements()
        then: "the nested requirements should be recorded"
            def growApples = capabilities.get(0)
            assert !growApples.children.isEmpty()
        and: "the capablity names are derived from the directory names"
            def capabilityNames = capabilities.get(0).children.collect {it.name}
            capabilityNames == ["Grow cider apples", "Grow granny smiths", "Grow red apples"]
    }

    def "nested capability types are set by convention if no .narrative files are present"() {
        given: "We are using the default requirements provider"
            RequirementsProvider capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features");
        when: "We load requirements with nested capability directories and no .narrative files"
            def capabilities = capabilityProvider.getRequirements()
        then: "the nested capablities are of type 'feature'"
            def capabilityTypes = capabilities.get(0).children.collect {it.type}
            capabilityTypes == ["feature", "feature", "feature"]
    }

    def "default nested capability types can be overriden using an environment variable"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsProvider capabilityProvider = new FileSystemRequirementsProvider("sample-story-directories/capabilities_and_features", 0, vars);
        and: "We define the capability type hierarchy in the environment variables"
            vars.setProperty("thucydides.capability.types","theme, epic, feature")
        when: "We load requirements with nested capability directories and no .narrative files"
            def capabilities = capabilityProvider.getRequirements()
        then: "the second-level capablities are of type 'epic'"
            capabilities.get(0).getChildren().get(0).getType() == "epic"
    }

}
