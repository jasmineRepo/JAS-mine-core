package microsim.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotate variables of the simulation manager to automatically display on the 
 * GUI (Graphical User Interface).  A GUIparameter can be modified at run-time.
 * Was previously called ModelParmameter but the name was changed as it was considered
 * misleading.
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
