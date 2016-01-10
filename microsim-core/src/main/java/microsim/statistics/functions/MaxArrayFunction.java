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
 * This class computes the maximum value in an array of source values. According to the source data type
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
public abstract class MaxArrayFunction extends AbstractArrayFunction implements IDoubleSource {

	/** Create a maximum function on a float array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(IFloatArraySource source) {
		super(source);
	}

	/** Create a maximum function on an integer array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(IIntArraySource source) {
		super(source);
	}

	/** Create a maximum function on a long array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(ILongArraySource source) {
		super(source);
	}

	/** Create a maximum function on a double array source.
	 * @param source The data source.
	 */
	public MaxArrayFunction(IDoubleArraySource source) {
		super(source);
	}
	
	/**
	 * MaxFunction operating on double source values.
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
	public static class Double extends MaxArrayFunction 
	{
		/** Create a maximum function on a double array source.
		 * @param source The data source.
		 */
		public Double(IDoubleArraySource source) {
			super(source);
		}

		protected double dmax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {
			dmax = java.lang.Double.MIN_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (dmax < data[i])
					dmax = data[i];
					
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> id) {	return dmax; }
	}
	
	/**
	 * MaxFunction operating on long source values.
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
	public static class Long extends MaxArrayFunction implements ILongSource
	{
		/** Create a maximum function on a long array source.
		 * @param source The data source.
		 */
		public Long(ILongArraySource source) {
			super(source);
		}

		protected long lmax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {
			lmax = java.lang.Long.MIN_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (lmax < data[i])
					lmax = data[i];
					
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public long getLongValue(Enum<?> id) {	return lmax; }

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lmax;
		}
	}
	
	/**
	 * MaxFunction operating on integer source values.
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
	public static class Integer extends MaxArrayFunction implements IIntSource
	{
		/** Create a maximum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IIntArraySource source) {
			super(source);
		}

		protected int imax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {
			
			imax = java.lang.Integer.MIN_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (imax < data[i])
					imax = data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public int getIntValue(Enum<?> id) {
			return imax;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return imax;
		}
	}
	
	/**
	 * MaxFunction operating on float source values.
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
	public static class Float extends MaxArrayFunction implements IFloatSource
	{
		/** Create a maximum function on an float array source.
		 * @param source The data source.
		 */
		public Float(IFloatArraySource source) {
			super(source);
		}

		protected float fmax;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(float[] data) {			
			fmax = java.lang.Float.MIN_VALUE;
			
			for (int i = 0; i < data.length; i++)
				if (fmax > data[i])
					fmax = data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public float getFloatValue(Enum<?> id) {
			return fmax;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return fmax;
		}
	}
}
