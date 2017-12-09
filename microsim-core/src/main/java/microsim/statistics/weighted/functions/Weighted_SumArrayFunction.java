package microsim.statistics.weighted.functions;

import microsim.statistics.weighted.IWeightedDoubleArraySource;
import microsim.statistics.weighted.IWeightedFloatArraySource;
import microsim.statistics.weighted.IWeightedIntArraySource;
import microsim.statistics.weighted.IWeightedLongArraySource;

import microsim.statistics.IDoubleSource;

/**
 * This class computes the sum of an array of source values, with each element of the array
 * multiplied by the weight of the source (the source must implement the <i>Weight</i> 
 * interface).  According to the source data type there are four data-type oriented implementations. 
 * Each of them implements always the <i>IDoubleSource</i> interface.
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
 * <p>
 */
public abstract class Weighted_SumArrayFunction extends AbstractWeightedArrayFunction implements IDoubleSource {

	/** Create a sum function on a float array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(IWeightedFloatArraySource source) {
		super(source);
	}

	/** Create a sum function on an integer array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(IWeightedIntArraySource source) {
		super(source);
	}

	/** Create a sum function on a long array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(IWeightedLongArraySource source) {
		super(source);
	}

	/** Create a sum function on a double array weighted-source.
	 * @param source The weighted data source.
	 */
	public Weighted_SumArrayFunction(IWeightedDoubleArraySource source) {
		super(source);
	}
	
	/**
	 * SumFunction operating on weighted double source values.
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
	 * <p>
	 *
	 */
	public static class Double extends Weighted_SumArrayFunction implements IDoubleSource
	{
		/** Create a sum function on a weighted double array source.
		 * @param source The data source.
		 */
		public Double(IWeightedDoubleArraySource source) {
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
	 * <p>
	 * 
	 */
	public static class Long extends Weighted_SumArrayFunction //implements ILongSource
	{
		/** Create a sum function on a weighted long array source.
		 * @param source The weighted data source.
		 */
		public Long(IWeightedLongArraySource source) {
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
	 * <p>
	 * 
	 */
	public static class Integer extends Weighted_SumArrayFunction //implements IIntSource
	{
		/** Create a sum function on a weighted integer array source.
		 * @param source The weighted data source.
		 */
		public Integer(IWeightedIntArraySource source) {
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
	 * <p>
	 * 
	 */
	public static class Float extends Weighted_SumArrayFunction //implements IFloatSource
	{
		/** Create a sum function on a weighted float array source.
		 * @param source The data source.
		 */
		public Float(IWeightedFloatArraySource source) {
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
