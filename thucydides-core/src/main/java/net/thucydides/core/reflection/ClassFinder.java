package net.thucydides.core.reflection;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Load classes from a given package.
 */
public class ClassFinder {

    private final ClassLoader classLoader;
    private final Class annotation;

    private ClassFinder(ClassLoader classLoader, Class annotation) {
        this.classLoader = classLoader;
        this.annotation = annotation;
    }

    private ClassFinder(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public static ClassFinder loadClasses() {
        return new ClassFinder(getDefaultClassLoader());
    }

    public ClassFinder withClassLoader(ClassLoader classLoader) {
        return new ClassFinder(classLoader);
    }

    public ClassFinder annotatedWith(Class annotation) {
        return new ClassFinder(this.classLoader, annotation);
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     */
    public List<Class<?>> fromPackage(String packageName) {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classResourcesOn(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
            classes.addAll(getClassesForPackage(packageName));
        }
        return filtered(new ArrayList<Class<?>>(classes));
    }

    private List<Class<?>> filtered(List<? extends Class<?>> classes) {
        List<Class<?>> matchingClasses = new ArrayList<Class<?>>();

        for(Class clazz : classes) {
            if (matchesConstraints(clazz)) {
                matchingClasses.add(clazz);
            }
        }
        return matchingClasses;
    }

    private boolean matchesConstraints(Class clazz) {
        if (annotation == null) {
            return true;
        } else {
            return (clazz.getAnnotation(annotation) != null);
        }
    }

    private Enumeration<URL> classResourcesOn(String path) {
        try {
            return getClassLoader().getResources(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not access class path at " + path, e);
        }
    }

    private List<Class<?>> getClassesForPackage(String packageName) {

        PackageDirectory rootPackage = new PackageDirectory(packageName);
        if (!rootPackage.exists()) {
            return Collections.EMPTY_LIST;
        } else {
            List<Class<?>> classes = new ArrayList<Class<?>>();

            if (rootPackage.directoryExists()) {
                addClassesInPackageDirectory(rootPackage, classes);
            } else {
                addClassesFromJarAt(rootPackage, classes);
            }
            return classes;
        }
    }

    private void addClassesFromJarAt(PackageDirectory rootPackage, List<Class<?>> classes) {
        try {
            JarFile jarFile = new JarFile(rootPackage.getJarPath());
            Enumeration<JarEntry> entries = jarFile.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryIsAClassIn(rootPackage, entryName)) {
                    try {
                        classes.add(Class.forName(classNameForJarEntry(entryName)));
                    }
                    catch (ClassNotFoundException e) {
                        throw new RuntimeException("Could not load class", e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(rootPackage.getName() + " (" + rootPackage.getDirectory() + ") does not appear to be a valid package", e);
        }
    }

    private boolean entryIsAClassIn(PackageDirectory rootPackage, String entryName) {
        return (entryName.startsWith(rootPackage.getRelativePath())
                && entryName.length() > (rootPackage.getRelativePath().length() + 1)
                && entryName.endsWith(".class"));
    }

    private String classNameForJarEntry(String entryName) {
        return entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
    }

    private void addClassesInPackageDirectory(PackageDirectory rootPackage, List<Class<?>> classes) {
        for(String filename : rootPackage.getDirectory().list()) {
            if (filename.endsWith(".class")) {
                String className = classNameFor(rootPackage.getName(), filename);
                try {
                    classes.add(Class.forName(className));
                }
                catch (ClassNotFoundException e) {
                    throw new RuntimeException("ClassNotFoundException loading " + className);
                }
            }
        }
    }

    private String classNameFor(String packageName, String filename) {
        return packageName + '.' + filename.substring(0, filename.length() - 6);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println("find classes in " + file);
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class") && isNotAnInnerClass(file.getName())) {
                    classes.add(correspondingClass(packageName, file));
                }
            }
        }
        return classes;
    }

    private Class<?> correspondingClass(String packageName, File file) {
        try {
            String fullyQualifiedClassName = packageName + '.' + simpleClassNameOf(file);
            return getClassLoader().loadClass(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find or access class for " + file, e);
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private String simpleClassNameOf(File file) {
        return file.getName().substring(0, file.getName().length() - 6);
    }

    private boolean isNotAnInnerClass(String className) {
        return (!className.contains("$"));
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }


    private static class PackageDirectory {
        private final Package rootPackage;
        private final String packageName;
        private final String relativePath;

        private PackageDirectory(String packageName) {
            this.packageName = packageName;
            this.rootPackage = Package.getPackage(packageName);
            this.relativePath =  packageName.replace('.','/');
        }

        public boolean exists() {
            return (rootPackage != null);
        }

        public String getName() {
            return packageName;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getFullPath() {
            URL resource = ClassLoader.getSystemClassLoader().getResource(relativePath);
            return resource.getFile();
        }

        public String getJarPath() {
            return getFullPath().replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        }

        public File getDirectory() {
            URL resource = ClassLoader.getSystemClassLoader().getResource(relativePath);
            try {
                return new File(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Illegal URL for resource read from classpath: ",e);
            }
        }

        public boolean isJarResource() {
            URL resource = ClassLoader.getSystemClassLoader().getResource(relativePath);
            try {
                return resource.toURI().getScheme().equals("jar");
            } catch (URISyntaxException e) {
                throw new RuntimeException("Illegal URL for resource read from classpath: ",e);
            }
        }

        public boolean directoryExists() {
            return !isJarResource() && getDirectory().exists();
        }

    }
}

