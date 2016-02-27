package microsim.statistics.functions;

import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.ILongArraySource;

/**
 * This class computes the average and variance value of an array of values taken from a data source. 
 * The mean function return always double values, so it implements only the 
 * <i>IDoubleSource</i> interface. <BR>
 * In order to retrieve the mean pass the MeanVarianceFunction.MEAN argument to the getDoubleValue function,
 * while for the variance the MeanVarianceFunction.VARIANCE one.
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
 * <p>
 */
public class MeanVarianceArrayFunction extends AbstractArrayFunction implements IDoubleSource {


	public enum Variables {
		/**	Represent the mean function argument for the getDoubleValue method. */
		Mean,
		/**	Represent the variance function argument for the getDoubleValue method. */
		Variance;		
	}
	
	/** Create a mean function on a float array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(IFloatArraySource source) {
		super(source);
	}

	/** Create a mean function on an integer array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(IIntArraySource source) {
		super(source);
	}

	/** Create a mean function on a long array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(ILongArraySource source) {
		super(source);
	}

	/** Create a mean function on a double array source.
	 * @param source The data source.
	 */
	public MeanVarianceArrayFunction(IDoubleArraySource source) {
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
			variance = (sumOfSquares - mean * sum) / (double) count; 		//This is a population variance as it is the variance of the array's data.
		}		
	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		
		double sum = 0.0;
		double sumOfSquares = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double d = data[i];
			sum += d;
			sumOfSquares += (d * d);
		}
		setValues(data.length, sum, sumOfSquares);
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {
		double sum = 0.0;
		double sumOfSquares = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double d = data[i];
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
		for (int i = 0; i < data.length; i++)
		{
			double d = data[i];
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
		for (int i = 0; i < data.length; i++)
		{
			double d = data[i];
			sum += d;
			sumOfSquares += (d * d);
		}
		setValues(data.length, sum, sumOfSquares);
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		switch ( (MeanVarianceArrayFunction.Variables) variableID)
		{
			case Mean: return mean;
			case Variance: return variance;
			default: throw new UnsupportedOperationException("The function result with id " + variableID + " is not supported.");
		}
	}
	
	
}
