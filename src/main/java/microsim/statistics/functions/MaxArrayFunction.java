package microsim.statistics.functions;

import microsim.statistics.*;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.LongSource;

/**
 * This class computes the maximum value in an array of source values. According to the source data type
 * there are four data-type oriented implementations. Each of them implements always the
 * <i>DoubleSource</i> interface.
 */
public abstract class MaxArrayFunction extends AbstractArrayFunction implements DoubleSource {

	/** Create a maximum function on an integer array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(IntArraySource source) {
		super(source);
	}

	/** Create a maximum function on a long array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(LongArraySource source) {
		super(source);
	}

	/** Create a maximum function on a double array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(DoubleArraySource source) {
		super(source);
	}

	/**
	 * MaxFunction operating on double source values.
	 */
	public static class Double extends MaxArrayFunction implements DoubleSource
	{
		/** Create a maximum function on a double array source.
		 * @param source The data source.
		 */
		public Double(DoubleArraySource source) {
			super(source);
		}

		protected double dmax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {
			dmax = java.lang.Double.MIN_VALUE;

			for (double datum : data)
				if (dmax < datum)
					dmax = datum;

		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> id) {	return dmax; }
	}

	/**
	 * MaxFunction operating on long source values.
	 */
	public static class Long extends MaxArrayFunction implements LongSource
	{
		/** Create a maximum function on a long array source.
		 * @param source The data source.
		 */
		public Long(LongArraySource source) {
			super(source);
		}

		protected long lmax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {
			lmax = java.lang.Long.MIN_VALUE;

			for (long datum : data)
				if (lmax < datum)
					lmax = datum;

		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public long getLongValue(Enum<?> id) {	return lmax; }

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lmax;
		}
	}

	/**
	 * MaxFunction operating on integer source values.
	 */
	public static class Integer extends MaxArrayFunction implements IntSource
	{
		/** Create a maximum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IntArraySource source) {
			super(source);
		}

		protected int imax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {

			imax = java.lang.Integer.MIN_VALUE;

			for (int datum : data)
				if (imax < datum)
					imax = datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public int getIntValue(Enum<?> id) {
			return imax;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return imax;
		}
	}
}
