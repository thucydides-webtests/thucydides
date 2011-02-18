package net.thucydides.junit.internals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.InvalidManagedWebDriverFieldException;
import net.thucydides.junit.annotations.ManagedPages;

/**
 * The Pages object keeps track of the Page Objects used during the tests.
 * 
 * @author johnsmart
 * 
 */
public class PagesAnnotatedField {

    private Field field;
    private ManagedPages annotation;
    
    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static PagesAnnotatedField findFirstAnnotatedField(final Class<?> testClass) {

        PagesAnnotatedField annotatedField = null;
        
        for (Field field : testClass.getDeclaredFields()) {
            ManagedPages fieldAnnotation = annotationFrom(field);
            if (fieldAnnotation != null) {
                annotatedField = new PagesAnnotatedField(field, fieldAnnotation);
                break;
            }
        }
        return annotatedField;
    }

    private static ManagedPages annotationFrom(Field aField) {
        ManagedPages annotationOnField = null;
        if (isFieldAnnotated(aField)) {
            annotationOnField = aField.getAnnotation(ManagedPages.class);
        }
        return annotationOnField;
    }

    private static boolean isFieldAnnotated(final Field field) {
        return (fieldIsAnnotatedCorrectly(field) && fieldIsRightType(field));
    }

    private static boolean fieldIsRightType(final Field field) {
        return (field.getType().isAssignableFrom(Pages.class));
    }

    private static boolean fieldIsAnnotatedCorrectly(final Field field) {
        
        boolean pagesAnnotationFound = false;
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof ManagedPages) {
                pagesAnnotationFound = true;
                break;
            }
        }
        return pagesAnnotationFound;
    }

    protected PagesAnnotatedField(final Field field, final ManagedPages annotation) {
        this.field = field;
        this.annotation = annotation;
    }

    public void setValue(final Object testCase, final Pages pages) {
        try {
            field.set(testCase, pages);
        } catch (IllegalAccessException e) {
            throw new InvalidManagedWebDriverFieldException("Could not access or set managed pages field: " + field);
        }
    }
    
    public String getDefaultBaseUrl() {
        return annotation.defaultUrl();
    }
}
