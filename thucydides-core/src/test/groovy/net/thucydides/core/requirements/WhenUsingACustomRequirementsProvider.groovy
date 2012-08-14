package net.thucydides.core.requirements

import spock.lang.Specification
import net.thucydides.core.statistics.service.ClasspathTagProviderService

class WhenUsingACustomRequirementsProvider extends Specification {

    def tagProviderService = new ClasspathTagProviderService();
    def ClasspathRequirementsProviderService requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService)

    def "Should be able to find a requirements provider on the classpath"() {
        when: "We have a custom requirements provider and a corresponding services file in the META-INF/services directory"
            List<RequirementsTagProvider> requirementsProviders = requirementsProviderService.getRequirementsProviders();
        then: "We can obtain the custom requirements provider"
            requirementsProviders.find{ it.class.simpleName == "CustomRequirementsTagProvider"}
    }

    def "Should be able to find the default file system requirements provider on the classpath"() {
        when: "We get the list of default requirements providers"
            List<RequirementsTagProvider> requirementsProviders = requirementsProviderService.getRequirementsProviders();
        then: "We obtain the default requirements provider"
            requirementsProviders.find{ it.class.simpleName == "FileSystemRequirementsTagProvider"}
    }

}
