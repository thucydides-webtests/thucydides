package net.thucydides.core.requirements;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableList;
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
 *
 * The
 */
public class ClasspathRequirementsProviderService implements RequirementsProviderService {
    private final Logger logger = LoggerFactory.getLogger(ClasspathRequirementsProviderService.class);

    private ClasspathTagProviderService tagProviderService;

    private List<RequirementsTagProvider> requirementsTagProviders;

    @Inject
    public ClasspathRequirementsProviderService(ClasspathTagProviderService tagProviderService) {
        this.tagProviderService = tagProviderService;
    }


    public List<RequirementsTagProvider> getRequirementsProviders() {

        if (requirementsTagProviders == null) {
            requirementsTagProviders = loadRequirementsTagProviders();
        }

        return ImmutableList.copyOf(requirementsTagProviders);
    }

    private List<RequirementsTagProvider> loadRequirementsTagProviders() {
        List<RequirementsTagProvider> providers = new ArrayList<RequirementsTagProvider>();

        List<TagProvider> tagProviders = tagProviderService.getTagProviders();
        logger.info("Using requirements providers: {}", tagProviders);
        for (TagProvider tagProvider : tagProviders) {
            if (tagProvider instanceof RequirementsTagProvider) {
                logger.info("ADDING REQUIREMENTS PROVIDER " + tagProvider);
                providers.add((RequirementsTagProvider)tagProvider);
            }
        }
        removeDefaultProviderIfItIsNotFirstFrom(providers);
        return providers;
    }

    private void removeDefaultProviderIfItIsNotFirstFrom(List<RequirementsTagProvider> providers) {
        int defaultProviderPos = -1;
        for(int i = 0; i < providers.size(); i++) {
            if (providers.get(i) instanceof  FileSystemRequirementsTagProvider) {
                defaultProviderPos = i;
                break;
            }
        }
        if (defaultProviderPos > 0) {
            providers.remove(defaultProviderPos);
        }
    }
}
