package net.thucydides.junit.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates which user story a given acceptance test belongs to.
 * This is used to build the aggregate reports, organized by user story.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ForUserStory {
    Class<?> value();
}
