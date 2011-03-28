package net.thucydides.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
/**
 * A step in an acceptance test.
 * Steps are like mini unit-tests, executed in a predetermined order. 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Step {
    String value() default "";
}
