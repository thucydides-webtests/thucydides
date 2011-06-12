package net.thucydides.core.steps;

import java.lang.reflect.Method;

import static net.thucydides.core.util.NameConverter.humanize;

/**
 * A description of a step executed during a Thucydides step run.
 * Used in the reporting to generate user-readable names for the executed steps.
 */
public class ExecutedTestDescription {

    private final Class<?> testClass;
    private final String testName;

    protected ExecutedTestDescription(final Class<?> testClass,
                                      final String testName) {
        this.testClass = testClass;
        this.testName = testName;
    }

    public ExecutedTestDescription clone() {
        return new ExecutedTestDescription(testClass, testName);
    }

    /**
     * The class of the step library being executed.
     */
    public Class<?> getTestClass() {
        return testClass;
    }


    public String getName() {
        return testName;
    }

    /**
     * We might not have the test class provided (e.g. at the end of a test).
     */
    public static ExecutedTestDescription of(final Class<?> testClass,
                                             final String name) {
        return new ExecutedTestDescription(testClass, name);
    }

    public Method getTestMethod() {
        if (getTestClass() != null) {
            return methodCalled(withNoArguments(getName()), getTestClass());
        } else {
            return null;
        }
    }


    private String withNoArguments(final String methodName) {
        int firstSpace = methodName.indexOf(':');
        if (firstSpace > 0) {
            return methodName.substring(0, firstSpace);
        }
        return methodName;
    }

    private Method methodCalled(final String methodName, final Class<?> testClass) {
        Method[] methods = testClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
    }


    /**
     * Turns a method into a human-readable title.
     */
    public String getTitle() {
        return humanize(getName());
    }
}
