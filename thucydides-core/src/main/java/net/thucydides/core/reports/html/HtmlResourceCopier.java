package net.thucydides.core.reports.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.regex.Pattern;

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
        Collection<String> reportResources = ResourceList.getResources(resourcePattern);
        for (String resourcePath : reportResources) {
            String targetSubDirectory = findTargetSubDirectoryFrom(resourcePath);
            File copyTo = new File(targetDirectory, targetSubDirectory);
            if (resourceIsFromAJar(resourcePath) 
                && (thisIsNotTheRoot(resourcePath)) 
                && (thisIsNotADirectory(resourcePath))) {
                copyFileFromClasspathToTargetDirectory(resourcePath, copyTo);
            }
        }
    }

    private boolean thisIsNotADirectory(final String resourcePath) {
        return !resourcePath.endsWith("/");
    }

    private boolean thisIsNotTheRoot(final String resourcePath) {
        return !resourceDirectory.equals(resourcePath);
    }

    private String findTargetSubDirectoryFrom(final String resourcePath) 
      throws IOException {
        if (resourcePath.startsWith(resourceDirectory)) {
            int subDirectoryStartsAt = resourceDirectory.length() + 1;
            String resourcePathName = resourcePath.substring(subDirectoryStartsAt);
            if (resourcePathName.endsWith("/")) {
                return resourcePathName;
            } else {
                File targetDirectory = new File(resourcePathName).getParentFile();
                if (targetDirectory != null) {
                    return targetDirectory.getPath();
                }
            }
        }
        return "";
    }

    private Pattern allFilesInDirectory(final String directory) {
        return Pattern.compile(".*" + directory + "/.*");
   }

    private void copyFileFromClasspathToTargetDirectory(final String resourcePath,
            final File targetDirectory) throws IOException {
        
        File resourceOnClasspath = new File(resourcePath);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        File destinationFile = new File(targetDirectory, resourceOnClasspath.getName());
        if (destinationFile.getParent() != null) {
            new File(destinationFile.getParent()).mkdirs();
        }
        FileOutputStream out = new FileOutputStream(destinationFile);
        try {
            byte[] buffer = new byte[BUFFER_SIZE];  
            int bytesRead;  
            while ((bytesRead = in.read(buffer)) != -1) {  
                out.write(buffer, 0, bytesRead);  
            }  
        } finally {
            in.close();  
            out.close(); 
        }
    }

    private boolean resourceIsFromAJar(final String resourcePath) {
        return !resourcePath.startsWith("/");
    }
}
