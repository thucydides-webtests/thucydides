package net.thucydides.core.reflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return filtered(classes);
    }

    private List<Class<?>> filtered(List<Class<?>> classes) {
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
}