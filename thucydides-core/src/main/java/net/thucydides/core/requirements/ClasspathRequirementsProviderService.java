package net.thucydides.core.requirements;

import com.google.inject.Inject;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Provides a way to obtain the list of requirements providers.
 * Requirements providers are a special type of tag providers that also provide a list of system requirements.
 * Custom requirements providers can be placed on the classpath along with a file in the META-INF.services
 * directory called net.thucydides.core.statistics.service.TagProvider.old that lists the fully qualified class
 * name for the class.
 */
public class ClasspathRequirementsProviderService implements RequirementsProviderService {
    private final Logger logger = LoggerFactory.getLogger(ClasspathRequirementsProviderService.class);

    private ClasspathTagProviderService tagProviderService;

    @Inject
    public ClasspathRequirementsProviderService(ClasspathTagProviderService tagProviderService) {
        this.tagProviderService = tagProviderService;
    }


    public List<RequirementsTagProvider> getRequirementsProviders() {
        List<RequirementsTagProvider> requirementsTagProviders = new ArrayList<RequirementsTagProvider>();

        List<TagProvider> tagProviders = tagProviderService.getTagProviders();
        logger.info("Using requirements providers: {}", tagProviders);
        for (TagProvider tagProvider : tagProviders) {
            if (tagProvider instanceof RequirementsTagProvider) {
                requirementsTagProviders.add((RequirementsTagProvider)tagProvider);
            }
        }
        return requirementsTagProviders;
    }
}
