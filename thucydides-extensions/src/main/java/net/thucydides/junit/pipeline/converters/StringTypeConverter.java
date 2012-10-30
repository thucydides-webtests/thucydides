package net.thucydides.junit.pipeline.converters;

public class StringTypeConverter implements TypeConverter {
    @Override
    public boolean appliesTo(Class<?> type) {
        return type.isAssignableFrom(String.class);
    }

    @Override
    public Object valueOf(Object fieldValue) {
        return fieldValue.toString();
    }
}
