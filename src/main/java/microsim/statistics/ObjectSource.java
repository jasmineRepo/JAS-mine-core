package microsim.statistics;

/**
 * Used by statistical object to access object data. Each variable must have a unique integer id.
 */
public interface ObjectSource {

	enum Variables {
		Default;
	}
	
	/**
	 * Return the value corresponding to the given variableID
	 * @param variableID A unique identifier for a variable.
	 * @return The current value of the required variable.
	 */
	Object getObjectValue(Enum<?> variableID);
}
