package net.thucydides.spock;

import java.lang.annotation.*;

import org.spockframework.runtime.extension.ExtensionAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@ExtensionAnnotation(ThucydidesEnabledExtension.class)
public @interface ThucydidesEnabled {
    String driver() default "";
}