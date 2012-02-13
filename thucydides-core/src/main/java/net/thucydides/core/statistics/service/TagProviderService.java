package net.thucydides.core.statistics.service;

import com.google.common.collect.Lists;
import sun.misc.Service;

import java.util.Iterator;
import java.util.List;

public class TagProviderService {
    public static List<TagProvider> getTagProviders() {
        List<TagProvider> tagProviders = Lists.newArrayList();

        Iterator<?> tagProviderImplementations = Service.providers(TagProvider.class);

        while (tagProviderImplementations.hasNext()) {
            tagProviders.add((TagProvider) tagProviderImplementations.next());
        }
        return tagProviders;
    }
}
