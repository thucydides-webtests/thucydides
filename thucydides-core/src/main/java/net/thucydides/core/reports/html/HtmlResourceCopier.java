package net.thucydides.core.reports.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.regex.Pattern;

import net.thucydides.core.resources.FileResources;
import net.thucydides.core.resources.ResourceList;

/**
 * Utility class that copies HTML resource files (images, stylesheets...) from a JAR to a target directory.
 *
 */
public class HtmlResourceCopier {
    
    private static final int BUFFER_SIZE = 4096;

    private String resourceDirectory;
    
    public HtmlResourceCopier(final String resourceDirectory) {
        super();
        this.resourceDirectory = resourceDirectory;
    }

    /**
     * Resources (stylesheets, images) etc are all stored in the
     * src/main/resources/reports directory. When the jar is deployed, they will
     * end up on the classpath.
     */
    public void copyHTMLResourcesTo(final File targetDirectory) throws IOException {

        Pattern resourcePattern = allFilesInDirectory(resourceDirectory);
        FileResources fileResource = FileResources.from(resourceDirectory);

        Collection<String> reportResources = ResourceList.getResources(resourcePattern);
        for (String resourcePath : reportResources) {
                if (resourceIsFromAJar(resourcePath)
                        && (thisIsNotTheRoot(resourcePath))
                        && (thisIsNotADirectory(resourcePath))) {
                	fileResource.copyResourceTo(resourcePath, targetDirectory);
                }
        }
    }

    private boolean thisIsNotADirectory(final String resourcePath) {
        return !resourcePath.endsWith("/");
    }

    private boolean thisIsNotTheRoot(final String resourcePath) {
        return !resourceDirectory.equals(resourcePath);
    }

    private Pattern allFilesInDirectory(final String directory) {
        String allFilesPattern = String.format(".*[\\\\/]?%s[\\\\/].*", directory);
        return Pattern.compile(allFilesPattern);
   }

    private boolean resourceIsFromAJar(final String resourcePath) {
        return !resourcePath.startsWith("/");
    }
}
