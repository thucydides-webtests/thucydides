package net.thucydides.core.requirements

import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenLoadingRequirementsFromAPackageStructure extends Specification {
    public static final String ROOT_DIRECTORY = "annotatedstories"

    def "Should be able to load capabilities from the default package structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements();
            def capabilityNames = capabilities.collect {it.name}
            def capabilitiyTexts = capabilities.collect {it.narrativeText}
        then:
            capabilityNames == ["Apples", "Nice zucchinis", "Potatoes"]
            capabilitiyTexts == ["This is a narrative\nFor apples", "This is a narrative\nFor NiceZuchinnis",
                    "This is a narrative\nFor a potato"]
    }

    def "Should be able to load capabilities from the file system"() {
        given: "The "
            def vars = new MockEnvironmentVariables()
            def outputDirectory = getClass().getResource("/").getPath()
            vars.setProperty(ThucydidesSystemProperty.OUTPUT_DIRECTORY.propertyName, outputDirectory)
            vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY + "NotOnClasspath")
        and: "We are using the Annoation provider"
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "we load the requirements"
            def capabilities = capabilityProvider.getRequirements();
            def capabilityNames = capabilities.collect {it.name}
            def capabilitiyTexts = capabilities.collect {it.narrativeText}
        then:
            capabilityNames == ["Mycapability1"]
            capabilitiyTexts == ["A narrative text\nfor capability 1"]
    }

    def "Should be able to load issues from the default directory structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements()
            def ids = capabilities.collect {it.cardNumber}
        then: "the card numbers should be read from the narratives if present"
            ids == ["#123", "", ""]
    }

    def "Should derive title from the directory name if not present in narravite"(){
        given: "there is a Narrative annotation on a pacakge that does not have a title line"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We try to load the capability from the package"
            def capabilities = capabilityProvider.getRequirements()
        then: "the capability should be found"
            def zucchiniCapability = capabilities.get(1)
        then: "the title should be a human-readable version of the directory name"
            zucchiniCapability.name == "Nice zucchinis"
    }

    def "capabilities can be nested"(){
        given: "We are using the default requirements provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We load requirements with nested capabilities"
            def capabilities = capabilityProvider.getRequirements()
        then: "the nested requirements should be recorded"
            def capabilityNames = capabilities.get(2).children.collect {it.name}
            //we've got both a narrative from a test and from a directory
            capabilityNames == ["Test2", "Big potatoes"]
    }

    def "nested capability types are set by convention if no Narrative annotation are present"() {
        given: "We are using default requirement package"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We load requirements with nested requirement packages and no Narrative annotations"
            def capabilities = capabilityProvider.getRequirements()
        then: "The nested requirement are of type 'feature'"
            def capabilityTypes = capabilities.get(2).children.collect{it.type}
            capabilities.collect{it.type} == ["capability", "capability", "mytype"]
            capabilityTypes == ["story", "feature"]
    }

    def "default nested requirement types can be overriden using an environment variable"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables()
        and: "We define the requirement type hierarchy in the environment variables"
            vars.setProperty("thucydides.requirement.types","theme, epic, feature")
        vars.setProperty(ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.propertyName, ROOT_DIRECTORY)
        RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We load requirements with nested requirement pacakge"
            def capabilities = capabilityProvider.getRequirements()
        then: "the second-level requirement are of type 'epic'"
            capabilities.collect{it.type} == ["theme", "theme", "mytype"]
            capabilities.get(2).children.collect{it.type} == ["story", "epic"]
    }

    def "default requirement directory can be overriden using an environment variable"() {
        given: "We using the annotated requirement provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the requirement annotated directory in the environment variables"
            vars.setProperty("thucydides.annotated.requirements.dir", "otherannotatedstories")
            RequirementsTagProvider capabilityProvider = new AnnotationBasedTagProvider(vars)
        when: "We load requirements"
            def capabilities = capabilityProvider.getRequirements()
        then: "the requirements are the one found in otherannotatedstories"
            capabilities.collect{it.name} == ["Theother1", "Theother2"]
    }
}
