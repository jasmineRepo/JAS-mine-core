package microsim.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotates simulation-manager fields for display in the GUI. By default, a
 * parameter is available for editing at runtime. Set {@link #runtimeModifiable()}
 * to false to indicate that it should remain fixed after the model is built.
 * Was previously called ModelParmameter but the name was changed as it was
 * considered
 * misleading.
 * 
 * @author ross richardson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface GUIparameter {

    public String section() default "";

    public String name() default "";

    public String description() default "";

    /**
     * @return whether the parameter may be edited after the model is built
     */
    public boolean runtimeModifiable() default true;

}
