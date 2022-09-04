package microsim.statistics.functions;

import microsim.statistics.*;

/**
 * This class computes the minimum value in an array of source values. According to the source data type
 * there are four data-type oriented implementations. Each of them implements always the
 * <i>DoubleSource</i> interface.
 */
public abstract class MinArrayFunction extends AbstractArrayFunction implements DoubleSource {

	/** Create a minimum function on an int array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(IntArraySource source) { super(source);	}

	/** Create a minimum function on a long array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(LongArraySource source) {	super(source); }

	/** Create a minimum function on a double array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(DoubleArraySource source) {	super(source); }

	/**
	 * MinFunction operating on double source values.
	 */
	public static class Double extends MinArrayFunction implements DoubleSource
	{
		/** Create a minimum function on a double array source.
		 * @param source The data source.
		 */
		public Double(DoubleArraySource source) { super(source);	}

		protected double min;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {

			min = java.lang.Double.MAX_VALUE;

			for (double datum : data)
				if (min > datum)
					min = datum;

		}

		/* (non-Javadoc)
		 * @see jas.statistics.LongSource#getLongValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return min;
		}
	}

	/**
	 * MinFunction operating on long source values.
	 */
	public static class Long extends MinArrayFunction implements LongSource
	{
		/** Create a minimum function on a long array source.
		 * @param source The data source.
		 */
		public Long(LongArraySource source) {
			super(source);
		}

		protected long lmin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {

			lmin = java.lang.Long.MAX_VALUE;

			for (long datum : data)
				if (lmin > datum)
					lmin = datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.LongSource#getLongValue(int)
		 */
		public long getLongValue(Enum<?> variableID) {
			return lmin;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lmin;
		}
	}

	/**
	 * MinFunction operating on integer source values.
	 */
	public static class Integer extends MinArrayFunction implements IntSource
	{
		/** Create a minimum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IntArraySource source) {
			super(source);
		}

		protected int imin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {

			imin = java.lang.Integer.MAX_VALUE;

			for (int datum : data)
				if (imin > datum)
					imin = datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.LongSource#getLongValue(int)
		 */
		public int getIntValue(Enum<?> variableID) {
			return imin;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return imin;
		}
	}
}
