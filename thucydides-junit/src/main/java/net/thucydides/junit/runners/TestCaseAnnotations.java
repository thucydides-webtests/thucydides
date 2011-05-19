package net.thucydides.junit.runners;

import net.thucydides.core.steps.PagesAnnotatedField;
import net.thucydides.core.steps.StepsAnnotatedField;
import net.thucydides.junit.internals.ManagedWebDriverAnnotatedField;
import org.openqa.selenium.WebDriver;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class TestCaseAnnotations {
    
    private TestCaseAnnotations() {}
    
    
    public static void checkThatTestCaseIsCorrectlyAnnotated(final  Class<?> testCase) {
        checkThatManagedFieldIsDefinedIn(testCase);
        checkThatStepsFieldIsDefinedIn(testCase);
        checkThatPagesFieldIsDefinedIn(testCase);
    }
    /**
     * There must be a WebDriver field in the test case annotated with the Managed annotation.
     */
    private static void checkThatManagedFieldIsDefinedIn(final  Class<?> testCase) {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(testCase);
    }

    /**
     * There must be a ScenarioSteps field in the test case annotated with the Steps annotation.
     */
    private static void checkThatStepsFieldIsDefinedIn(final  Class<?> testCase) {
        StepsAnnotatedField.findMandatoryAnnotatedFields(testCase);
    }

    /**
     * There must be a Pages field in the test case annotated with the ManagedPages annotation.
     */
    private static void checkThatPagesFieldIsDefinedIn(final  Class<?> testCase) {
        PagesAnnotatedField.findFirstAnnotatedField(testCase);
    }

    
    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    public static void injectDriverInto(final Object testCase, final WebDriver driver) {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        webDriverField.setValue(testCase, driver);
    }

}
