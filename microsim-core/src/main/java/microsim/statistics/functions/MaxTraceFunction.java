package microsim.statistics.functions;

import microsim.event.CommonEventType;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatSource;
import microsim.statistics.IIntSource;
import microsim.statistics.ILongSource;
import microsim.statistics.IUpdatableSource;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.FloatInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

/**
 * A MixFunction object is to collect data over time, computing some statistics
 * on the fly, without storing the data in memory. It is particularly useful when the user
 * need to compute basic statistics on data sources, without affecting the memory occupancy. 
 * The memoryless series computes automatically the statistics using accumulation variables
 * and counters.<br> This statistic computer should be used when possible, particularly when
 * the simulation model has to run for a long time, condition which implies the growth of the
 * memory occupancy. Moreover the MemorylessSeries objects are much faster than the Series one,
 * because they pre-compute the statistics operation step by step. Trying to compute a mean
 * of a Series object, force the Mean function to sum all the values, every time series is updated. 
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
public abstract class MaxTraceFunction extends AbstractFunction implements IDoubleSource  {
					
	public enum Variables {
		LastValue,
		Max;
	}
	
	protected int count = 0;
	
	/** Collect a value from the source. */
	public void applyFunction() { count++;	}
		
	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	@Override
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new SimulationRuntimeException("The SimpleStatistics object does not support " + type + " operation.");
	}

	/**
	 * An implementation of the MemorylessSeries class, which manages long type data sources.
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
	public static class Long extends MaxTraceFunction implements ILongSource {
		protected long max = java.lang.Long.MIN_VALUE;
	
		protected ILongSource target;
		private Enum<?> valueID;

		private long lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param source The ILongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(ILongSource source, Enum<?> valueID) {
			super();
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Object source, String valueName, boolean getFromMethod) {
			super();
			target = new LongInvoker(source, valueName, getFromMethod);
			valueID = ILongSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void applyFunction() {
			super.applyFunction();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getLongValue(valueID);

			if (lastRead > max)
				max = lastRead;
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			switch ( (MaxTraceFunction.Variables) valueID) {
				case LastValue :			return (double) lastRead;
				case Max :				return (double) max;
				default :	throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}
		/** Return the result of a given statistic.
			*  @param valueID One of the F_ constants representing available statistics.
			*  @return The computed value.
			*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public long getLongValue(Enum<?> valueID) {
			switch ( (MaxTraceFunction.Variables) valueID) {
				case LastValue :			return lastRead;
				case Max :				return  max;
				default :	throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}


		/** Return the last double value read from the source object.
			*  @return A double value collected at the last reading operation.*/
		public long getLastValue() {
			return lastRead;
		}
		/**
		 * The current maximum value.
		 * @return The maximum value.
		 */
		public long getMax()
		{
			return max;
		}
	}

	/**
	 * An implementation of the MemorylessSeries class, which manages double type data sources.
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
	 *
	 */
	public static class Double extends MaxTraceFunction implements IDoubleSource {
		protected double max = java.lang.Double.MIN_VALUE;
		
		protected IDoubleSource target;
		private Enum<?> valueID;

		private double lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param source The IDblSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Double(IDoubleSource source, Enum<?> valueID) {
			super();
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Object source, String valueName, boolean getFromMethod) {
			super();
			target = new DoubleInvoker(source, valueName, getFromMethod);
			valueID = IDoubleSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void applyFunction() {
			super.applyFunction();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getDoubleValue(valueID);

			if (lastRead > max)
				max = lastRead;
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			switch( (MaxTraceFunction.Variables) valueID)
			{
				case LastValue: return lastRead;
				case Max: return max;
				default: throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}

		/** Return the last double value read from the source object.
			*  @return A double value collected at the last reading operation.*/
		public double getLastValue() {
			return lastRead;
		}

		/** 
		 * Return the current max value.
		 * @return The maximum value.
		 */
		public double getMax() { return max; }
	}

	/**
	 * An implementation of the MemorylessSeries class, which manages integer type data sources.
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
	public static class Integer extends MaxTraceFunction implements IIntSource {
		protected int max = java.lang.Integer.MIN_VALUE;
		
		protected IIntSource target;
		private Enum<?> valueID;

		private int lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param source The IIntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(IIntSource source, Enum<?> valueID) {
			super();
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Object source, String valueName, boolean getFromMethod) {
			super();
			target = new IntegerInvoker(source, valueName, getFromMethod);
			valueID = IIntSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void applyFunction() {
			super.applyFunction();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getIntValue(valueID);

			if (lastRead > max)
				max = lastRead;
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			switch( (MaxTraceFunction.Variables) valueID)
			{
				case LastValue: return lastRead;
				case Max: return max;
				default: throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}
		
		public int getIntValue(Enum<?> valueID) {
			switch((MaxTraceFunction.Variables) valueID)
			{
				case LastValue: return lastRead;
				case Max: return max;
				default: throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}
		/** Return the last double value read from the source object.
			*  @return A double value collected at the last reading operation.*/
		public int getLastValue() {
			return lastRead;
		}
		
		/** The maximum function.
			*  @return The maximum value.*/
		public int getMax() {
			return max;
		}

	}

	/**
	 * An implementation of the MemorylessSeries class, which manages float type data sources.
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
	public static class Float extends MaxTraceFunction implements IFloatSource {
		protected float max = java.lang.Float.MIN_VALUE;
		
		protected IFloatSource target;
		private Enum<?> valueID;

		private float lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param source The IFloatSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Float(IFloatSource source, Enum<?> valueID) {
			super();
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Float(Object source, String valueName, boolean getFromMethod) {
			super();
			target = new FloatInvoker(source, valueName, getFromMethod);
			valueID = IFloatSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void applyFunction() {
			super.applyFunction();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getFloatValue(valueID);

			if (lastRead > max)
				max = lastRead;
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			switch ( (MaxTraceFunction.Variables) valueID) {
				case LastValue :			return (double) lastRead;
				case Max :				return (double) max;
				default :	throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}
		/** Return the result of a given statistic.
			*  @param valueID One of the F_ constants representing available statistics.
			*  @return The computed value.
			*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public float getFloatValue(Enum<?> valueID) {
			switch ((MaxTraceFunction.Variables) valueID) {
				case LastValue :			return lastRead;
				case Max :				return max;
				default :	throw new UnsupportedOperationException("The computer does not support an operation with id " + valueID);
			}
		}

				/** Return the last double value read from the source object.
			*  @return A double value collected at the last reading operation.*/
		public float getLastValue() {
			return lastRead;
		}

		/** The maximum function.
			*  @return The maximum value.*/
		public float getMax() {
			return max;
		}

	}
}