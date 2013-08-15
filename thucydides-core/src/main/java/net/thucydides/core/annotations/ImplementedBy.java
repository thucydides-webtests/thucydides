package net.thucydides.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import net.thucydides.core.pages.WebElementFacadeImpl;

/**
 *  Annotation is used to specify the implementation Class of the interface
 *  that extends WebElementFacade. 
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementedBy {

	Class<? extends WebElementFacadeImpl> value();

}
