package microsim.statistics.functions;

import microsim.statistics.*;

/**
 * This class computes the minimum value in an array of source values. According to the source data type
 * there are four data-type oriented implementations. Each of them implements always the 
 * <i>DoubleSource</i> interface.
 */
public abstract class MinArrayFunction extends AbstractArrayFunction implements DoubleSource {

	/** Create a minimum function on a float array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(FloatArraySource source) { super(source); }

	/** Create a minimum function on an int array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(IntArraySource source) { super(source);	}

	/** Create a minimum function on a long array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(LongArraySource source) {	super(source); }

	/** Create a minimum function on a double array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(DoubleArraySource source) {	super(source); }

	/**
	 * MinFunction operating on double source values.
	 */
	public static class Double extends MinArrayFunction implements DoubleSource
	{
		/** Create a minimum function on a double array source.
		 * @param source The data source.
		 */
		public Double(DoubleArraySource source) { super(source);	}

		protected double min;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {
			
			min = java.lang.Double.MAX_VALUE;

			for (double datum : data)
				if (min > datum)
					min = datum;
				
		}

		/* (non-Javadoc)
		 * @see jas.statistics.LongSource#getLongValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return min;
		}
	}
		
	/**
	 * MinFunction operating on long source values.
	 */
	public static class Long extends MinArrayFunction implements LongSource
	{
		/** Create a minimum function on a long array source.
		 * @param source The data source.
		 */
		public Long(LongArraySource source) {
			super(source);
		}

		protected long lmin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {
			
			lmin = java.lang.Long.MAX_VALUE;

			for (long datum : data)
				if (lmin > datum)
					lmin = datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.LongSource#getLongValue(int)
		 */
		public long getLongValue(Enum<?> variableID) {
			return lmin;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lmin;
		}
	}
	
	/**
	 * MinFunction operating on integer source values.
	 */
	public static class Integer extends MinArrayFunction implements IntSource
	{
		/** Create a minimum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IntArraySource source) {
			super(source);
		}

		protected int imin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {
			
			imin = java.lang.Integer.MAX_VALUE;

			for (int datum : data)
				if (imin > datum)
					imin = datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.LongSource#getLongValue(int)
		 */
		public int getIntValue(Enum<?> variableID) {
			return imin;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return imin;
		}
	}
	
	/**
	 * MinFunction operating on float source values.
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
	public static class Float extends MinArrayFunction implements FloatSource
	{
		/** Create a minimum function on a float array source.
		 * @param source The data source.
		 */
		public Float(FloatArraySource source) {
			super(source);
		}

		protected float fmin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(float[] data) {
			
			fmin = java.lang.Float.MAX_VALUE;

			for (float datum : data)
				if (fmin > datum)
					fmin = datum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.FloatSource#getFloatValue(int)
		 */
		public float getFloatValue(Enum<?> variableID) {
			return fmin;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return fmin;
		}
	}

}
