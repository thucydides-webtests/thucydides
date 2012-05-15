package net.thucydides.core.statistics.service;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ClasspathTagProviderService implements TagProviderService {
//    private final List<TagProvider> tagProviders;
    private final EnvironmentVariables environmentVariables;

    @Inject
    public ClasspathTagProviderService(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public List<TagProvider> getTagProviders() {
        List<TagProvider> tagProviders = Lists.newArrayList();

        ServiceLoader<TagProvider> serviceLoader = ServiceLoader.load(TagProvider.class);

        for (TagProvider aServiceLoader : serviceLoader) {
            tagProviders.add(aServiceLoader);
        }
        return tagProviders;
    }
}
