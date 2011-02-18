package net.thucydides.junit.internals;

import java.lang.reflect.Method;

import org.junit.Ignore;

import net.thucydides.junit.annotations.Pending;

public class TestStatus {
    
    final Method method;
    
    private TestStatus(Method method) {
        super();
        this.method = method;
    }

    public static TestStatus of(Method method) {
        return new TestStatus(method);
    }

    public boolean isPending() {
        Pending pending = method.getAnnotation(Pending.class);
        return (pending != null);
    }

    public boolean isIgnored() {
        Ignore ignored = method.getAnnotation(Ignore.class);
        return (ignored != null);
    }
}
