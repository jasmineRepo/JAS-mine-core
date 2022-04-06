package microsim.statistics.weighted.functions;

import microsim.statistics.IDoubleSource;
import microsim.statistics.weighted.IWeightedDoubleArraySource;
import microsim.statistics.weighted.IWeightedFloatArraySource;
import microsim.statistics.weighted.IWeightedIntArraySource;
import microsim.statistics.weighted.IWeightedLongArraySource;

/**
 * This class computes the (weighted) average (mean) value of an array of values taken from a data source, 
 * weighted by corresponding weights:
 * 		weighted mean = sum (values * weights) / sum (weights)
 * Note that the array of weights must have the same length as the array of values, otherwise an exception 
 * will be thrown.
 * The mean function return always double values, so it implements only the 
 * <i>IDoubleSource</i> interface.
 *
 * <p>Title: JAS-mine</p>
 * <p>Description: Java Agent-based Simulation library.  Modelling in a Networked Environment.</p>
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
 * <p>
 */
public class Weighted_MeanArrayFunction extends AbstractWeightedArrayFunction implements IDoubleSource {

	/** Create a mean function on a float array source.
	 * @param source The data source.
	 */
	public Weighted_MeanArrayFunction(IWeightedFloatArraySource source) {
		super(source);
	}

	/** Create a mean function on an integer array source.
	 * @param source The data source.
	 */
	public Weighted_MeanArrayFunction(IWeightedIntArraySource source) {
		super(source);
	}

	/** Create a mean function on a long array source.
	 * @param source The data source.
	 */
	public Weighted_MeanArrayFunction(IWeightedLongArraySource source) {
		super(source);
	}

	/** Create a mean function on a (weighted) double array source.
	 * @param source The weighted data source.
	 */
	public Weighted_MeanArrayFunction(IWeightedDoubleArraySource source) {
		super(source);
	}

	protected double weightedMean;
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data, double[] weights) {
		
//		if(data.length != weights.length) {
//			throw new IllegalArgumentException("Error - the length of the array of data does not match the length of the array of weights!");
//		}
		
		weightedMean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			double denominator = 0.;
			for (int i = 0; i < data.length; i++) {
				sum += data[i] * weights[i];
				denominator += weights[i];
			}
			weightedMean = sum / denominator;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data, double[] weights) {
//		if(data.length != weights.length) {
//		throw new IllegalArgumentException("Error - the length of the array of data does not match the length of the array of weights!");
//	}
	
		weightedMean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			double denominator = 0.;
			for (int i = 0; i < data.length; i++) {
				sum += data[i] * weights[i];
				denominator += weights[i];
			}
			weightedMean = sum / denominator;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data, double[] weights) {
		
//		if(data.length != weights.length) {
//			throw new IllegalArgumentException("Error - the length of the array of data does not match the length of the array of weights!");
//		}
	
		weightedMean = 0.0;
		if (data.length != 0) {
		
			double sum = 0.0;
			double denominator = 0.;
			for (int i = 0; i < data.length; i++) {
				sum += data[i] * weights[i];
				denominator += weights[i];
			}
			weightedMean = sum / denominator;
		}
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return weightedMean;
	}
	
	
}
