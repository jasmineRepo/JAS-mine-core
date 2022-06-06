package microsim.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate entity class used to store coefficient tables on database.
 * Keys fields are used to index values in a multi-key multi-value structure.
 * 
 * @author Michele Sonnessa
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CoefficientMapping {

	String[] keys();
	
	String[] values();
}
