package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.FloatArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;

/**
 * This class computes the average value of an array of values taken from a data source.
 * The mean function return always double values, so it implements only the
 * <i>DoubleSource</i> interface.
 */
public class MeanArrayFunction extends AbstractArrayFunction implements DoubleSource {

	/** Create a mean function on a float array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(FloatArraySource source) {
		super(source);
	}

	/** Create a mean function on an integer array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(IntArraySource source) {
		super(source);
	}

	/** Create a mean function on a long array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(LongArraySource source) {
		super(source);
	}

	/** Create a mean function on a double array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(DoubleArraySource source) {
		super(source);
	}

	protected double mean;

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {

		mean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			for (double datum : data) {
				sum += datum;
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {

		mean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			for (float datum : data) {
				sum += datum;
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {// replace this garbage

		mean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			for (int datum : data) {
				sum += datum;
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {

		mean = 0.0;
		if (data.length != 0) {

			double sum = 0.0;
			for (long datum : data) {
				sum += datum;
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return mean;
	}


}
