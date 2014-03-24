package net.thucydides.core;

import java.util.Map;

public interface SessionMap extends Map {
    Map<String, String> getMetaData();
    void addMetaData(String key, String value);
    void clearMetaData();
    void shouldContainKey(Object o);
}
