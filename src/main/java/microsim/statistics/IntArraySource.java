package microsim.statistics;

/**
 * Used by statistical object to access array of integer values.
 */
public interface IntArraySource {
	/**
	 * Return the currently cached array of integer values.
	 * @return An array of integer or a null pointer if the source is empty.
	 */
	int[] getIntArray();
}
