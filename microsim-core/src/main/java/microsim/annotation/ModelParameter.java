package microsim.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate variables of the simulation manager automatically managed by JAS gui
 * to ask parameters to simulation user.
 *  
 * @author Michele Sonnessa
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelParameter {

	public String section() default "";
	
	public String name() default "";
	
	public String description() default "";
	
}
