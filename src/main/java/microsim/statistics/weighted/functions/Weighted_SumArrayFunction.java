package microsim.statistics.weighted.functions;

import microsim.statistics.weighted.WeightedDoubleArraySource;
import microsim.statistics.weighted.WeightedFloatArraySource;
import microsim.statistics.weighted.WeightedIntArraySource;
import microsim.statistics.weighted.WeightedLongArraySource;

import microsim.statistics.DoubleSource;

/**
 * This class computes the sum of an array of source values, with each element of the array
 * multiplied by the weight of the source (the source must implement the <i>Weight</i> 
 * interface).  According to the source data type there are four data-type oriented implementations. 
 * Each of them implements always the <i>DoubleSource</i> interface.
 */
public abstract class Weighted_SumArrayFunction extends AbstractWeightedArrayFunction implements DoubleSource {

	/** Create a sum function on a float array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(WeightedFloatArraySource source) {
		super(source);
	}

	/** Create a sum function on an integer array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(WeightedIntArraySource source) {
		super(source);
	}

	/** Create a sum function on a long array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(WeightedLongArraySource source) {
		super(source);
	}

	/** Create a sum function on a double array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(WeightedDoubleArraySource source) {
		super(source);
	}
	
	/**
	 * SumFunction operating on weighted double source values.
	 */
	public static class Double extends Weighted_SumArrayFunction implements DoubleSource
	{
		/** Create a sum function on a weighted double array source.
		 * @param source The data source.
		 */
		public Double(WeightedDoubleArraySource source) {
			super(source);
		}

		protected double dsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data, double[] weights) {
			dsum = 0.;
			
			for (int i = 0; i < data.length; i++)
				dsum += data[i] * weights[i];
					
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> id) {	return dsum; }
	}
	
	/**
	 * SumFunction operating on weighted long source values.
	 */
	public static class Long extends Weighted_SumArrayFunction //implements LongSource
	{
		/** Create a sum function on a weighted long array source.
		 * @param source The weighted data source.
		 */
		public Long(WeightedLongArraySource source) {
			super(source);
		}

		protected double lsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data, double[] weights) {
			lsum = 0;
			
			for (int i = 0; i < data.length; i++)
				lsum += data[i] * weights[i];
					
		}

//		/* (non-Javadoc)
//		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
//		 */
//		public long getLongValue(Enum<?> id) {	return lsum; }
//
//		/* (non-Javadoc)
//		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
//		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lsum;
		}
	}
	
	/**
	 * SumFunction operating on weighted integer source values.
	 */
	public static class Integer extends Weighted_SumArrayFunction //implements IntSource
	{
		/** Create a sum function on a weighted integer array source.
		 * @param source The weighted data source.
		 */
		public Integer(WeightedIntArraySource source) {
			super(source);
		}

		protected double isum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data, double[] weights) {
			
			isum = 0;
			
			for (int i = 0; i < data.length; i++)
				isum += data[i] * weights[i];
		}

//		/* (non-Javadoc)
//		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
//		 */
//		public int getIntValue(Enum<?> id) {
//			return isum;
//		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return isum;
		}
	}
	
	/**
	 * SumFunction operating on weighted float source values.
	 */
	public static class Float extends Weighted_SumArrayFunction //implements FloatSource
	{
		/** Create a sum function on a weighted float array source.
		 * @param source The data source.
		 */
		public Float(WeightedFloatArraySource source) {
			super(source);
		}

		protected double fsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(float[] data, double[] weights) {			
			fsum = 0;
			
			for (int i = 0; i < data.length; i++)
				fsum += data[i] * weights[i];
		}

//		/* (non-Javadoc)
//		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
//		 */
//		public float getFloatValue(Enum<?> id) {
//			return fsum;
//		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return fsum;
		}
	}
}
