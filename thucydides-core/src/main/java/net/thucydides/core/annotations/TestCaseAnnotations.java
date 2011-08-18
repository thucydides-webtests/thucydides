package net.thucydides.core.annotations;

import net.thucydides.core.steps.PagesAnnotatedField;

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
    
    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    public void injectDriver(final WebDriver driver) {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        webDriverField.setValue(testCase, driver);
    }
}
