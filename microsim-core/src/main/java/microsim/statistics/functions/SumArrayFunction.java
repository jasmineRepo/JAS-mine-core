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
 * This class computes the sum of an array of source values. According to the source data type
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
public abstract class SumArrayFunction extends AbstractArrayFunction implements IDoubleSource {

	/** Create a sum function on a float array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(IFloatArraySource source) {
		super(source);
	}

	/** Create a sum function on an integer array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(IIntArraySource source) {
		super(source);
	}

	/** Create a sum function on a long array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(ILongArraySource source) {
		super(source);
	}

	/** Create a sum function on a double array source.
	 * @param source The data source.
	 */
	public SumArrayFunction(IDoubleArraySource source) {
		super(source);
	}
	
	/**
	 * SumFunction operating on double source values.
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
	public static class Double extends SumArrayFunction implements IDoubleSource
	{
		/** Create a sum function on a double array source.
		 * @param source The data source.
		 */
		public Double(IDoubleArraySource source) {
			super(source);
		}

		protected double dsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(double[] data) {
			dsum = 0.;
			
			for (int i = 0; i < data.length; i++)
				dsum += data[i];
					
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> id) {	return dsum; }
	}
	
	/**
	 * SumFunction operating on long source values.
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
	public static class Long extends SumArrayFunction implements ILongSource
	{
		/** Create a sum function on a long array source.
		 * @param source The data source.
		 */
		public Long(ILongArraySource source) {
			super(source);
		}

		protected long lsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(long[] data) {
			lsum = 0;
			
			for (int i = 0; i < data.length; i++)
				lsum += data[i];
					
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public long getLongValue(Enum<?> id) {	return lsum; }

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return lsum;
		}
	}
	
	/**
	 * SumFunction operating on integer source values.
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
	public static class Integer extends SumArrayFunction implements IIntSource
	{
		/** Create a sum function on an integer array source.
		 * @param source The data source.
		 */
		public Integer(IIntArraySource source) {
			super(source);
		}

		protected int isum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(int[] data) {
			
			isum = 0;
			
			for (int i = 0; i < data.length; i++)
				isum += data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public int getIntValue(Enum<?> id) {
			return isum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return isum;
		}
	}
	
	/**
	 * SumFunction operating on float source values.
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
	public static class Float extends SumArrayFunction implements IFloatSource
	{
		/** Create a sum function on an float array source.
		 * @param source The data source.
		 */
		public Float(IFloatArraySource source) {
			super(source);
		}

		protected float fsum;

		/* (non-Javadoc)
		 * @see jas.statistics.functions.IArrayFunction#apply(long[])
		 */
		public void apply(float[] data) {			
			fsum = 0;
			
			for (int i = 0; i < data.length; i++)
				fsum += data[i];
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public float getFloatValue(Enum<?> id) {
			return fsum;
		}

		/* (non-Javadoc)
		 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
		 */
		public double getDoubleValue(Enum<?> variableID) {
			return fsum;
		}
	}
}
