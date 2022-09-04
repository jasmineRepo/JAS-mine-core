package microsim.statistics.functions;

import microsim.statistics.*;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.FloatArraySource;
import microsim.statistics.FloatSource;

/**
 * This class computes the sum of an array of source values. According to the source data type
 * there are four data-type oriented implementations. Each of them implements always the
 * <i>DoubleSource</i> interface.
 */
public abstract class SumArrayFunction extends AbstractArrayFunction implements DoubleSource {

	/** Create a sum function on a float array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(FloatArraySource source) {
		super(source);
	}

	/** Create a sum function on an integer array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(IntArraySource source) {
		super(source);
	}

	/** Create a sum function on a long array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(LongArraySource source) {
		super(source);
	}

	/** Create a sum function on a double array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(DoubleArraySource source) {
		super(source);
	}

	/**
	 * SumFunction operating on double source values.
	 */
	public static class Double extends SumArrayFunction implements DoubleSource
	{
		/** Create a sum function on a double array source.
		 * @param source The data source.
		 */
		public Double(DoubleArraySource source) {
			super(source);
		}

		protected double dsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {
			dsum = 0.;

			for (double datum : data) dsum += datum;

		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> id) {	return dsum; }
	}

	/**
	 * SumFunction operating on long source values.
	 */
	public static class Long extends SumArrayFunction implements LongSource
	{
		/** Create a sum function on a long array source.
		 * @param source The data source.
		 */
		public Long(LongArraySource source) {
			super(source);
		}

		protected long lsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {
			lsum = 0;

			for (long datum : data) lsum += datum;

		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public long getLongValue(Enum<?> id) {	return lsum; }

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lsum;
		}
	}

	/**
	 * SumFunction operating on integer source values.
	 */
	public static class Integer extends SumArrayFunction implements IntSource
	{
		/** Create a sum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IntArraySource source) {
			super(source);
		}

		protected int isum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {

			isum = 0;

			for (int datum : data) isum += datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public int getIntValue(Enum<?> id) {
			return isum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return isum;
		}
	}

	/**
	 * SumFunction operating on float source values.
	 */
	public static class Float extends SumArrayFunction implements FloatSource
	{
		/** Create a sum function on an float array source.
		 * @param source The data source.
		 */
		public Float(FloatArraySource source) {
			super(source);
		}

		protected float fsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(float[] data) {
			fsum = 0;

			for (float datum : data) fsum += datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public float getFloatValue(Enum<?> id) {
			return fsum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return fsum;
		}
	}
}
