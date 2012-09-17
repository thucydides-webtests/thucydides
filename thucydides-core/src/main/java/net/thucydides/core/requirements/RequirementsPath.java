package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import org.hamcrest.text.StringStartsWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.collections.IteratorUtils.toList;

public class RequirementsPath {

    private final static Pattern PATH_SEPARATORS = Pattern.compile("[\\\\/.]");
    private final static Pattern FILE_SYSTEM_PATH_SEPARATORS = Pattern.compile("[\\\\/]");

    public static List<String> stripRootFromPath(String root, List<String> storyPathElements) {
        List<String> rootElements = pathElements(root);
        if (thePathIn(storyPathElements).startsWith(rootElements)) {
            return storyPathElements.subList(rootElements.size(), storyPathElements.size());
        } else {
            return storyPathElements;
        }
    }

    private static PathStartsWith thePathIn(List<String> storyPathElements) {
        return new PathStartsWith(storyPathElements);
    }

    public static List<String> pathElements(String path) {
        return toList(Splitter.on(PATH_SEPARATORS).omitEmptyStrings().trimResults().split(path).iterator());
    }

    public static List<String> fileSystemPathElements(String path) {
        return toList(Splitter.on(FILE_SYSTEM_PATH_SEPARATORS).omitEmptyStrings().trimResults().split(path).iterator());
    }

    private static class PathStartsWith {
        private List<String> storyPathElements;

        public PathStartsWith(List<String> storyPathElements) {
            this.storyPathElements = storyPathElements;
        }

        public boolean startsWith(List<String> rootElements) {
            if (storyPathElements.size() >= rootElements.size()) {
                int elementIndex = 0;
                for(String pathElement : storyPathElements) {
                    if (!pathElement.equals(rootElements.get(elementIndex++))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
