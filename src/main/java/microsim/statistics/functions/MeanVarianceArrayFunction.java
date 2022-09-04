package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;

/**
 * This class computes the average and variance value of an array of values taken from a data source.
 * The mean function return always double values, so it implements only the
 * <i>IDoubleSource</i> interface. <BR>
 * In order to retrieve the mean pass the MeanVarianceFunction.MEAN argument to the getDoubleValue function,
 * while for the variance the MeanVarianceFunction.VARIANCE one.
 */
public class MeanVarianceArrayFunction extends AbstractArrayFunction implements DoubleSource {


	public enum Variables {
		/**	Represent the mean function argument for the getDoubleValue method. */
		Mean,
		/**	Represent the variance function argument for the getDoubleValue method. */
		Variance;
	}

	/** Create a mean function on an integer array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(IntArraySource source) {
		super(source);
	}

	/** Create a mean function on a long array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(LongArraySource source) {
		super(source);
	}

	/** Create a mean function on a double array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(DoubleArraySource source) {
		super(source);
	}

	protected double mean, variance;

	private void setValues(int count, double sum, double sumOfSquares)
	{
		if (count == 0)
		{
			mean = 0.0;
			variance = 0.0;
		}
		else
		{
			mean = sum / (double) count;
			variance = (sumOfSquares - mean * sum) / (double) count;
			//This is a population variance as it is the variance of the array's data.
		}
		System.out.println("count " + count + ", mean " + mean + ", variance " + variance);
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {

		double sum = 0.0;
		double sumOfSquares = 0.0;
		for (double d : data) {
			sum += d;
			sumOfSquares += (d * d);
		}
		setValues(data.length, sum, sumOfSquares);
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {

		double sum = 0.0;
		double sumOfSquares = 0.0;
		for (double d : data) {
			sum += d;
			sumOfSquares += (d * d);
		}
		setValues(data.length, sum, sumOfSquares);
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {
		double sum = 0.0;
		double sumOfSquares = 0.0;
		for (double d : data) {
			sum += d;
			sumOfSquares += (d * d);
		}
		setValues(data.length, sum, sumOfSquares);
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return switch ((Variables) variableID) {
			case Mean -> mean;
			case Variance -> variance;
			default ->
					throw new UnsupportedOperationException("The function result with id " + variableID + " is not supported.");
		};
	}


}
