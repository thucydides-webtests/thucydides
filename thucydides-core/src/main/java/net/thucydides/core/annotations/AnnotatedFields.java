package net.thucydides.core.annotations;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Find the annotated fields in a given class.
 * Used as a utility class for the higher-level annotation processing.
 * Typical use:
 * <pre>
 *     <code>
 *         for (Field field : AnnotatedFields.of(someClass).allFields()) {
 *             ...
 *         }
 *     </code>
 * </pre>
 */
public class AnnotatedFields {

    private final Class<?> clazz;

    public static AnnotatedFields of(final Class<?> testClass) {
        return new AnnotatedFields(testClass);
    }

    private AnnotatedFields(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Set<Field> allFields() {
        Set<Field> fields = new HashSet<Field>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getFields()));
        if (clazz != Object.class) {
            fields.addAll(AnnotatedFields.of(clazz.getSuperclass()).allFields());
        }
        return fields;
    }
}

