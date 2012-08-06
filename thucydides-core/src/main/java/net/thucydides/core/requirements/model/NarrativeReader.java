package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import static net.thucydides.core.requirements.RequirementsPath.fileSystemPathElements;
import static net.thucydides.core.requirements.RequirementsPath.pathElements;
import static net.thucydides.core.requirements.RequirementsPath.stripRootFromPath;

/**
 * Load a narrative text from a directory.
 * A narrative is a text file that describes a capability, feature, or epic, or whatever terms you are using in your
 * project. The directory structure itself is used to organize capabilities into features, and so on. At the leaf
 * level, the directory will contain story files (e.g. JBehave stories, JUnit test cases, etc). At each level, a
 * "narrative.txt" file provides a description.
 *
 */
public class NarrativeReader {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String TITLE_SEPARATOR = ":";

    private final String rootDirectory;
    private final List<String> capabilityTypes;

    protected NarrativeReader(String rootDirectory, List<String> capabilityTypes) {
        this.rootDirectory = rootDirectory;
        this.capabilityTypes = ImmutableList.copyOf(capabilityTypes);
    }

    public static NarrativeReader forRootDirectory(String rootDirectory) {
        return new NarrativeReader(rootDirectory, FileSystemRequirementsTagProvider.DEFAULT_CAPABILITY_TYPES);
    }

    public NarrativeReader withCapabilityTypes(List<String> capabilityTypes) {
        return new NarrativeReader(this.rootDirectory, capabilityTypes);
    }

    public Optional<Narrative> loadFrom(File directory) {
        return loadFrom(directory, 0);
    }

    public Optional<Narrative> loadFrom(File directory, int requirementsLevel) {
        File[] narrativeFiles = directory.listFiles(calledNarrativeDotTxt());
        if (narrativeFiles.length == 0) {
            return Optional.absent();
        } else {
            return narrativeLoadedFrom(narrativeFiles[0], requirementsLevel);
        }
    }

    private Optional<Narrative> narrativeLoadedFrom(File narrativeFile, int requirementsLevel) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(narrativeFile));

            String title = null;
            String cardNumber = null;
            Optional<String> titleLine = readOptionalTitleLine(reader);
            if (titleLine.isPresent()) {
                title = readTitleFrom(titleLine.get());
                cardNumber = findCardNumberIn(titleLine.get());
            }
            String text = readDescriptionFrom(reader);
            String type = directoryLevelInRequirementsHierarchy(narrativeFile, requirementsLevel);
            reader.close();
            return Optional.of(new Narrative(Optional.fromNullable(title), Optional.fromNullable(cardNumber), type, text));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return Optional.absent();
    }

    private String directoryLevelInRequirementsHierarchy(File narrativeFile, int requirementsLevel) {
        String normalizedNarrativePath = normalized(narrativeFile.getAbsolutePath());
        String normalizedRootPath = normalized(rootDirectory);
        int rootDirectoryStart = normalizedNarrativePath.lastIndexOf(normalizedRootPath);
        int rootDirectoryEnd = (rootDirectoryStart >= 0) ? rootDirectoryStart + normalizedRootPath.length() : 0;
        String relativeNarrativePath = normalizedNarrativePath.substring(rootDirectoryEnd);
        int directoryCount = fileSystemPathElements(relativeNarrativePath).size() - 1;
        int level = requirementsLevel + directoryCount - 1;

        return getRequirementTypeForLevel(level);
    }

    private String normalized(String absolutePath) {
        return absolutePath.replaceAll(IOUtils.LINE_SEPARATOR, IOUtils.LINE_SEPARATOR_UNIX);
    }

    private String getRequirementTypeForLevel(int level) {
        if (level > capabilityTypes.size() - 1) {
            return capabilityTypes.get(capabilityTypes.size() - 1);
        } else {
            return capabilityTypes.get(level);
        }
    }


    private Optional<String> readOptionalTitleLine(BufferedReader reader) throws IOException {
        String titleLine = reader.readLine();
        if (titleLine.contains(TITLE_SEPARATOR)) {
            return Optional.of(titleLine);
        } else {
            return Optional.absent();
        }
    }

    private String findCardNumberIn(String titleLine) {
        List<String> issues = Formatter.issuesIn(titleLine);
        if (!issues.isEmpty()) {
            return issues.get(0);
        } else {
            return null;
        }
    }

    private String readTitleFrom(String titleLine) throws IOException {
        int separatorAt = titleLine.indexOf(TITLE_SEPARATOR);
        if (separatorAt > 0) {
            return titleLine.substring(separatorAt + 1).trim();
        } else {
            return null;
        }
    }

    private String readDescriptionFrom(BufferedReader reader) throws IOException {
        StringBuilder description = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            description.append(line);
            description.append(NEW_LINE);
        }
        return description.toString();
    }

    private FilenameFilter calledNarrativeDotTxt() {
        return new FilenameFilter() {

            @Override
            public boolean accept(File file, String name) {
                return name.toLowerCase().equals("narrative.txt");
            }
        };
    }
}
