package net.thucydides.jbehave;

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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
