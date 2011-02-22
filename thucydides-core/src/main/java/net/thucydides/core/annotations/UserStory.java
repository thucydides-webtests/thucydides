package net.thucydides.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flags a class as representing a user story.
 * Test scenarios can be organized into user stories. This allows for aggregate reporting
 * by user story, and selectivly running the scenarios associated with one or more user
 * stories. Using a class to represent a user story makes refactoring easier, and ensures that
 * user stories are not skipped due to typing errors.
 *
 * @author johnsmart
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UserStory {

}
