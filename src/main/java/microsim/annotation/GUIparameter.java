package microsim.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates variables of the simulation manager to automatically display on the GUI. A GUIparameter can be modified at
 * run-time.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GUIparameter {

    String section() default "";

    String name() default "";

    String description() default "";
}
