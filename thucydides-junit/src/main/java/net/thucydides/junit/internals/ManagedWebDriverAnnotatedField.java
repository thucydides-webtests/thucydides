package net.thucydides.junit.internals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.thucydides.junit.annotations.InvalidManagedWebDriverFieldException;
import net.thucydides.junit.annotations.Managed;

import org.openqa.selenium.WebDriver;

/**
 * The WebDriver driver is stored as an annotated field in the test classes.
 * 
 * @author johnsmart
 * 
 */
public class ManagedWebDriverAnnotatedField {

    private static final String NO_ANNOTATED_FIELD_ERROR 
                                    = "No WebDriver field annotated with @Managed was found in the test case.";

    private Field field;

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static ManagedWebDriverAnnotatedField findFirstAnnotatedField(final Class<?> testClass) {

        for (Field field : testClass.getDeclaredFields()) {
            if (isFieldAnnotated(field)) {
                return new ManagedWebDriverAnnotatedField(field);
            }
        }
        throw new InvalidManagedWebDriverFieldException(NO_ANNOTATED_FIELD_ERROR);
    }

    private static boolean isFieldAnnotated(final Field field) {
        return (fieldIsAnnotatedCorrectly(field) && fieldIsRightType(field));
    }

    private static boolean fieldIsRightType(final Field field) {
        return (field.getType().isAssignableFrom(WebDriver.class));
    }

    private static boolean fieldIsAnnotatedCorrectly(final Field field) {
        
        boolean managedAnnotationFound = false;
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof Managed) {
                managedAnnotationFound = true;
                break;
            }
        }
        return managedAnnotationFound;
    }

    protected ManagedWebDriverAnnotatedField(final Field field) {
        this.field = field;
    }

    public void setValue(final Object testCase, final WebDriver manageDriver) {
        try {
            field.set(testCase, manageDriver);
        } catch (IllegalAccessException e) {
            throw new InvalidManagedWebDriverFieldException("Could not access or set web driver field: " 
                         + field 
                         + " - is this field public?");
        }
    }
}
