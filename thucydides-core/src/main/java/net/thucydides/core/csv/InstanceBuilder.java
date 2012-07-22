package net.thucydides.core.csv;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for finding and invoking constructors.
 */
public final class InstanceBuilder {

    private Object targetObject;

    private  <T> InstanceBuilder(final T newObject) {
        this.targetObject = newObject;
    }

    public static <T> T newInstanceOf(final Class<T> clazz,
                                      final Object... constructorArgs)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {

        if ((constructorArgs.length == 0) &&(thereIsADefaultConstructorFor(clazz))) {
            return clazz.newInstance();
        } else {
            return invokeConstructorFor(clazz, constructorArgs);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeConstructorFor(final Class<T> clazz, final Object[] constructorArgs)
            throws InvocationTargetException, IllegalAccessException, InstantiationException {

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        for(Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == constructorArgs.length) {
                return (T) constructor.newInstance(constructorArgs);
            }
        }
        throw new IllegalStateException("No matching constructor found for " + clazz
                + " with arguments: " + ArrayUtils.toString(constructorArgs));

    }

    private static <T> boolean thereIsADefaultConstructorFor(final Class<T> clazz) {

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for(Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }

    public void setPropertyValue(final String property,
                                 final String value) {
        if (!setProperty(property, value)) {
            throw new FailedToInitializeTestData("Could not find property field " + property);
        }
    }

    private boolean setProperty(String property, String value) {
        try {
            Method setter = findSetter(property);
            Field field = findField(property);
            if (setter != null) {
                setter.invoke(targetObject, value);
                return true;
            } else if (field != null) {
                field.set(targetObject, value);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new FailedToInitializeTestData("Could not assign property value", e);
        }
    }

    private Method findSetter(final String property) {
        Method[] methods = targetObject.getClass().getMethods();
        String setterMethod = "set" + StringUtils.capitalize(property);
        for(Method method : methods) {
            if (method.getName().equals(setterMethod)) {
                return method;
            }
        }
        return null;
    }

    private Field findField(final String property) {
        List<Field> fields = getAllDeclaredFieldsIn(targetObject.getClass());
        for(Field field :fields) {
            if (field.getName().compareToIgnoreCase(property) == 0) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    private List<Field> getAllDeclaredFieldsIn(Class targetClass) {
        List<Field> parentFields
                = (targetClass.getSuperclass() != null) ? getAllDeclaredFieldsIn(targetClass.getSuperclass()) : Collections.EMPTY_LIST;

        List<Field> localFields = Arrays.asList(targetClass.getDeclaredFields());
        List<Field> allFields = new ArrayList<Field>(localFields);
        allFields.addAll(parentFields);
        return allFields;
    }

    public static <T> InstanceBuilder inObject(final T newObject) {
        return new InstanceBuilder(newObject);
    }
}
