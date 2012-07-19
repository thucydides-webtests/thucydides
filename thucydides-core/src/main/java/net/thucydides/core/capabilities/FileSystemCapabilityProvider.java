package net.thucydides.core.capabilities;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.capabilities.model.Capability;
import net.thucydides.core.capabilities.model.Narrative;
import net.thucydides.core.capabilities.model.NarrativeReader;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Transient;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Load a set of capabilities (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 */
class FileSystemCapabilityProvider implements CapabilityProvider {

    private final static String DEFAULT_ROOT_DIRECTORY = "stories";
    private final static List<String> DEFAULT_CAPABILITY_TYPES = ImmutableList.of("capability","feature");

    private final String rootDirectoryPath;
    private final NarrativeReader narrativeReader;
    private final int level;
    private final EnvironmentVariables environmentVariables;

    @Transient
    private List<Capability> capabilities;

    public FileSystemCapabilityProvider() {
        this(DEFAULT_ROOT_DIRECTORY);
    }

    public FileSystemCapabilityProvider(String rootDirectory, int level) {
        this(rootDirectory, level, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public FileSystemCapabilityProvider(String rootDirectory, int level, EnvironmentVariables environmentVariables) {
        this.rootDirectoryPath = rootDirectory;
        this.level = level;
        this.narrativeReader = new NarrativeReader();
        this.environmentVariables = environmentVariables;
    }

    public FileSystemCapabilityProvider(String rootDirectory) {
        this(rootDirectory, 0);
    }

    public List<Capability> getCapabilities() {
        if (capabilities == null) {
            try {
                URL rootDirectoryUrl = getClass().getClassLoader().getResources(rootDirectoryPath).nextElement();
                File rootDirectory = new File(rootDirectoryUrl.getPath());
                File[] capabilityDirectories = rootDirectory.listFiles(thatAreDirectories());
                capabilities = loadCapabilitiesFrom(capabilityDirectories);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not load capabilities from '" + rootDirectoryPath + "'", e);
            }
        }
        return capabilities;
    }

    public List<TestTag> getTagsFor(final TestOutcome testOutcome) {
        String testOutcomePath = testOutcome.getPath();
        List<String> storyPathElements = IteratorUtils.toList(Splitter.on(".").split(stripRootPathFrom(testOutcomePath)).iterator());
        return getMatchingCapabilities(getCapabilities(), storyPathElements);
    }

    private List<TestTag> getMatchingCapabilities(List<Capability> capabilities, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            Optional<Capability> matchingCapability = findMatchingCapabilityIn(next(storyPathElements), capabilities);
            if (matchingCapability.isPresent()) {
                TestTag thisTag = TestTag.withName(matchingCapability.get().getName()).andType(matchingCapability.get().getType());
                List<TestTag> remainingTags = getMatchingCapabilities(matchingCapability.get().getChildren(), tail(storyPathElements));
                return concat(thisTag, remainingTags);
            } else {
                return Collections.EMPTY_LIST;
            }
        }
    }

    private List<TestTag> concat(TestTag thisTag, List<TestTag> remainingTags) {
        List<TestTag> totalTags = new ArrayList<TestTag>();
        totalTags.add(thisTag);
        totalTags.addAll(remainingTags);
        return totalTags;
    }

    private <T> T next(List<T> elements) {
        return elements.get(0);
    }

    private <T> List<T> tail(List<T> elements) {
        return elements.subList(1, elements.size());
    }

    private Optional<Capability> findMatchingCapabilityIn(String storyPathElement, List<Capability> capabilities) {
        for(Capability capability : capabilities) {
            String normalizedStoryPathElement = Inflector.getInstance().humanize(storyPathElement);
            if (capability.getName().equals(normalizedStoryPathElement)) {
                return Optional.of(capability);
            }
        }
        return Optional.absent();
    }

    private String stripRootPathFrom(String testOutcomePath) {
        String rootPath = ThucydidesSystemProperty.TEST_ROOT_PACKAGE.from(environmentVariables);
        if (testOutcomePath.startsWith(rootPath)) {
            return testOutcomePath.substring(rootPath.length() + 1);
        } else {
            return testOutcomePath;
        }
    }

    private List<Capability> loadCapabilitiesFrom(File[] capabilityDirectories) {
        return convert(capabilityDirectories, toCapabilities());
    }

    private Converter<File,Capability> toCapabilities() {
        return new Converter<File, Capability>() {

            @Override
            public Capability convert(File capabilityFileOrDirectory) {
                return readCapabilityFrom(capabilityFileOrDirectory);
            }
        };
    }

    private Capability readCapabilityFrom(File capabilityDirectory) {
        Optional<Narrative> capabilityNarrative = narrativeReader.loadFrom(capabilityDirectory);
        if (capabilityNarrative.isPresent()) {
            String title = getTitleFromNarrativeOrDirectoryName(capabilityNarrative.get(), capabilityDirectory);
            String type = capabilityNarrative.get().getType();
            List<Capability> children = readChildrenFrom(capabilityDirectory);
            return new Capability(title, type, capabilityNarrative.get().getText(), children);
        } else {
            String capabilityName = humanReadableVersionOf(capabilityDirectory.getName());
            List<Capability> children = readChildrenFrom(capabilityDirectory);
            return new Capability(capabilityName, getDefaultType(), capabilityName, children);
        }
    }

    private String getDefaultType() {
        List<String> types = getCapabilityTypes();
        if (level > types.size() - 1) {
            return types.get(types.size() - 1);
        } else {
            return types.get(level);
        }
    }

    private List<String> getCapabilityTypes() {
        String capabilityTypes = ThucydidesSystemProperty.CAPABILITY_TYPES.from(environmentVariables);
        if (StringUtils.isNotEmpty(capabilityTypes)) {
            Iterator<String> types = Splitter.on(",").trimResults().split(capabilityTypes).iterator();
            return IteratorUtils.toList(types);
        } else {
            return DEFAULT_CAPABILITY_TYPES;
        }
    }

    private List<Capability> readChildrenFrom(File capabilityDirectory) {
        String childDirectory = rootDirectoryPath + "/" + capabilityDirectory.getName();
        CapabilityProvider childReader = new FileSystemCapabilityProvider(childDirectory, level + 1, environmentVariables);
        return childReader.getCapabilities();
    }

    private String getTitleFromNarrativeOrDirectoryName(Narrative capabilityNarrative, File capabilityDirectory) {
        if (capabilityNarrative.getTitle().isPresent()) {
            return capabilityNarrative.getTitle().get();
        } else {
            return humanReadableVersionOf(capabilityDirectory.getName());
        }
    }

    private String humanReadableVersionOf(String name) {
        return Inflector.getInstance().humanize(name);
    }

    private FileFilter thatAreDirectories() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
    }
}
