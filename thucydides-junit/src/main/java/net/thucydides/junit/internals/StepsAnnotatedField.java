package net.thucydides.junit.internals;

import java.lang.reflect.Field;

import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.junit.annotations.InvalidManagedWebDriverFieldException;
import net.thucydides.junit.annotations.InvalidStepsFieldException;
import net.thucydides.junit.annotations.Steps;

/**
 * The Pages object keeps track of the Page Objects used during the tests.
 * 
 * @author johnsmart
 * 
 */
public class StepsAnnotatedField {

    private Field field;
    
    private static final String NO_ANNOTATED_FIELD_ERROR 
        = "No ScenarioSteps field annotated with @Steps was found in the test case.";

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static StepsAnnotatedField findFirstAnnotatedField(final Class<?> testClass) {

        for (Field field : testClass.getDeclaredFields()) {
            Steps fieldAnnotation = annotationFrom(field);
            if (fieldAnnotation != null) {
                return new StepsAnnotatedField(field);
            }
        }
        throw new InvalidStepsFieldException(NO_ANNOTATED_FIELD_ERROR);
    }

    private static Steps annotationFrom(final Field aField) {
        Steps annotationOnField = null;
        if (isFieldAnnotated(aField)) {
            annotationOnField = aField.getAnnotation(Steps.class);
        }
        return annotationOnField;
    }

    private static boolean isFieldAnnotated(final Field field) {
        return (fieldIsAnnotatedCorrectly(field) && fieldIsRightType(field));
    }

    private static boolean fieldIsRightType(final Field field) {
        return (ScenarioSteps.class.isAssignableFrom(field.getType()));
    }

    private static boolean fieldIsAnnotatedCorrectly(final Field field) {
        return (field.getAnnotation(Steps.class) != null);
    }

    protected StepsAnnotatedField(final Field field) {
        this.field = field;
    }

    public void setValue(final Object testCase, final ScenarioSteps steps) {
        try {
            field.set(testCase, steps);
        } catch (IllegalAccessException e) {
            throw new InvalidManagedWebDriverFieldException("Could not access or set @Steps field: " + field);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ScenarioSteps> getFieldClass() {
        return (Class<? extends ScenarioSteps>) field.getType();
    }
}
