package net.thucydides.core.requirements

import spock.lang.Specification
import net.thucydides.core.requirements.model.NarrativeReader
import net.thucydides.core.requirements.model.Narrative
import com.google.common.base.Optional

class WhenLoadingNarrativeDescriptionFromADirectory extends Specification {

    def "Should be able to load a narrative description from a directory"() {
        given: "there is a capability.narrativeText file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            Optional<Narrative> narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title and description should be loaded"
            narrative.get().title.get() == "Grow more apples"
            narrative.get().text.contains("In order to make apple pies") &&
            narrative.get().text.contains("As a farmer") &&
            narrative.get().text.contains("I want to grow apples")
        and: "the narrative type should be derived from the directory"
            narrative.get().type == "capability"
    }

    def "Should be able to derive the narrative type from the directory level"() {
        given: "there is a narrative.txt file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            Optional<Narrative> narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title and description should be loaded"
            narrative.get().title.get() == "Grow red apples"
        and: "the type should be derived from the directory level"
            narrative.get().type == "feature"
    }

    def "Should use the lowest requirement type for deeply nested requirements"() {
        given: "there is a narrative.txt file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            Optional<Narrative> narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title and description should be loaded"
            narrative.get().title.get() == "Grow special red apples"
        and: "the type should be derived from lowest requirement type"
            narrative.get().type == "feature"
    }

    def "Should ignore GivenStories clause in requirements from .story files"() {
        given: "there is a .story file in a directory"
            File storyFile = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples/PlantingAnotherAppleTree.story")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            Optional<Narrative> narrative = reader.loadFromStoryFile(storyFile)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrative text should be loaded"
            !narrative.get().title.isPresent()
            narrative.get().getText().contains("As a farmer")
            narrative.get().getText().contains("I want to plant an apple tree")
            narrative.get().getText().contains("So that I can grow apples")
        and: "the type should be story"
            narrative.get().type == "story"
        and: "meta data is skipped"
            !narrative.get().getText().contains("Meta") && !narrative.get().getText().contains("issue")
    }

    File directoryInClasspathAt(String path) {
        new File(getClass().getClassLoader().getResources(path).nextElement().getPath())
    }
}
