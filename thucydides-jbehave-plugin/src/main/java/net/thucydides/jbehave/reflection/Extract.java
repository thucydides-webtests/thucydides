package net.thucydides.jbehave.reflection;

import java.lang.reflect.Field;

public class Extract {

    private final String fieldName;

    private Extract(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Extract field(String fieldName) {
        return new Extract(fieldName);
    }

    public Object from(Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
