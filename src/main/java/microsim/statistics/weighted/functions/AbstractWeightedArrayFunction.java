package microsim.statistics.weighted.functions;

import microsim.statistics.weighted.IWeightedDoubleArraySource;
import microsim.statistics.weighted.IWeightedFloatArraySource;
import microsim.statistics.weighted.IWeightedIntArraySource;
import microsim.statistics.weighted.IWeightedLongArraySource;
import microsim.statistics.IUpdatableSource;
import microsim.statistics.functions.AbstractFunction;

/**
 * This class represents the skeleton for all the function which operate on array of native data type values,
 * appropriately weighted by weights specified in a corresponding array of doubles.
 * Each inheriting class automatically implements the <i>IUpdatableSource</i> and the <i>ISimEventListener</i>,
 * which are managed by the AbstractWeightedArrayFunction.
 * 
 * <p>Title: JAS-mine</p>
 * <p>Description: Java Agent-based Simulation library.  Modelling in a Networked Environment</p>
 * <p>Copyright (C) 2017 Michele Sonnessa and Ross Richardson</p>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * @author Michele Sonnessa and Ross Richardson
 * 
 */
public abstract class AbstractWeightedArrayFunction extends AbstractFunction {

	protected static final int TYPE_DBL = 0;
	protected static final int TYPE_FLT = 1;
	protected static final int TYPE_INT = 2;
	protected static final int TYPE_LNG = 3;

	protected IWeightedDoubleArraySource dblSource;
	protected IWeightedFloatArraySource fltSource;
	protected IWeightedIntArraySource intSource;
	protected IWeightedLongArraySource lngSource;
	protected int type;
	
	
	/** Create a function on a double array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(IWeightedDoubleArraySource source)
	{
		super();
		type = TYPE_DBL;
		dblSource = source;
	}
	
	/** Create a function on a float array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(IWeightedFloatArraySource source)
	{
		super();
		type = TYPE_FLT;
		fltSource = source;
	}
	
	/** Create a function on an integer array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(IWeightedIntArraySource source)
	{
		super();
		type = TYPE_INT;
		intSource = source;
	}
	
	/** Create a function on a long array source.
	 * @param source The data source.
	 */
	public AbstractWeightedArrayFunction(IWeightedLongArraySource source)
	{
		super();
		type = TYPE_LNG;
		lngSource = source;
	}		
	
	/**
	 * Force the function to update itself. If the data source implements the <i>IUpdatableSource</i> 
	 * interface it is updated before reading data.
	 */
	public void applyFunction()
	{
		switch (type)
		{
			case TYPE_DBL:
				if (dblSource instanceof IUpdatableSource)
					((IUpdatableSource) dblSource).updateSource(); 
				apply(dblSource.getDoubleArray(), dblSource.getWeights()); 
				break;
			case TYPE_FLT: 
				if (fltSource instanceof IUpdatableSource)
					((IUpdatableSource) fltSource).updateSource(); 
				apply(fltSource.getFloatArray(), fltSource.getWeights()); 
				break;
			case TYPE_INT: 
				if (intSource instanceof IUpdatableSource)
					((IUpdatableSource) intSource).updateSource(); 
				apply(intSource.getIntArray(), intSource.getWeights()); 
				break;
			case TYPE_LNG:
				if (lngSource instanceof IUpdatableSource)
					((IUpdatableSource) lngSource).updateSource(); 			 
				apply(lngSource.getLongArray(), lngSource.getWeights()); 
				break;
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
