package net.thucydides.junit.runners;

import net.thucydides.junit.annotations.Qualifier;
import net.thucydides.junit.internals.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class QualifierFinder {

    private final Object testCase;

    public QualifierFinder(Object testCase) {
        this.testCase = testCase;
    }

    public static QualifierFinder forTestCase(Object testCase) {
        return new QualifierFinder(testCase);
    }

    public String getQualifier() {
        if (hasQualifierAnnotation()) {
            return (String) MethodInvoker.on(testCase).run(getQualifiedMethod());
        } else {
            return testCase.toString();
        }
    }

    private Method getQualifiedMethod() {
        Method[] methods = testCase.getClass().getDeclaredMethods();
        for (Method each : methods) {
            if (each.getAnnotation(Qualifier.class) != null) {
                checkModifiersFor(each);
                return each;
            }
        }
        return null;
    }

    private void checkModifiersFor(Method each) {
        int modifiers = each.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("Qualifier method must not be static");
        }
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Qualifier method must be public");
        }
        if (each.getReturnType() != String.class) {
            throw new IllegalArgumentException("Qualifier method must return a String");
        }
    }

    private boolean hasQualifierAnnotation() {
        return (getQualifiedMethod() != null);
    }
}
