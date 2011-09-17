package net.thucydides.core.annotations;

import net.thucydides.core.reports.html.Formatter;
import org.junit.Ignore;

import java.lang.reflect.Method;
import java.util.List;

import static net.thucydides.core.util.NameConverter.withNoArguments;

/**
 * Utility class used to help process annotations on tests and test steps.
 */
public class TestAnnotations {

    private final Class<?> testClass;

    private TestAnnotations(final Class<?> testClass) {
        this.testClass = testClass;
    }

    public static TestAnnotations forClass(final Class<?> testClass) {
        return new TestAnnotations(testClass);
    }

    public String getAnnotatedTitleForMethod(final String methodName) {
        String annotatedTitle = null;
        if (testClass != null) {
            if (testClassHasMethodCalled(methodName)) {
                annotatedTitle = getAnnotatedTitle(methodName);
            }
        }
        return annotatedTitle;
    }

    public boolean isPending(final String methodName) {
        if (testClass != null) {
            if (testClassHasMethodCalled(methodName)) {
                return isPending(getMethodCalled(methodName));
            }
        }
        return false;
    }

    public static boolean isPending(final Method method) {
        if (method != null) {
            return (method.getAnnotation(Pending.class) != null);
        }
        return false;
    }

    public static boolean isIgnored(final Method method) {
        if (method != null) {
            return (method.getAnnotation(Ignore.class) != null);
        }
        return false;
    }

    public boolean isIgnored(final String methodName) {
        if (testClass != null) {
            return isIgnored(getMethodCalled(methodName));
        }
        return false;
    }

    private String getAnnotatedTitle(String methodName) {
        Method testMethod = getMethodCalled(methodName);
        Title titleAnnotation = testMethod.getAnnotation(Title.class);
        if (titleAnnotation != null) {
            return titleAnnotation.value();
        } else {
            return null;
        }
    }

    private boolean testClassHasMethodCalled(final String methodName) {
        return (getMethodCalled(methodName) != null);

    }

    private Method getMethodCalled(final String methodName) {
        String baseMethodName = withNoArguments(methodName);
        try {
            return testClass.getMethod(baseMethodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Return a list of the issues mentioned in the title annotation of this method.
     * @param methodName
     * @return
     */
    public List<String> getAnnotatedIssuesForMethod(String methodName) {
        String title = getAnnotatedTitleForMethod(methodName);
        if (title != null) {
            return Formatter.issuesIn(title);
        } else {
            return Formatter.issuesIn(methodName);
        }
    }
}
