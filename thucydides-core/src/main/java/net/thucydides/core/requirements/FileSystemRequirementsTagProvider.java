package net.thucydides.core.requirements;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.requirements.model.NarrativeReader;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.requirements.RequirementsPath.pathElements;
import static net.thucydides.core.requirements.RequirementsPath.stripRootFromPath;

//import javax.persistence.Transient;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 * By default, the tests
 */
public class FileSystemRequirementsTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider {

    private final static String DEFAULT_ROOT_DIRECTORY = "stories";
    private final static String DEFAULT_RESOURCE_DIRECTORY = "src/test/resources";
    private static final String WORKING_DIR = "user.dir";
    private static final List<Requirement> NO_REQUIREMENTS = Lists.newArrayList();
    private static final List<TestTag> NO_TEST_TAGS = Lists.newArrayList();

    private final String rootDirectoryPath;
    private final NarrativeReader narrativeReader;
    private final int level;

//    @Transient
    private List<Requirement> requirements;

    public FileSystemRequirementsTagProvider() {
        this(getDefaultRootDirectoryPathFrom(Injectors.getInjector().getInstance(EnvironmentVariables.class)));
    }

    public static String getDefaultRootDirectoryPathFrom(EnvironmentVariables environmentVariables) {

        if (ThucydidesSystemProperty.REQUIREMENTS_DIRECTORY.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.REQUIREMENTS_DIRECTORY.from(environmentVariables);
        }
        if (ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables);
        }
        return DEFAULT_ROOT_DIRECTORY;
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level) {
        this(filePathFormOf(rootDirectory), level, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    /**
     * Convert a package name to a file path if necessary.
     */
    private static String filePathFormOf(String rootDirectory) {
        if (rootDirectory.contains(".")) {
            return rootDirectory.replace(".","/");
        } else {
            return rootDirectory;
        }
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level, EnvironmentVariables environmentVariables) {
        super(environmentVariables);
        this.rootDirectoryPath = rootDirectory;
        this.level = level;
        this.narrativeReader = NarrativeReader.forRootDirectory(rootDirectory)
                .withRequirementTypes(getRequirementTypes());
    }

    public FileSystemRequirementsTagProvider(String rootDirectory) {
        this(rootDirectory, 0);
    }

    /**
     * We look for file system requirements in the root directory path (by default, 'stories').
     * First, we look on the classpath. If we don't find anything on the classpath (e.g. if the task is
     * being run from the Maven plugin), we look in the src/main/resources and src/test/resources directories starting
     * at the working directory.
     */
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            URL rootDirectoryPath = null;
            try {
                Optional<String> directoryPath = getRootDirectoryPath();
                if (directoryPath.isPresent()) {
                    File rootDirectory = new File(directoryPath.get());
                    List<Requirement> allRequirements = Lists.newArrayList();
                    allRequirements.addAll(loadCapabilitiesFrom(rootDirectory.listFiles(thatAreDirectories())));
                    allRequirements.addAll(loadStoriesFrom(rootDirectory.listFiles(thatAreStories())));
                    Collections.sort(allRequirements);
                    requirements = allRequirements;
                } else {
                    requirements = NO_REQUIREMENTS;
                }
            } catch (IOException e) {
                requirements = NO_REQUIREMENTS;
                throw new IllegalArgumentException("Could not load requirements from '" + rootDirectoryPath + "'", e);
            }
        }
        return requirements;
    }

    private Optional<String> getRootDirectoryPath() throws IOException {

        if (ThucydidesSystemProperty.TEST_REQUIREMENTS_ROOT.isDefinedIn(environmentVariables)){
            return getRootDirectoryFromRequirementsBaseDir();
        } else {
            Optional<String> rootDirectoryOnClasspath = getRootDirectoryFromClasspath();

            if (rootDirectoryOnClasspath.isPresent()) {
                return rootDirectoryOnClasspath;
            } else {
                return getRootDirectoryFromWorkingDirectory();
            }
        }
    }

    private Optional<String> getRootDirectoryFromClasspath() throws IOException {
        Enumeration<URL> requirementResources = getDirectoriesFrom(rootDirectoryPath);
        if (requirementResources.hasMoreElements()) {
            return Optional.of(withRestoredSpaces(requirementResources.nextElement().getPath()));
        } else {
            return Optional.absent();
        }
    }

    private String withRestoredSpaces(String path) {
        return StringUtils.replace(path,"%20"," ");
    }

    private Optional<String> getRootDirectoryFromWorkingDirectory() throws IOException {
        return getRootDirectoryFromParentDir(System.getProperty(WORKING_DIR));
    }

    private Optional<String> configuredRelativeRootDirectory;
    private Optional<String> getRootDirectoryFromRequirementsBaseDir() {
        if (configuredRelativeRootDirectory == null) {
            configuredRelativeRootDirectory
                    = getRootDirectoryFromParentDir(ThucydidesSystemProperty.TEST_REQUIREMENTS_ROOT
                                                                             .from(environmentVariables,""));
        }
        return configuredRelativeRootDirectory;
    }

    private Optional<String> getRootDirectoryFromParentDir(String parentDir) {
        File resourceDirectory = getResourceDirectory().isPresent() ? new File(parentDir, getResourceDirectory().get()) : new File(parentDir);
        File requirementsDirectory = absolutePath(rootDirectoryPath) ? new File(rootDirectoryPath) : new File(resourceDirectory, rootDirectoryPath);
        if (requirementsDirectory.exists()) {
            return Optional.of(requirementsDirectory.getAbsolutePath());
        } else {
            return Optional.absent();
        }
    }

    private boolean absolutePath(String rootDirectoryPath) {
        return (new File(rootDirectoryPath).isAbsolute() || rootDirectoryPath.startsWith("/"));
    }


    private Enumeration<URL> getDirectoriesFrom(String root) throws IOException {
        return getClass().getClassLoader().getResources(root);
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        Set<TestTag> tags = new HashSet<TestTag>();
        if (testOutcome.getPath() != null) {
            List<String> storyPathElements = stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath())));
            addStoryTagIfPresent(tags, storyPathElements);
            storyPathElements = stripStorySuffixFrom(storyPathElements);
            tags.addAll(getMatchingCapabilities(getRequirements(), storyPathElements));
        }
        return tags;
    }

    private List<String> stripStorySuffixFrom(List<String> pathElements) {
        if ((!pathElements.isEmpty()) && (last(pathElements).toLowerCase().equals("story"))) {
            return dropLastElement(pathElements);
        } else {
            return pathElements;
        }
    }

    private List<String> dropLastElement(List<String> pathElements) {
        List<String> strippedPathElements = Lists.newArrayList(pathElements);
        strippedPathElements.remove(pathElements.size() - 1);
        return strippedPathElements;
    }

    private void addStoryTagIfPresent(Set<TestTag> tags, List<String> storyPathElements) {
        Optional<TestTag> storyTag = storyTagFrom(storyPathElements);
        tags.addAll(storyTag.asSet());
    }

    private Optional<TestTag> storyTagFrom(List<String> storyPathElements) {
        if (!storyPathElements.isEmpty() && (last(storyPathElements).equals("story"))) {
            String storyName = Lists.reverse(storyPathElements).get(1);
            TestTag storyTag = TestTag.withName(NameConverter.humanize(storyName)).andType("story");
            return Optional.of(storyTag);
        } else {
            return Optional.absent();
        }
    }

    private String last(List<String> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    public Optional<Requirement> getParentRequirementOf(final TestOutcome testOutcome) {

        if (testOutcome.getPath() != null) {
            List<String> storyPathElements = stripStorySuffixFrom(stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath()))));
            return lastRequirementFrom(storyPathElements);
        } else {
            return mostSpecificTagRequirementFor(testOutcome);
        }
    }

    private Optional<Requirement> mostSpecificTagRequirementFor(TestOutcome testOutcome) {
        Optional<Requirement> mostSpecificRequirement = Optional.absent();
        int currentSpecificity = -1;

        for(TestTag tag : testOutcome.getTags()) {
            Optional<Requirement> matchingRequirement = getRequirementFor(tag);
            if (matchingRequirement.isPresent()) {
                int specificity = requirementsConfiguration.getRequirementTypes().indexOf(matchingRequirement.get().getType());
                if (currentSpecificity < specificity) {
                    currentSpecificity = specificity;
                    mostSpecificRequirement = matchingRequirement;
                }
            }

        }
        return mostSpecificRequirement;
    }

    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        for(Requirement requirement : getFlattenedRequirements()) {
            if (requirement.getName().equalsIgnoreCase(testTag.getName()) && requirement.getType().equalsIgnoreCase(testTag.getType())) {
                return Optional.of(requirement);
            }
        }
        return Optional.absent();
    }

    private List<Requirement> getFlattenedRequirements() {
        List<Requirement> allRequirements = Lists.newArrayList();
        for(Requirement requirement : getRequirements()) {
            allRequirements.add(requirement);
            allRequirements.addAll(childRequirementsOf(requirement));
        }
        return allRequirements;
    }

    private Collection<Requirement> childRequirementsOf(Requirement requirement) {
        List<Requirement> childRequirements = Lists.newArrayList();
        for(Requirement childRequirement : requirement.getChildren()) {
            childRequirements.add(childRequirement);
            childRequirements.addAll(childRequirementsOf(childRequirement));
        }
        return childRequirements;
    }

    private Optional<Requirement> lastRequirementFrom(List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Optional.absent();
        } else {
            return lastRequirementMatchingPath(getRequirements(), storyPathElements);
        }
    }

    private Optional<Requirement> lastRequirementMatchingPath(List<Requirement> requirements, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Optional.absent();
        }
        Optional<Requirement> matchingRequirement = findMatchingRequirementIn(next(storyPathElements), requirements);
        if (!matchingRequirement.isPresent()) {
            return Optional.absent();
        }
        if (tail(storyPathElements).isEmpty()) {
            return matchingRequirement;
        }
        List<Requirement> childRequrements = matchingRequirement.get().getChildren();
        return lastRequirementMatchingPath(childRequrements, tail(storyPathElements));
    }

    private List<TestTag> getMatchingCapabilities(List<Requirement> requirements, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return NO_TEST_TAGS;
        } else {
            Optional<Requirement> matchingRequirement = findMatchingRequirementIn(next(storyPathElements), requirements);
            if (matchingRequirement.isPresent()) {
                TestTag thisTag = TestTag.withName(matchingRequirement.get().getName()).andType(matchingRequirement.get().getType());
                List<TestTag> remainingTags = getMatchingCapabilities(matchingRequirement.get().getChildren(), tail(storyPathElements));
                return concat(thisTag, remainingTags);
            } else {
                return NO_TEST_TAGS;
            }
        }
    }

    private List<String> stripRootFrom(List<String> storyPathElements) {
        return stripRootFromPath(rootDirectoryPath, storyPathElements);
    }

    private String stripRootPathFrom(String testOutcomePath) {
        String rootPath = ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables);
        if (rootPath != null && testOutcomePath.startsWith(rootPath) && (!testOutcomePath.equals(rootPath))) {
            return testOutcomePath.substring(rootPath.length() + 1);
        } else {
            return testOutcomePath;
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

    private Optional<Requirement> findMatchingRequirementIn(String storyPathElement, List<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            String normalizedStoryPathElement = Inflector.getInstance().humanize(Inflector.getInstance().underscore(storyPathElement));
            if (requirement.getName().equals(normalizedStoryPathElement)) {
                return Optional.of(requirement);
            }
        }
        return Optional.absent();
    }

    private List<Requirement> loadCapabilitiesFrom(File[] requirementDirectories) {
        return convert(requirementDirectories, toRequirements());
    }


    private List<Requirement> loadStoriesFrom(File[] storyFiles) {
        return convert(storyFiles, toStoryRequirements());
    }

    private Converter<File, Requirement> toRequirements() {
        return new Converter<File, Requirement>() {

            public Requirement convert(File requirementFileOrDirectory) {
                return readRequirementFrom(requirementFileOrDirectory);
            }
        };
    }

    private Converter<File, Requirement> toStoryRequirements() {
        return new Converter<File, Requirement>() {

            public Requirement convert(File storyFile) {
                return readRequirementsFromStoryFile(storyFile);
            }
        };
    }

    private Requirement readRequirementFrom(File requirementDirectory) {
        Optional<Narrative> requirementNarrative = narrativeReader.loadFrom(requirementDirectory, level);

        if (requirementNarrative.isPresent()) {
            return requirementWithNarrative(requirementDirectory,
                                            humanReadableVersionOf(requirementDirectory.getName()),
                                            requirementNarrative.get());
        } else {
            return requirementFromDirectoryName(requirementDirectory);
        }
    }

    private Requirement readRequirementsFromStoryFile(File storyFile) {
        Optional<Narrative> optionalNarrative = narrativeReader.loadFromStoryFile(storyFile);
        String storyName = storyFile.getName().replace(".story","");
        if (optionalNarrative.isPresent()) {
            return requirementWithNarrative(storyFile, humanReadableVersionOf(storyName), optionalNarrative.get());
        } else {
            return storyNamed(storyName);
        }
    }

    private Requirement requirementFromDirectoryName(File requirementDirectory) {
        String shortName = humanReadableVersionOf(requirementDirectory.getName());
        List<Requirement> children = readChildrenFrom(requirementDirectory);
        return Requirement.named(shortName).withType(getDefaultType(level)).withNarrativeText(shortName).withChildren(children);
    }

    private Requirement storyNamed(String storyName) {
        String shortName = humanReadableVersionOf(storyName);
        return Requirement.named(shortName).withType("story").withNarrativeText(shortName);
    }

    private Requirement requirementWithNarrative(File requirementDirectory, String shortName, Narrative requirementNarrative) {
        String displayName = getTitleFromNarrativeOrDirectoryName(requirementNarrative, shortName);
        String cardNumber = requirementNarrative.getCardNumber().orNull();
        String type = requirementNarrative.getType();
        List<String> releaseVersions = requirementNarrative.getVersionNumbers();
        List<Requirement> children = readChildrenFrom(requirementDirectory);
        return Requirement.named(shortName)
                .withOptionalDisplayName(displayName)
                .withOptionalCardNumber(cardNumber)
                .withType(type)
                .withNarrativeText(requirementNarrative.getText())
                .withReleaseVersions(releaseVersions)
                .withChildren(children);
    }

    private List<Requirement> readChildrenFrom(File requirementDirectory) {
        String childDirectory = rootDirectoryPath + "/" + requirementDirectory.getName();
        RequirementsTagProvider childReader = new FileSystemRequirementsTagProvider(childDirectory, level + 1, environmentVariables);
        return childReader.getRequirements();
    }

    private String getTitleFromNarrativeOrDirectoryName(Narrative requirementNarrative, String nameIfNoNarrativePresent) {
        if (requirementNarrative.getTitle().isPresent()) {
            return requirementNarrative.getTitle().get();
        } else {
            return nameIfNoNarrativePresent;
        }
    }

    private FileFilter thatAreDirectories() {
        return new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().startsWith(".");
            }
        };
    }

    private FileFilter thatAreStories() {
        return new FileFilter() {
            public boolean accept(File file) {
                String filename = file.getName().toLowerCase();
                if (filename.startsWith("given") || filename.startsWith("precondition")) {
                    return false;
                } else {
                    return file.getName().toLowerCase().endsWith(".story");
                }
            }
        };
    }

    public Optional<String> getResourceDirectory() {
        if (ThucydidesSystemProperty.REQUIREMENTS_DIRECTORY.isDefinedIn(environmentVariables)) {
            return Optional.absent();
        } else {
            return Optional.of(DEFAULT_RESOURCE_DIRECTORY);
        }
    }
}
