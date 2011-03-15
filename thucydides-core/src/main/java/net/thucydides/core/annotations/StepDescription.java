package net.thucydides.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to override the default description given to a test step.
 * A sample usage is shown here:
 * <pre><code>
    @Test @Step(1)
    @Description("The user opens the Google home page.")
    public void the_user_opens_the_page() {
       ...
    }    
 * </code><pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StepDescription {
    String value();
}
