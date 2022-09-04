package microsim.statistics.weighted.functions;

import microsim.statistics.UpdatableSource;
import microsim.statistics.weighted.WeightedDoubleArraySource;
import microsim.statistics.weighted.WeightedFloatArraySource;
import microsim.statistics.weighted.WeightedIntArraySource;
import microsim.statistics.weighted.WeightedLongArraySource;
import microsim.statistics.functions.AbstractFunction;

/**
 * This class represents the skeleton for all the function which operate on array of native data type values,
 * appropriately weighted by weights specified in a corresponding array of doubles.
 * Each inheriting class automatically implements the <i>UpdatableSource</i> and the <i>ISimEventListener</i>,
 * which are managed by the AbstractWeightedArrayFunction.
 */
public abstract class AbstractWeightedArrayFunction extends AbstractFunction {

	protected static final int TYPE_DBL = 0;
	protected static final int TYPE_FLT = 1;
	protected static final int TYPE_INT = 2;
	protected static final int TYPE_LNG = 3;

	protected WeightedDoubleArraySource dblSource;
	protected WeightedFloatArraySource fltSource;
	protected WeightedIntArraySource intSource;
	protected WeightedLongArraySource lngSource;
	protected int type;


	/** Create a function on a double array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(WeightedDoubleArraySource source)
	{
		super();
		type = TYPE_DBL;
		dblSource = source;
	}

	/** Create a function on a float array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(WeightedFloatArraySource source)
	{
		super();
		type = TYPE_FLT;
		fltSource = source;
	}

	/** Create a function on an integer array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(WeightedIntArraySource source)
	{
		super();
		type = TYPE_INT;
		intSource = source;
	}

	/** Create a function on a long array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(WeightedLongArraySource source)
	{
		super();
		type = TYPE_LNG;
		lngSource = source;
	}

	/**
	 * Force the function to update itself. If the data source implements the <i>UpdatableSource</i>
	 * interface it is updated before reading data.
	 */
	public void applyFunction()
	{
		switch (type) {//default branch?
			case TYPE_DBL -> {
				if (dblSource instanceof UpdatableSource)
					((UpdatableSource) dblSource).updateSource();
				apply(dblSource.getDoubleArray(), dblSource.getWeights());
			}
			case TYPE_FLT -> {
				if (fltSource instanceof UpdatableSource)
					((UpdatableSource) fltSource).updateSource();
				apply(fltSource.getFloatArray(), fltSource.getWeights());
			}
			case TYPE_INT -> {
				if (intSource instanceof UpdatableSource)
					((UpdatableSource) intSource).updateSource();
				apply(intSource.getIntArray(), intSource.getWeights());
			}
			case TYPE_LNG -> {
				if (lngSource instanceof UpdatableSource)
					((UpdatableSource) lngSource).updateSource();
				apply(lngSource.getLongArray(), lngSource.getWeights());
			}
		}
	}


	/**
	 * Apply the function on a the given array of double values.
	 * @param data A source array of values.
	 * @param weights An array of weights.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(double[] data, double[] weights)
	{
		if(data.length != weights.length) {
			throw new IllegalArgumentException("Error: length of data array ( = " + data.length + " and length of weights array ( = " + weights.length + " do not match!");
		}
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of double values.");
	}

	/**
	 * Apply the function on a the given array of float values.
	 * @param data A source array of values.
	 * @param weights An array of weights.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(float[] data, double[] weights)
	{
		if(data.length != weights.length) {
			throw new IllegalArgumentException("Error: length of data array ( = " + data.length + " and length of weights array ( = " + weights.length + " do not match!");
		}
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of float values.");
	}

	/**
	 * Apply the function on a the given array of integer values.
	 * @param data A source array of values.
	 * @param weights An array of weights.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(int[] data, double[] weights)
	{
		if(data.length != weights.length) {
			throw new IllegalArgumentException("Error: length of data array ( = " + data.length + " and length of weights array ( = " + weights.length + " do not match!");
		}
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of integer values.");
	}

	/**
	 * Apply the function on a the given array of long values.
	 * @param data A source array of values.
	 * @param weights An array of weights.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(long[] data, double[] weights)
	{
		if(data.length != weights.length) {
			throw new IllegalArgumentException("Error: length of data array ( = " + data.length + " and length of weights array ( = " + weights.length + " do not match!");
		}
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of long values.");
	}



}
