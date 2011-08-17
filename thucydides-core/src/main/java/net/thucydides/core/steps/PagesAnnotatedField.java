package net.thucydides.core.steps;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.thucydides.core.annotations.AnnotatedFields;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;

/**
 * The Pages object keeps track of the Page Objects used during the tests.
 * 
 * @author johnsmart
 * 
 */
public class PagesAnnotatedField {

    private static final String NO_ANNOTATED_FIELD_ERROR 
    = "No Pages field annotated with @ManagedPages was found in the test case.";

    private Field field;
    private ManagedPages annotation;
    
    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static PagesAnnotatedField findFirstAnnotatedField(final Class<?> testClass) {

        for (Field field : AnnotatedFields.of(testClass).allFields()) {
            ManagedPages fieldAnnotation = annotationFrom(field);
            if (fieldAnnotation != null) {
                return new PagesAnnotatedField(field, fieldAnnotation);
            }
        }
        throw new InvalidManagedPagesFieldException(NO_ANNOTATED_FIELD_ERROR);
    }

    private static ManagedPages annotationFrom(final Field aField) {
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
            field.setAccessible(true);
            field.set(testCase, pages);
        } catch (IllegalAccessException e) {
            throw new InvalidManagedWebDriverFieldException("Could not access or set managed pages field: " + field, e);
        }
    }
    
    public String getDefaultBaseUrl() {
        return annotation.defaultUrl();
    }
}
