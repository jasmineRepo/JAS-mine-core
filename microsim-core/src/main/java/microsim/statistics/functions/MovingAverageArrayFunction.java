package microsim.statistics.functions;

import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.ILongArraySource;

/**
 * This class computes the average of the last given number of values in an array taken from a data source. 
 * The mean function return always a double value, so it implements the 
 * <i>IDoubleSource</i> interface and the standard  <i>IDoubleSource</i> one.
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
public class MovingAverageArrayFunction extends AbstractArrayFunction implements IDoubleSource {

	protected double mean;
	protected int window;
	
	/** Create a count function on a float array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(IFloatArraySource source, int window) {
		super(source);
		this.window = window;
	}

	/** Create a count function on an integer array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(IIntArraySource source, int window) {
		super(source);
		this.window = window;
	}

	/** Create a count function on a long array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(ILongArraySource source, int window) {
		super(source);
		this.window = window;
	}

	/** Create a count function on a double array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(IDoubleArraySource source, int window) {
		super(source);
		this.window = window;
	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = data.length; i > firstElement ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = data.length; i > firstElement ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = data.length; i > firstElement ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = firstElement; i < data.length ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return mean;
	}
	
}
