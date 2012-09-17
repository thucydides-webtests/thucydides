package net.thucydides.core.statistics.service;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ClasspathTagProviderService implements TagProviderService {

    private final Logger logger = LoggerFactory.getLogger(ClasspathTagProviderService.class);

    public ClasspathTagProviderService() {
    }

    @Override
    public List<TagProvider> getTagProviders() {
        List<TagProvider> tagProviders = Lists.newArrayList();

        ServiceLoader<TagProvider> tagProviderServiceLoader = ServiceLoader.load(TagProvider.class);

        for (TagProvider aServiceLoader : tagProviderServiceLoader) {
            logger.debug("Using tag provider: {}", aServiceLoader.getClass());
            tagProviders.add(aServiceLoader);
        }
        return tagProviders;
    }
}
