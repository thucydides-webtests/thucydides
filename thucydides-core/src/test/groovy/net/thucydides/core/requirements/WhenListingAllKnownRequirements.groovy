package net.thucydides.core.requirements

import spock.lang.Specification
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables

import static net.thucydides.core.requirements.FileSystemRequirementsTagProvider.getDefaultRootDirectoryPathFrom
import net.thucydides.core.ThucydidesSystemProperty

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

    def "Should be able to read requirements from requirements base dir if it is specified in environment properties"() {
        given: "we have stored requirements in a non-standard folder structure"
            File requirementsFolder = newTemporaryRequirementsDirectory()
            File storiesFolder = newStoriesFolder(requirementsFolder, "/src/test/resources/stories/search_feature");
            File story = addStory(storiesFolder)

        and: "we have specified the base directory in the system property"
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.test.requirements.basedir",requirementsFolder.getAbsolutePath());

        and: " we are using the default requirements provider"
            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(
                    getDefaultRootDirectoryPathFrom(environmentVariables), 0, environmentVariables)

        when: "we obtain the list of requirements"
            def capabilities = capabilityProvider.getRequirements()
            def capabilityNames = capabilities.collect {it.name}
        then:
        capabilityNames == ["Search feature"]
    }




    def newTemporaryRequirementsDirectory() {
        File requirementsDir= File.createTempFile("test-project","");
        if (requirementsDir.exists()) requirementsDir.delete();
        requirementsDir.mkdir();
        return requirementsDir;
    }

    def newStoriesFolder(File parentDir, String storiesFolderName) {
        File storiesFolder = new File(parentDir, storiesFolderName);
        storiesFolder.mkdirs();
        return storiesFolder;
    }

    def addStory(File requirementsDir) throws IOException {
        File story = new File(requirementsDir,"placeholder.txt");
        if (! story.exists()) {
            story.createNewFile();
        }

        return story;
    }


}
