package net.thucydides.junit.internals;

import java.lang.reflect.Method;

import org.junit.Ignore;

import net.thucydides.core.annotations.Pending;

/**
 * Determine the status of a method based on its annotations.
 * @author johnsmart
 *
 */
public final class TestStatus {
    
    private final Method method;
    
    private TestStatus(final Method method) {
        super();
        this.method = method;
    }

    public static TestStatus of(final Method method) {
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
