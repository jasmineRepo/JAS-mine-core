package microsim.statistics.functions;

import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.ILongArraySource;
import microsim.statistics.IUpdatableSource;

/**
 * This class represents the skeleton for all the function which opeate on array of native data type values.
 * Each inheriting class automatically implements the <i>IUpdatableSource</i> and the <i>ISimEventListener</i>,
 * which are managed by the AbstractArrayFunction.
 * 
 * <p>Title: JAS</p>
 * <p>Description: Java Agent-based Simulation library</p>
 * <p>Copyright (C) 2002 Michele Sonnessa</p>
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
 * @author Michele Sonnessa
 * 
 */
public abstract class AbstractArrayFunction extends AbstractFunction {

	protected static final int TYPE_DBL = 0;
	protected static final int TYPE_FLT = 1;
	protected static final int TYPE_INT = 2;
	protected static final int TYPE_LNG = 3;

	protected IDoubleArraySource dblSource;
	protected IFloatArraySource fltSource;
	protected IIntArraySource intSource;
	protected ILongArraySource lngSource;
	protected int type;
	
	
	/** Create a function on a double array source.
	 * @param source The data source.
	 */
	public AbstractArrayFunction(IDoubleArraySource source)
	{
		super();
		type = TYPE_DBL;
		dblSource = source;
	}
	
	/** Create a function on a float array source.
	 * @param source The data source.
	 */
	public AbstractArrayFunction(IFloatArraySource source)
	{
		super();
		type = TYPE_FLT;
		fltSource = source;
	}
	
	/** Create a function on an integer array source.
	 * @param source The data source.
	 */
	public AbstractArrayFunction(IIntArraySource source)
	{
		super();
		type = TYPE_INT;
		intSource = source;
	}
	
	/** Create a function on a long array source.
	 * @param source The data source.
	 */
	public AbstractArrayFunction(ILongArraySource source)
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
				apply(dblSource.getDoubleArray()); 
				break;
			case TYPE_FLT: 
				if (fltSource instanceof IUpdatableSource)
					((IUpdatableSource) fltSource).updateSource(); 
				apply(fltSource.getFloatArray()); 
				break;
			case TYPE_INT: 
				if (intSource instanceof IUpdatableSource)
					((IUpdatableSource) intSource).updateSource(); 
				apply(intSource.getIntArray()); 
				break;
			case TYPE_LNG:
				if (lngSource instanceof IUpdatableSource)
					((IUpdatableSource) lngSource).updateSource(); 			 
				apply(lngSource.getLongArray()); 
				break;
		}
	}
	

	/**
	 * Apply the function on a the given array of double values.
	 * @param data A source array of values.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(double[] data)
	{
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of double values.");
	}

	/**
	 * Apply the function on a the given array of float values.
	 * @param data A source array of values.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(float[] data)
	{
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of float values.");
	}

	/**
	 * Apply the function on a the given array of integer values.
	 * @param data A source array of values.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(int[] data)
	{
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of integer values.");
	}

	/**
	 * Apply the function on a the given array of long values.
	 * @param data A source array of values.
	 * @throws UnsupportedOperationException If the function is not able to work on double data type.
	 */
	public void apply(long[] data)
	{
		throw new UnsupportedOperationException("This function class cannot be applied to arrays of long values.");
	}
	


}
