package microsim.statistics.functions;

import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.ILongArraySource;

/**
 * This class computes the average value of an array of values taken from a data source. 
 * The mean function return always double values, so it implements only the 
 * <i>IDoubleSource</i> interface.
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
 * @author Michele Sonnessa and Ross Richardson
 * <p>
 */
public class MeanArrayFunction extends AbstractArrayFunction implements IDoubleSource {

	/** Create a mean function on a float array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(IFloatArraySource source) {
		super(source);
	}

	/** Create a mean function on an integer array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(IIntArraySource source) {
		super(source);
	}

	/** Create a mean function on a long array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(ILongArraySource source) {
		super(source);
	}

	/** Create a mean function on a double array source.
	 * @param source The data source.
	 */
	public MeanArrayFunction(IDoubleArraySource source) {
		super(source);
	}

	protected double mean;
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		
		mean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			for (int i = 0; i < data.length; i++) {
				sum += data[i];
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {
		
		mean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			for (int i = 0; i < data.length; i++) {
				sum += data[i];
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {
		
		mean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			for (int i = 0; i < data.length; i++) {
				sum += data[i];
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {

		mean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			for (int i = 0; i < data.length; i++) {
				sum += data[i];
			}
			mean = sum / data.length;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return mean;
	}
	
	
}
