package microsim.statistics;

/**
 * Used by statistical object to access double data. Each variable must have a unique integer id.
 */
public interface DoubleSource {

	enum Variables {
		Default;
	}
	
	/**
	 * Return the double value corresponding to the given variableID
	 * @param variableID A unique identifier for a variable.
	 * @return The current double value of the required variable.
	 */
	double getDoubleValue(Enum<?> variableID);
}
