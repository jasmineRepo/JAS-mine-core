package microsim.statistics;

/**
 * Used by statistical object to access array of long values.
 */
public interface LongArraySource {
	/**
	 * Return the currently cached array of long values.
	 * @return An array of long or a null pointer if the source is empty. 
	 */
	long[] getLongArray();
}
