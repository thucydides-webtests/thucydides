package net.thucydides.core.sessions;

import com.google.common.collect.ImmutableMap;
import net.thucydides.core.SessionMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestSessionVariables extends ConcurrentHashMap implements SessionMap {

    private final Map<String, String> metadata = new ConcurrentHashMap();

    public void shouldContainKey(Object o) {
        Object result = super.get(o);
        if (result == null) {
            throw new AssertionError("Session variable " + o + " expected but not found.");
        }
    }

    @Override
    public Map<String, String> getMetaData() {
        return ImmutableMap.copyOf(metadata);
    }

    @Override
    public void addMetaData(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public void clearMetaData() {
        metadata.clear();
    }

    @Override
    public void clear() {
        clearMetaData();
        super.clear();
    }
}
