package microsim.statistics.functions;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
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
 * @author Michele Sonnessa
 * <p>
 */
public abstract class MultiTraceFunction implements IDoubleSource, IUpdatableSource, EventListener  {
				
	public enum Variables {
		/** Return the last collected value. */
		LastValue,
		/** Return the minimum collected value. */
		Min,
		/** Return the maximum collected value. */
		Max,
		/** Return the mean of the collected values. */
		Mean,
		/** Return the variance of the collected values. */
		Variance,
		/** Return the number of collected values. */
		Count,
		/** Return the sum of collected values. */
		Sum;
	}
	
	protected int count = 0;
	
	/** Collect a value from the source. */
	public void updateSource() { count++;	}
	
	public abstract double getMean();
	public abstract double getVariance();
	public int getCount() { return count;	}
	
	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @param actionType The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new SimulationRuntimeException("SimpleStatistics object does not support " + type + " operation.");
	}
	

	/** Compute one of the available statistical functions on the collected data. */
	public double getDoubleValue(Enum<?> valueID)
	{
		switch( (Variables) valueID)
		{			
			case Mean: 			return getMean();
			case Variance: 	return getVariance();
			case Count:			return (double) getCount();
			default:
				throw new UnsupportedOperationException("The valueID " + valueID + " is not supported.");
		}
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
	public static class Long extends MultiTraceFunction implements ILongSource {
		protected long max = java.lang.Long.MIN_VALUE;
		protected long min = java.lang.Long.MAX_VALUE;
		protected long sum = 0, sumSquare = 0;
	
		protected ILongSource target;
		private Enum<?> valueID;

		private long lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param name Name of the statistic object.
		 *  @param source The IDblSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(ILongSource source, Enum<?> valueID) {
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param name Name of the statistic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Object source, String valueName, boolean getFromMethod) {
			target = new LongInvoker(source, valueName, getFromMethod);
			valueID = ILongSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void updateSource() {
			super.updateSource();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getLongValue(valueID);

			if (lastRead < min)
				min = lastRead;
			if (lastRead > max)
				max = lastRead;
			sum += lastRead;
			sumSquare += (lastRead * lastRead);
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			switch ( (MultiTraceFunction.Variables) valueID) {
				case LastValue :			return (double) lastRead;
				case Max :				return (double) max;
				case Min :				return (double) min;
				case Sum:					return (double) sum;	
				default :
					return super.getDoubleValue(valueID);
			}
		}
		/** Return the result of a given statistic.
			*  @param valueID One of the F_ constants representing available statistics.
			*  @return The computed value.
			*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public long getLongValue(Enum<?> valueID) {
			if (valueID.equals( IIntSource.Variables.Default))
				return lastRead;
			switch ( (MultiTraceFunction.Variables) valueID ) {
				case LastValue:		return lastRead;
				case Max :			return max;
				case Min :			return min;
				case Count :		return count;
				case Sum:				return sum;	
				default :
					throw new UnsupportedOperationException(
						valueID
							+ " is not a defined function for " + getClass() + ".");
			}
		}

		/** The variance function.
		 *  @return The variance value.*/
		public double getVariance() {
			if (count > 1)
				return (sumSquare - (sum * sum)) / (count - 1);
			else
				return 0.0;
		}

		/** Return the last double value read from the source object.
			*  @return A double value collected at the last reading operation.*/
		public long getLastValue() {
			return lastRead;
		}

		/** The maximum function.
			*  @return The maximum value.*/
		public long getMax() {
			return max;
		}
		/** The sum function.
			*  @return The sum of collected values.*/
		public long getSum() {
			return sum;
		}		
		/** The minimum function.
			*  @return The minimum value.*/
		public long getMin() {
			return min;
		}
		/** The mean function.
			*  @return The mean value.*/
		public double getMean() {
			if (count > 0)
				return sum / count;
			else
				return 0.0;
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
	 * @author Michele Sonnessa
	 *
	 */
	public static class Double extends MultiTraceFunction {
		protected double max = java.lang.Double.MIN_VALUE;
		protected double min = java.lang.Double.MAX_VALUE;
		protected double sum = 0, sumSquare = 0;
		
		protected IDoubleSource target;
		private Enum<?> valueID;

		private double lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param name Name of the statistic object.
		 *  @param source The IDblSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Double(IDoubleSource source, Enum<?> valueID) {
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param name Name of the statistic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Object source, String valueName, boolean getFromMethod) {
			target = new DoubleInvoker(source, valueName, getFromMethod);
			valueID = IDoubleSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void updateSource() {
			super.updateSource();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getDoubleValue(valueID);

			if (lastRead < min)
				min = lastRead;
			if (lastRead > max)
				max = lastRead;
			sum += lastRead;
			sumSquare += (lastRead * lastRead);
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			if (valueID.equals( IIntSource.Variables.Default))
				return lastRead;
			switch ( (MultiTraceFunction.Variables) valueID) {
				case LastValue :	return lastRead;
				case Max : return max;
				case Min :	return min;
				case Sum: 	return sum;
				default :	return super.getDoubleValue(valueID);
			}
		}

		/** The variance function.
		 *  @return The variance value.*/
		public double getVariance() {
			if (count > 1)
				return (sumSquare - (sum * sum)) / (count - 1);
			else
				return 0.0;
		}

		/** Return the last double value read from the source object.
			*  @return A double value collected at the last reading operation.*/
		public double getLastValue() {
			return lastRead;
		}

		/** The maximum function.
			*  @return The maximum value.*/
		public double getMax() {
			return max;
		}
		/** The sum function.
			*  @return The sum of collected values.*/
		public double getSum() {
			return sum;
		}
		/** The minimum function.
			*  @return The minimum value.*/
		public double getMin() {
			return min;
		}
		/** The mean function.
			*  @return The mean value.*/
		public double getMean() {
			if (count > 0)
				return sum / count;
			else
				return 0.0;
		}

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
	public static class Integer
		extends MultiTraceFunction
		implements IIntSource {
		protected int max = java.lang.Integer.MIN_VALUE;
		protected int min = java.lang.Integer.MAX_VALUE;
		protected int sum = 0;
		protected long sumSquare = 0;
	
		protected IIntSource target;
		private Enum<?> valueID;

		private int lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param name Name of the statistic object.
		 *  @param source The IDblSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(IIntSource source, Enum<?> valueID) {
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param name Name of the statistic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(
			Object source,
			String valueName,
			boolean getFromMethod) {
			target = new IntegerInvoker(source, valueName, getFromMethod);
			valueID = IIntSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void updateSource() {
			super.updateSource();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getIntValue(valueID);

			if (lastRead < min)
				min = lastRead;
			if (lastRead > max)
				max = lastRead;
			sum += lastRead;
			sumSquare += (lastRead * lastRead);
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			switch ( (MultiTraceFunction.Variables) valueID) {
				case LastValue :			return (double) lastRead;
				case Max :				return (double) max;
				case Min :				return (double) min;
				case Sum:					return (double) sum;	
				default :
					return super.getDoubleValue(valueID);
			}
		}
		/** Return the result of a given statistic.
			*  @param valueID One of the F_ constants representing available statistics.
			*  @return The computed value.
			*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public int getIntValue(Enum<?> valueID) {
			if (valueID.equals( IIntSource.Variables.Default))
				return lastRead;
			switch ((MultiTraceFunction.Variables) valueID) {
				case LastValue :		return lastRead;
				case Max :			return max;
				case Min :			return min;
				case Count :		return count;
				default :
					throw new UnsupportedOperationException(
						valueID
							+ " is not a defined function for " + getClass() + ".");
			}
		}

		/** The variance function.
		 *  @return The variance value.*/
		public double getVariance() {
			if (count > 1)
				return (sumSquare - (sum * sum)) / (count - 1);
			else
				return 0.0;
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
		/** The sum function.
			*  @return The sum of collected values.*/
		public int getSum() {
			return sum;
		}		
		/** The minimum function.
			*  @return The minimum value.*/
		public int getMin() {
			return min;
		}
		/** The mean function.
			*  @return The mean value.*/
		public double getMean() {
			if (count > 0)
				return sum / count;
			else
				return 0.0;
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
	public static class Float
		extends MultiTraceFunction
		implements IFloatSource {
		protected float max = java.lang.Float.MIN_VALUE;
		protected float min = java.lang.Float.MAX_VALUE;
		protected float sum = 0.0f;
		protected double sumSquare = 0.0;
		
		protected IFloatSource target;
		private Enum<?> valueID;

		private float lastRead;

		/** Create a basic statistic probe on a IDblSource object.
		 *  @param name Name of the statistic object.
		 *  @param source The IDblSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Float(IFloatSource source, Enum<?> valueID) {
			target = source;
			this.valueID = valueID;
		}

		/** Create a basic statistic probe on a generic object.
		  *  @param name Name of the statistic object.
		  *  @param source A generic source object.
		  *  @param valueName The name of the field or the method returning the variable to be probed.
		  *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Float(Object source, String valueName, boolean getFromMethod) {
			target = new FloatInvoker(source, valueName, getFromMethod);
			valueID = IFloatSource.Variables.Default;
		}

		/** Read the source values and update statistics.*/
		public void updateSource() {
			super.updateSource();
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			lastRead = target.getFloatValue(valueID);

			if (lastRead < min)
				min = lastRead;
			if (lastRead > max)
				max = lastRead;
			sum += lastRead;
			sumSquare += (lastRead * lastRead);
		}

		/** Return the result of a given statistic.
				*  @param valueID One of the F_ constants representing available statistics.
				*  @return The computed value.
				*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public double getDoubleValue(Enum<?> valueID) {
			if (valueID.equals( IIntSource.Variables.Default))
				return lastRead;
			switch ((MultiTraceFunction.Variables) valueID) {
				case LastValue :			return (double) lastRead;
				case Max :				return (double) max;
				case Min :				return (double) min;
				case Sum:					return (double) sum;	
				default :
					return super.getDoubleValue(valueID);
			}
		}
		/** Return the result of a given statistic.
			*  @param valueID One of the F_ constants representing available statistics.
			*  @return The computed value.
			*  @throws UnsupportedOperationException If the given valueID is not supported.*/
		public float getFloatValue(Enum<?> valueID) {
			switch ((MultiTraceFunction.Variables) valueID) {
				case LastValue :		return lastRead;
				case Max :			return max;
				case Min :			return min;
				case Mean :			return (float) getMean();
				case Variance :	return (float) getVariance();
				case Sum:				return sum;	
				default :
					throw new UnsupportedOperationException(
						valueID
							+ " is not a defined function for " + getClass() + ".");
			}
		}

		/** The variance function.
		 *  @return The variance value.*/
		public double getVariance() {
			if (count > 1)
				return (sumSquare - (sum * sum)) / (count - 1);
			else
				return 0.0;
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
		/** The sum function.
			*  @return The sum of collected values.*/
		public float getSum() {
			return sum;
		}		
		/** The minimum function.
			*  @return The minimum value.*/
		public float getMin() {
			return min;
		}
		/** The mean function.
			*  @return The mean value.*/
		public double getMean() {
			if (count > 0)
				return sum / count;
			else
				return 0.0;
		}

	}
}