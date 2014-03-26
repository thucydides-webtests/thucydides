package net.thucydides.core.statistics.service;

import com.google.common.collect.Lists;
import net.thucydides.core.requirements.CoreTagProvider;
import net.thucydides.core.requirements.OverridableTagProvider;
import net.thucydides.core.requirements.RequirementsTagProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ServiceLoader;

public class ClasspathTagProviderService implements TagProviderService {

    private final Logger logger = LoggerFactory.getLogger(ClasspathTagProviderService.class);

    private List<TagProvider> tagProviders;

    public ClasspathTagProviderService() {
    }

    @Override
    public List<TagProvider> getTagProviders() {
        if (tagProviders == null) {
            List<TagProvider> newTagProviders = Lists.newArrayList();

            Iterable<TagProvider> tagProviderServiceLoader = loadTagProvidersFromPath();

            for (TagProvider aServiceLoader : tagProviderServiceLoader) {
                newTagProviders.add(aServiceLoader);
            }
            if (additionalTagProvidersArePresentIn(newTagProviders)) {
                newTagProviders = removeOverridableProvidersFrom(newTagProviders);
            }
            tagProviders = newTagProviders;
        }
        return tagProviders;
    }

    protected Iterable<TagProvider> loadTagProvidersFromPath() {
        return ServiceLoader.load(TagProvider.class);
    }


    private boolean additionalTagProvidersArePresentIn(List<TagProvider> providers) {
        for(TagProvider provider : providers) {
            if (!isKnownProvider(provider)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKnownProvider(TagProvider provider) {
        return (CoreTagProvider.class.isAssignableFrom(provider.getClass()));
    }

    private List<TagProvider> removeOverridableProvidersFrom(List<TagProvider> providers) {
        List<TagProvider> retainedProviders = com.beust.jcommander.internal.Lists.newArrayList();
        for(TagProvider provider : providers) {
            if (!OverridableTagProvider.class.isAssignableFrom(provider.getClass())) {
                retainedProviders.add(provider);
            }
        }
        return retainedProviders;
    }
}
