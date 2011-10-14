package net.thucydides.core.csv;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

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
        if (PropertyUtils.isWriteable(targetObject, property)) {
            setPropertyValueViaSetter(property, value);
        } else {
            setFieldValueDirectly(property, value);
        }
    }

    private void setPropertyValueViaSetter(final String property, final String value) {
        try {
            PropertyUtils.setProperty(targetObject, property, value);
        } catch (Exception e) {
            throw new FailedToInitializeTestData("Could not assign property value using setter", e);
        }
    }

    private void setFieldValueDirectly(final String property, final String value) {
        try {
            Field field = findField(property);
            field.set(targetObject, value);
        } catch (Exception e) {
            throw new FailedToInitializeTestData("Could not assign property value", e);
        }

    }

    private Field findField(final String property) {
        Field[] fields = targetObject.getClass().getFields();
        for(Field field : fields) {
            if (field.getName().equals(property)) {
                return field;
            }
        }
        throw new FailedToInitializeTestData("Could not find property called " + property);
    }

    public static <T> InstanceBuilder inObject(final T newObject) {
        return new InstanceBuilder(newObject);
    }
}
