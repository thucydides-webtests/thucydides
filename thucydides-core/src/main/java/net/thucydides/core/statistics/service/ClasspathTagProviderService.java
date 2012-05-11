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
//        tagProviders = findTagProviders();
    }

//    @Override
//    public List<TagProvider> getTagProviders() {
//        return ImmutableList.copyOf(tagProviders);
//    }

    @Override
    public List<TagProvider> getTagProviders() {
        List<TagProvider> tagProviders = Lists.newArrayList();

        ServiceLoader<TagProvider> serviceLoader = ServiceLoader.load(TagProvider.class);

        for (TagProvider aServiceLoader : serviceLoader) {
            tagProviders.add(aServiceLoader);
        }
        return tagProviders;
    }

//    private List<TagProvider> findTagProviders() {
//        List<TagProvider> tagProvidersOnClasspath = Lists.newArrayList();
//
//        tagProvidersOnClasspath.addAll(instancesOf(tagProvidersFromPackage("net.thucydides")));
//        addCustomTagProviders(tagProvidersOnClasspath);
//        return tagProvidersOnClasspath;
//    }
//
//    private void addCustomTagProviders(List<TagProvider> tagProvidersOnClasspath) {
//        String extensionPackages = ThucydidesSystemProperty.EXTENSION_PACKAGES.from(environmentVariables);
//        if (extensionPackages != null) {
//            Iterable<String> rootPackages = Splitter.on(",").trimResults().omitEmptyStrings().split(extensionPackages);
//            for (String rootPackage : rootPackages) {
//                tagProvidersOnClasspath.addAll(instancesOf(tagProvidersFromPackage(rootPackage)));
//            }
//        }
//    }
//
//    private List<TagProvider> instancesOf(Set<Class<? extends TagProvider>> classesWithTagProviderInterface) {
//        final List<TagProvider> tagProviders = Lists.newArrayList();
//
//        for (Class<? extends TagProvider> tagProviderClass : classesWithTagProviderInterface) {
//            try {
//                tagProviders.add(tagProviderClass.newInstance());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return tagProviders;
//    }
//
//    private Set<Class<? extends TagProvider>> tagProvidersFromPackage(final String packageName) {
//        final Set<Class<? extends TagProvider>> classesWithTagProviderInterface = Sets.newHashSet();
//
//        ComponentScanner scanner = new ComponentScanner();
//
//        scanner.getClasses(new ComponentQuery() {
//            protected void query() {
//                select().from(packageName).andStore(
//                        thoseImplementing(TagProvider.class).into(classesWithTagProviderInterface));
//            }
//        });
//        return classesWithTagProviderInterface;
//    }
}
