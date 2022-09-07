package microsim.statistics.weighted;
//package microsim.statistics.weighted;

import microsim.statistics.ICollectionFilter;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatSource;
import microsim.statistics.IIntSource;
import microsim.statistics.ILongSource;
import microsim.statistics.ISourceObjectArray;
import microsim.statistics.IUpdatableSource;
import microsim.statistics.TimeChecker;

import java.util.Collection;
import java.util.Iterator;

import microsim.agent.Weight;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.FloatInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

/**
 * A weighted cross section is a collection of values each of them representing the status of a given
 * variable of a weighted element of a collection of agents. 
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
public abstract class Weighted_CrossSection implements EventListener, IUpdatableSource, ISourceObjectArray
{
	protected Object[] sourceList;
	
	protected TimeChecker timeChecker = new TimeChecker();;
	
	protected ICollectionFilter filter = null;
	
	public abstract void updateSource();

	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new UnsupportedOperationException("The SimpleStatistics object does not support " + type + " operation.");
	}

	public Object[] getSourceArray() {	return sourceList; }
			
	public static class Double extends Weighted_CrossSection implements IWeightedDoubleArraySource
	{
		protected double[] valueList;
		protected double[] weights;
		
		protected DoubleInvoker invoker;
		protected Collection<? extends Weight> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IDoubleSource objects.
		 *  @param source The collection containing IDoubleSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Double(Collection<? extends Weight> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IDoubleSource objects. 
		 *  It uses the IDoubleSource.DEFAULT variable id.
		 *  @param source The collection containing IDoubleSource object.
		 */
		public Double(Collection<? extends Weight> source)
		{ 
			target = source;
			this.valueID = IDoubleSource.Variables.Default;
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = IDoubleSource.Variables.Default;
			invoker = new DoubleInvoker(objectClass, valueName, getFromMethod);
		}

		public double[] getDoubleArray() 
		{ 
			return valueList;	
		}
	
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i] + " ");
			}
			buf.append(weights[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new double[target.size()];
			sourceList = new Weight[valueList.length];
			weights = new double[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getDouble(obj);
							weights[i] = obj.getWeight(); 
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((IDoubleSource) obj).getDoubleValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				weights = cern.colt.Arrays.trimToCapacity(weights, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = invoker.getDouble(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = ((IDoubleSource) o).getDoubleValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
						
		}

		@Override
		public double[] getWeights() {
			return weights;
		}

	}
	

	
	public static class Integer extends Weighted_CrossSection implements IWeightedIntArraySource
	{
		protected int[] valueList;
		protected double[] weights;
		
		protected IntegerInvoker invoker;
		protected Collection<? extends Weight> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IIntSource objects.
		 *  @param source The collection containing IIntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(Collection<? extends Weight> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IIntSource objects. 
		 *  It uses the IIntSource.DEFAULT variable id.
		 *  @param source The collection containing IIntSource object.
		 */
		public Integer(Collection<? extends Weight> source)
		{ 
			target = source;
			this.valueID = IIntSource.Variables.Default;
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = IIntSource.Variables.Default;
			invoker = new IntegerInvoker(objectClass, valueName, getFromMethod);
		}

		public int[] getIntArray() 
		{ 
			return valueList;	
		}
	
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Integer [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i] + " ");
			}
			buf.append(weights[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new int[target.size()];
			sourceList = new Weight[valueList.length];
			weights = new double[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getInt(obj);
							weights[i] = obj.getWeight(); 
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((IIntSource) obj).getIntValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				weights = cern.colt.Arrays.trimToCapacity(weights, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = invoker.getInt(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = ((IIntSource) o).getIntValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
						
		}

		@Override
		public double[] getWeights() {
			return weights;
		}	

	}
	
	
	
	
	public static class Float extends Weighted_CrossSection implements IWeightedFloatArraySource
	{
		protected float[] valueList;
		protected double[] weights;
		
		protected FloatInvoker invoker;
		protected Collection<? extends Weight> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IFloatSource objects.
		 *  @param source The collection containing IFloatSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Float(Collection<? extends Weight> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IFloatSource objects. 
		 *  It uses the IFloatSource.DEFAULT variable id.
		 *  @param source The collection containing IFloatSource object.
		 */
		public Float(Collection<? extends Weight> source)
		{ 
			target = source;
			this.valueID = IFloatSource.Variables.Default;
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Float(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = IFloatSource.Variables.Default;
			invoker = new FloatInvoker(objectClass, valueName, getFromMethod);
		}

		public float[] getFloatArray() 
		{ 
			return valueList;	
		}
	
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Float [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i] + " ");
			}
			buf.append(weights[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new float[target.size()];
			sourceList = new Weight[valueList.length];
			weights = new double[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getFloat(obj);
							weights[i] = obj.getWeight(); 
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((IFloatSource) obj).getFloatValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				weights = cern.colt.Arrays.trimToCapacity(weights, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = invoker.getFloat(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = ((IFloatSource) o).getFloatValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
						
		}

		@Override
		public double[] getWeights() {
			return weights;
		}

	}

	
	
	public static class Long extends Weighted_CrossSection implements IWeightedLongArraySource
	{
		protected long[] valueList;
		protected double[] weights;
		
		protected LongInvoker invoker;
		protected Collection<? extends Weight> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of ILongSource objects.
		 *  @param source The collection containing ILongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(Collection<? extends Weight> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of ILongSource objects. 
		 *  It uses the ILongSource.DEFAULT variable id.
		 *  @param source The collection containing ILongSource object.
		 */
		public Long(Collection<? extends Weight> source)
		{ 
			target = source;
			this.valueID = ILongSource.Variables.Default;
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = ILongSource.Variables.Default;
			invoker = new LongInvoker(objectClass, valueName, getFromMethod);
		}

		public long[] getLongArray() 
		{ 
			return valueList;	
		}
	
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Long [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i] + " ");
			}
			buf.append(weights[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new long[target.size()];
			sourceList = new Weight[valueList.length];
			weights = new double[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getLong(obj);
							weights[i] = obj.getWeight(); 
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((ILongSource) obj).getLongValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				weights = cern.colt.Arrays.trimToCapacity(weights, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = invoker.getLong(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Iterator<? extends Weight> it = target.iterator(); it.hasNext(); )
					{
						Weight o = it.next();
						valueList[i] = ((ILongSource) o).getLongValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
						
		}

		@Override
		public double[] getWeights() {
			return weights;
		}

	}
		
	
	/**
	 * @return
	 */
	public ICollectionFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 */
	public void setFilter(ICollectionFilter filter) {
		this.filter = filter;
	}

	/** Return the current status of the time checker. A time checker avoid the object to update
	 * more than one time per simulation step. The default value is enabled (true). 
	 * @return True if the computer is currently checking time before update cached data, false if disabled.
	 */
	public boolean isCheckingTime() {
		return timeChecker.isEnabled();
	}

	/** Set the current status of the time checker. A time checker avoid the object to update
	 * more than one time per simulation step. The default value is enabled (true). 
	 * @param b True if the computer is currently checking time before update cached data, false if disabled.
	 */
	public void setCheckingTime(boolean b) {
		timeChecker.setEnabled(b);
	}

}