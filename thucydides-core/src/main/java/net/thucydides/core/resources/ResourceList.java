package net.thucydides.core.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Utility class to read report resources from the classpath. This way, report
 * resources such as images and stylesheets can be shipped in a separate JAR
 * file.
 */
public final class ResourceList {

    /**
     * This is a utility class - don't instanciate.
     */
    private ResourceList() {
    }

    /**
     * Find a list of resources matching a given path on the classpath. for all
     * elements of java.class.path get a Collection of resources Pattern pattern
     * = Pattern.compile(".*"); gets all resources
     * 
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> getResources(final Pattern pattern) {
        final ArrayList<String> resources = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(":");
        for (final String element : classPathElements) {
            resources.addAll(getResources(element, pattern));
        }
        return resources;
    }

    private static Collection<String> getResources(final String element, final Pattern pattern) {
        final ArrayList<String> resources = new ArrayList<String>();
        final File file = new File(element);
        if (isAJarFile(file)) {
            resources.addAll(getResourcesFromJarFile(file, pattern));
        } else {
            resources.addAll(getResourcesFromDirectory(file, pattern));
        }
        return resources;
    }
    
    private static boolean isAJarFile(final File file) {
        if (file.isDirectory()) {
            return false;
        } else if ((file.getName().endsWith(".jar")) || (file.getName().endsWith(".zip"))) {
            return true;
        }
        return false;
    }

    private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        if (file.exists()) {
            ZipFile zf;
            try {
                zf = new ZipFile(file);
            } catch (final ZipException e) {
                throw new Error(e);
            } catch (final IOException e) {
                throw new Error(e);
            }
            @SuppressWarnings("rawtypes")
            final Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = (ZipEntry) e.nextElement();
                final String fileName = ze.getName();
                final boolean accept = pattern.matcher(fileName).matches();
                if (accept) {
                    retval.add(fileName);
                }
            }
            try {
                zf.close();
            } catch (final IOException e1) {
                throw new Error(e1);
            }
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(final File directory,
                                                                final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (final File file : fileList) {
                if (file.isDirectory() && (file.exists())) {
                    retval.addAll(getResourcesFromDirectory(file, pattern));
                } else {
                    if (file.exists()) {
                        try {
                            final String fileName = file.getCanonicalPath();
                            final boolean accept = pattern.matcher(fileName).matches();
                            if (accept) {
                                retval.add(fileName);
                            }
                        } catch (final IOException e) {
                            throw new Error(e);
                        }
                    }
                }
            }
        }
        return retval;
    }
}
