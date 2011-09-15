package net.thucydides.junit.runners;

import net.thucydides.core.annotations.ManagedWebDriverAnnotatedField;
import net.thucydides.core.pages.PagesAnnotatedField;
import org.openqa.selenium.WebDriver;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class TestCaseAnnotations {

    private final Object testCase;

    private TestCaseAnnotations(final Object testCase) {
        this.testCase = testCase;
    }

    public static TestCaseAnnotations forTestCase(final Object testCase) {
        return new TestCaseAnnotations(testCase);
    }

    public static void checkThatTestCaseIsCorrectlyAnnotated(final Class<?> testClass) {
        checkThatManagedFieldIsDefined(testClass);
        checkThatPagesFieldIsDefined(testClass);
    }
    /**
     * There must be a WebDriver field in the test case annotated with the Managed annotation.
     */
    private static void checkThatManagedFieldIsDefined(final Class<?> testClass) {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(testClass);
    }

    /**
     * There must be a Pages field in the test case annotated with the ManagedPages annotation.
     */
    private static void checkThatPagesFieldIsDefined(final Class<?> testClass) {
        PagesAnnotatedField.findFirstAnnotatedField(testClass);
    }

    
    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    public void injectDriver(final WebDriver driver) {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        webDriverField.setValue(testCase, driver);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    public boolean isUniqueSession() {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        return webDriverField.isUniqueSession();
    }

}
