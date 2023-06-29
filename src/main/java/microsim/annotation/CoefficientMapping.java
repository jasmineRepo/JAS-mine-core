package microsim.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates entity class used to store coefficient tables in database.
 * Fields marked as keys are used to index values in a multi-key multi-value structure.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CoefficientMapping {

    String[] keys();

    String[] values();
}
