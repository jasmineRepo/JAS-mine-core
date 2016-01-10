package microsim.statistics.functions;

import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.IIntSource;
import microsim.statistics.ILongArraySource;

/**
 * This class computes the number of values in an array taken from a data source. 
 * The mean function return always an int value, so it implements the 
 * <i>IIntSource</i> interface and the standard  <i>IDoubleSource</i> one.
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
public class CountArrayFunction extends AbstractArrayFunction implements IDoubleSource, IIntSource {

	/** Create a count function on a float array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(IFloatArraySource source) {
		super(source);
	}

	/** Create a count function on an integer array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(IIntArraySource source) {
		super(source);
	}

	/** Create a count function on a long array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(ILongArraySource source) {
		super(source);
	}

	/** Create a count function on a double array source.
	 * @param source The data source.
	 */
	public CountArrayFunction(IDoubleArraySource source) {
		super(source);
	}

	protected int count;
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {
		count = data.length;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return (double) count;
	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#getInt()
	 */
	public int getIntValue(Enum<?> id) { return count; }

}
