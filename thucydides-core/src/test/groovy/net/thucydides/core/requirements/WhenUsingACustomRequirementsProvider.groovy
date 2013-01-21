package net.thucydides.core.requirements

import spock.lang.Specification
import net.thucydides.core.statistics.service.ClasspathTagProviderService

class WhenUsingACustomRequirementsProvider extends Specification {

    def tagProviderService = new ClasspathTagProviderService();
    def ClasspathRequirementsProviderService requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService)

    def "Should be able to find a requirements provider on the classpath"() {
        when: "We have a custom requirements provider and a corresponding services file in the META-INF/services directory"
            List<RequirementsTagProvider> requirementsProviders = requirementsProviderService.getRequirementsProviders();
        then: "We can obtain a custom requirements provider if it is present"
            requirementsProviders.collect { it.class.simpleName } == ["FileSystemRequirementsTagProvider"]
    }

    def "Should return the default file system requirements provider on the classpath if no others are present"() {
        given:
            def tagProviderService = Mock(ClasspathTagProviderService)
            def requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService)
            tagProviderService.getTagProviders() >> [ new FileSystemRequirementsTagProvider()]
        when: "We get the list of default requirements providers"
            List<RequirementsTagProvider> requirementsProviders = requirementsProviderService.getRequirementsProviders();
        then: "We obtain the default requirements provider"
            requirementsProviders.collect { it.class.simpleName } == ["FileSystemRequirementsTagProvider"]
    }

    def "Should not return the default file system requirements provider on the classpath if others are defined"() {
        given:
            def tagProviderService = Mock(ClasspathTagProviderService)
            def requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService)
            tagProviderService.getTagProviders() >> [ new CustomRequirementsTagProvider(), new FileSystemRequirementsTagProvider()]
        when: "We get the list of default requirements providers"
            List<RequirementsTagProvider> requirementsProviders = requirementsProviderService.getRequirementsProviders();
        then: "We obtain the default requirements provider"
            requirementsProviders.collect { it.class.simpleName } == ["CustomRequirementsTagProvider"]
    }

}
