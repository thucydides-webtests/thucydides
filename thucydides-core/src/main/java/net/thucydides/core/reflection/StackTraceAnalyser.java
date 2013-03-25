package net.thucydides.core.reflection;

import java.lang.reflect.Method;


public class StackTraceAnalyser {

    private final StackTraceElement stackTraceElement;

    private StackTraceAnalyser(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public static StackTraceAnalyser forStackTraceElement(StackTraceElement stackTraceElement) {
        return new StackTraceAnalyser(stackTraceElement);
    }

    public Method getMethod() {
        try {
            if (allowedClassName(stackTraceElement.getClassName())) {
                Class callingClass = Class.forName(stackTraceElement.getClassName());
                Method matchingMethod = extractMethod(stackTraceElement, callingClass);
                if (matchingMethod != null) {
                    return matchingMethod;
                }
            }
        } catch (ClassNotFoundException classNotFound) {}

        return null;
    }

    public static Method extractMethod(StackTraceElement stackTraceElement, Class callingClass)  {
        Class targetClass;
        if (stackTraceElement.getFileName().equals("<generated>")) {
            targetClass = callingClass.getSuperclass();
        } else {
            targetClass = callingClass;
        }
        try {
            return targetClass.getMethod(stackTraceElement.getMethodName());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private boolean allowedClassName(String className) {
        return !((className.startsWith("sun.")) || (className.startsWith("java.")));
    }
}
