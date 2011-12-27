package net.thucydides.core.sessions;

import java.util.concurrent.ConcurrentHashMap;

public class TestSessionVariables extends ConcurrentHashMap {

    @Override
    public Object get(Object o) {
        Object result = super.get(o);
        if (result == null) {
            throw new AssertionError("Session variable " + o + " expected but not found.");
        }
        return result;
    }
}
