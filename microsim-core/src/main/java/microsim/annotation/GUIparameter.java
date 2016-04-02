package microsim.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotate variables of the simulation manager automatically managed by JAS-mine gui
 * to ask parameters to simulation user.  A GUIparameter can be modified at run-time.
 * Was previously called ModelParmameter but name changed as this name was misleading.
 * 
 * @author ross richardson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD }) 
public @interface GUIparameter {

	public String section() default "";
	
	public String name() default "";
	
	public String description() default "";
	
}
