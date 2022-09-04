package microsim.statistics;

/**
 * Used by statistical object to access float data. Each variable must have a unique integer id.
 */
public interface FloatSource
{
	enum Variables {
		Default;
	}

	/**
	 * Return the float value corresponding to the given variableID
	 * @param variableID A unique identifier for a variable.
	 * @return The current float value of the required variable.
	 */
	float getFloatValue(Enum<?> variableID);
}
