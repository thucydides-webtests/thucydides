package net.thucydides.junit.pipeline.converters;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class TypeConverters {
    private static final List<TypeConverter> DEFAULT_TYPE_CONVERTERS = ImmutableList.of(
            new StringTypeConverter(),
            new IntegerTypeConverter()
    );

    public static List<TypeConverter> getDefaultTypeConverters() {
        return DEFAULT_TYPE_CONVERTERS;
    }
}
