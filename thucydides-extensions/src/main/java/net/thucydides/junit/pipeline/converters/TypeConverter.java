package net.thucydides.junit.pipeline.converters;

public interface TypeConverter {
    public abstract boolean appliesTo(Class<?> type);
    Object valueOf(Object fieldValue);
}
