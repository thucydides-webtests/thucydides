package net.thucydides.core.requirements;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.requirements.model.NarrativeReader;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.statistics.service.TagProvider;
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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 */
public class FileSystemRequirementsTagProvider implements RequirementsTagProvider {

    private final static String DEFAULT_ROOT_DIRECTORY = "stories";
    private final static List<String> DEFAULT_CAPABILITY_TYPES = ImmutableList.of("capability","feature");

    private final String rootDirectoryPath;
    private final NarrativeReader narrativeReader;
    private final int level;
    private final EnvironmentVariables environmentVariables;

    @Transient
    private List<Requirement> requirements;

    public FileSystemRequirementsTagProvider() {
        this(DEFAULT_ROOT_DIRECTORY);
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level) {
        this(rootDirectory, level, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level, EnvironmentVariables environmentVariables) {
        this.rootDirectoryPath = rootDirectory;
        this.level = level;
        this.narrativeReader = new NarrativeReader();
        this.environmentVariables = environmentVariables;
    }

    public FileSystemRequirementsTagProvider(String rootDirectory) {
        this(rootDirectory, 0);
    }

    public List<Requirement> getRequirements() {
        if (requirements == null) {
            try {
                Enumeration<URL> requirementResources = getClass().getClassLoader().getResources(rootDirectoryPath);
                if (requirementResources.hasMoreElements()) {
                    URL rootDirectoryUrl = requirementResources.nextElement();
                    File rootDirectory = new File(rootDirectoryUrl.getPath());
                    File[] capabilityDirectories = rootDirectory.listFiles(thatAreDirectories());
                    requirements = loadCapabilitiesFrom(capabilityDirectories);
                    Collections.sort(requirements);
                } else {
                    requirements = Collections.EMPTY_LIST;
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not load requirements from '" + rootDirectoryPath + "'", e);
            }
        }
        return requirements;
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        Set<TestTag> tags = new HashSet<TestTag>();
        if (testOutcome.getPath() != null) {
            List<String> storyPathElements
                    = IteratorUtils.toList(Splitter.on(".")
                                                   .split(stripRootPathFrom(testOutcome.getPath())).iterator());
            tags.addAll(getMatchingCapabilities(getRequirements(), storyPathElements));
        }
        return tags;
    }

    private List<TestTag> getMatchingCapabilities(List<Requirement> requirements, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            Optional<Requirement> matchingCapability = findMatchingCapabilityIn(next(storyPathElements), requirements);
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

    private Optional<Requirement> findMatchingCapabilityIn(String storyPathElement, List<Requirement> requirements) {
        for(Requirement requirement : requirements) {
            String normalizedStoryPathElement = Inflector.getInstance().humanize(storyPathElement);
            if (requirement.getName().equals(normalizedStoryPathElement)) {
                return Optional.of(requirement);
            }
        }
        return Optional.absent();
    }

    private String stripRootPathFrom(String testOutcomePath) {
        String rootPath = ThucydidesSystemProperty.TEST_ROOT_PACKAGE.from(environmentVariables);
        if (rootPath != null && testOutcomePath.startsWith(rootPath)) {
            return testOutcomePath.substring(rootPath.length() + 1);
        } else {
            return testOutcomePath;
        }
    }

    private List<Requirement> loadCapabilitiesFrom(File[] capabilityDirectories) {
        return convert(capabilityDirectories, toCapabilities());
    }

    private Converter<File,Requirement> toCapabilities() {
        return new Converter<File, Requirement>() {

            @Override
            public Requirement convert(File capabilityFileOrDirectory) {
                return readCapabilityFrom(capabilityFileOrDirectory);
            }
        };
    }

    private Requirement readCapabilityFrom(File capabilityDirectory) {
        Optional<Narrative> capabilityNarrative = narrativeReader.loadFrom(capabilityDirectory);
        if (capabilityNarrative.isPresent()) {
            return requirementWithNarrative(capabilityDirectory, capabilityNarrative);
        } else {
            return requirementFromDirectoryName(capabilityDirectory);
        }
    }

    private Requirement requirementFromDirectoryName(File capabilityDirectory) {
        String shortName = humanReadableVersionOf(capabilityDirectory.getName());
        List<Requirement> children = readChildrenFrom(capabilityDirectory);
        return Requirement.named(shortName).withType(getDefaultType()).withNarrativeText(shortName).withChildren(children);
    }

    private Requirement requirementWithNarrative(File capabilityDirectory, Optional<Narrative> capabilityNarrative) {
        String shortName = humanReadableVersionOf(capabilityDirectory.getName());
        String displayName = getTitleFromNarrativeOrDirectoryName(capabilityNarrative.get(), capabilityDirectory);
        String cardNumber = capabilityNarrative.get().getCardNumber().orNull();
        String type = capabilityNarrative.get().getType();
        List<Requirement> children = readChildrenFrom(capabilityDirectory);
        return Requirement.named(shortName)
                          .withOptionalDisplayName(displayName)
                          .withOptionalCardNumber(cardNumber)
                          .withType(type)
                          .withNarrativeText(capabilityNarrative.get().getText())
                          .withChildren(children);
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

    private List<Requirement> readChildrenFrom(File capabilityDirectory) {
        String childDirectory = rootDirectoryPath + "/" + capabilityDirectory.getName();
        RequirementsTagProvider childReader = new FileSystemRequirementsTagProvider(childDirectory, level + 1, environmentVariables);
        return childReader.getRequirements();
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
