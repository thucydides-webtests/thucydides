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
            def reader = new NarrativeReader();
            Optional<Narrative> narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title and description should be loaded"
            narrative.get().title.get() == "Grow more apples"
            narrative.get().text.contains("In order to make apple pies") &&
            narrative.get().text.contains("As a farmer") &&
            narrative.get().text.contains("I want to grow apples")
        and: "the type should be derived from the file name"
            narrative.get().type == "capability"
    }


    File directoryInClasspathAt(String path) {
        new File(getClass().getClassLoader().getResources(path).nextElement().getPath())
    }
}
