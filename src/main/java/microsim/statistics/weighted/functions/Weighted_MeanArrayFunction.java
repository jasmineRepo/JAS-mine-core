package microsim.statistics.weighted.functions;

import microsim.statistics.DoubleSource;
import microsim.statistics.weighted.WeightedDoubleArraySource;
import microsim.statistics.weighted.WeightedFloatArraySource;
import microsim.statistics.weighted.WeightedIntArraySource;
import microsim.statistics.weighted.WeightedLongArraySource;

/**
 * This class computes the (weighted) average (mean) value of an array of values taken from a data source,
 * weighted by corresponding weights:
 * 		weighted mean = sum (values * weights) / sum (weights)
 * Note that the array of weights must have the same length as the array of values, otherwise an exception
 * will be thrown.
 * The mean function return always double values, so it implements only the
 * <i>DoubleSource</i> interface.
 */
public class Weighted_MeanArrayFunction extends AbstractWeightedArrayFunction implements DoubleSource {

	/** Create a mean function on a float array source.
	 * @param source The data source.
	 */
	public Weighted_MeanArrayFunction(WeightedFloatArraySource source) {
		super(source);
	}

	/** Create a mean function on an integer array source.
	 * @param source The data source.
	 */
	public Weighted_MeanArrayFunction(WeightedIntArraySource source) {
		super(source);
	}

	/** Create a mean function on a long array source.
	 * @param source The data source.
	 */
	public Weighted_MeanArrayFunction(WeightedLongArraySource source) {
		super(source);
	}

	/** Create a mean function on a (weighted) double array source.
	 * @param source The weighted data source.
	 */
	public Weighted_MeanArrayFunction(WeightedDoubleArraySource source) {
		super(source);
	}

	protected double weightedMean;

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data, double[] weights) {

		weightedMean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			double denominator = 0.;
			for (int i = 0; i < data.length; i++) {
				sum += data[i] * weights[i];
				denominator += weights[i];
			}
			weightedMean = sum / denominator;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data, double[] weights) {
		weightedMean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			double denominator = 0.;
			for (int i = 0; i < data.length; i++) {
				sum += data[i] * weights[i];
				denominator += weights[i];
			}
			weightedMean = sum / denominator;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data, double[] weights) {
		weightedMean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			double denominator = 0.;
			for (int i = 0; i < data.length; i++) {
				sum += data[i] * weights[i];
				denominator += weights[i];
			}
			weightedMean = sum / denominator;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return weightedMean;
	}


}
