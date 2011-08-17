package net.thucydides.core.annotations;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

