package microsim.statistics.weighted;

/**
 * Used by statistical object to access array of float values.
 */
public interface WeightedFloatArraySource {
	/**
	 * Return the currently cached array of float values.
	 * @return An array of float or a null pointer if the source is empty. 
	 */
	float[] getFloatArray();
	
	double[] getWeights();
}
