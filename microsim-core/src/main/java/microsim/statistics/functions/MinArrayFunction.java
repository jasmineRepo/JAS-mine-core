package microsim.statistics.functions;

import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IFloatSource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.IIntSource;
import microsim.statistics.ILongArraySource;
import microsim.statistics.ILongSource;

/**
 * This class computes the minimum value in an array of source values. According to the source data type
 * there are four data-type oriented implementations. Each of them implements always the 
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
 * @author Michele Sonnessa
 * <p>
 */
public abstract class MinArrayFunction extends AbstractArrayFunction implements IDoubleSource {

	/** Create a minimum function on a float array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(IFloatArraySource source) { super(source); }

	/** Create a minimum function on an int array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(IIntArraySource source) { super(source);	}

	/** Create a minimum function on a long array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(ILongArraySource source) {	super(source); }

	/** Create a minimum function on a double array source.
	 * @param source The data source.
	 */
	public MinArrayFunction(IDoubleArraySource source) {	super(source); }

	/**
	 * MinFunction operating on double source values.
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
	public static class Double extends MinArrayFunction implements IDoubleSource
	{
		/** Create a minimum function on a double array source.
		 * @param source The data source.
		 */
		public Double(IDoubleArraySource source) { super(source);	}

		protected double min;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {
			
			min = java.lang.Double.MAX_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (min > data[i])
					min = data[i];
				
		}

		/* (non-Javadoc)
		 * @see jas.statistics.ILongSource#getLongValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return min;
		}
	}
		
	/**
	 * MinFunction operating on long source values.
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
	public static class Long extends MinArrayFunction implements ILongSource
	{
		/** Create a minimum function on a long array source.
		 * @param source The data source.
		 */
		public Long(ILongArraySource source) {
			super(source);
		}

		protected long lmin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {
			
			lmin = java.lang.Long.MAX_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (lmin > data[i])
					lmin = data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.ILongSource#getLongValue(int)
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
	public static class Integer extends MinArrayFunction implements IIntSource
	{
		/** Create a minimum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IIntArraySource source) {
			super(source);
		}

		protected int imin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {
			
			imin = java.lang.Integer.MAX_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (imin > data[i])
					imin = data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.ILongSource#getLongValue(int)
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
	public static class Float extends MinArrayFunction implements IFloatSource
	{
		/** Create a minimum function on a float array source.
		 * @param source The data source.
		 */
		public Float(IFloatArraySource source) {
			super(source);
		}

		protected float fmin;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(float[] data) {
			
			fmin = java.lang.Float.MAX_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (fmin > data[i])
					fmin = data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IFloatSource#getFloatValue(int)
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
