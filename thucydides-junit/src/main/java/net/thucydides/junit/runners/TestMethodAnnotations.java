package net.thucydides.junit.runners;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedWebDriverAnnotatedField;
import net.thucydides.core.annotations.WithDriver;
import org.junit.runners.model.FrameworkMethod;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

import static com.google.common.collect.ImmutableSet.copyOf;

/**
 * Utility class used to read Thucydides annotations for a particular JUnit test.
 * @author johnsmart
 *
 */
public final class TestMethodAnnotations {

    private final FrameworkMethod method;

    private TestMethodAnnotations(final FrameworkMethod method) {
        this.method = method;
    }

    public static TestMethodAnnotations forTest(final FrameworkMethod method) {
        return new TestMethodAnnotations(method);
    }


    public boolean isDriverSpecified() {
        return (method.getMethod().getAnnotation(WithDriver.class) != null);
    }

    public String specifiedDriver() {
        Preconditions.checkArgument(isDriverSpecified() == true);
        return (method.getMethod().getAnnotation(WithDriver.class).value());
    }


}
