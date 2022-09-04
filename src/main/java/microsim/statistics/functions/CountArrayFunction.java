package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntArraySource;
import microsim.statistics.IntSource;
import microsim.statistics.LongArraySource;

/**
 * This class computes the number of values in an array taken from a data source.
 * The mean function return always an int value, so it implements the
 * <i>IntSource</i> interface and the standard  <i>DoubleSource</i> one.
 */
public class CountArrayFunction extends AbstractArrayFunction implements DoubleSource, IntSource {

	/** Create a count function on an integer array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(IntArraySource source) {
		super(source);
	}

	/** Create a count function on a long array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(LongArraySource source) {
		super(source);
	}

	/** Create a count function on a double array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(DoubleArraySource source) {
		super(source);
	}

	protected int count;

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return (double) count;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#getInt()
	 */
	public int getIntValue(Enum<?> id) { return count; }

}
