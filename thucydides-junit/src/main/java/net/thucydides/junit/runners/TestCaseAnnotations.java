package net.thucydides.junit.runners;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedWebDriverAnnotatedField;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

import static com.google.common.collect.ImmutableSet.copyOf;

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

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    public boolean isUniqueSession() {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        return webDriverField.isUniqueSession();
    }

    /**
     * Does this class support web tests?
     * Test cases that support web tests need to have at least a WebDriver field annotated with the @Managed
     * annotation.
     */
    public static boolean supportsWebTests(Class clazz) {
        return Iterables.any(fieldsIn(clazz), isAManagedWebDriver());
    }

    private static ImmutableSet<Field> fieldsIn(Class clazz) {
        return copyOf(clazz.getDeclaredFields());
    }

    private static Predicate<Field> isAManagedWebDriver() {
        return new Predicate<Field>() {

            public boolean apply(Field field) {
                return ((WebDriver.class.isAssignableFrom(field.getType()))
                        && (field.getAnnotation(Managed.class) != null));
            }
        };
    }

}
