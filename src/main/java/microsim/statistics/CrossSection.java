package microsim.statistics;

import java.util.Collection;
import java.util.Iterator;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.FloatInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

/**
 * A cross section is a collection of values each of them representing the status of a given
 * variable of an element of a collection of agents. 
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
public abstract class CrossSection implements EventListener, IUpdatableSource, ISourceObjectArray
{
	protected Object[] sourceList;
	
	protected TimeChecker timeChecker = new TimeChecker();;
	
	protected ICollectionFilter filter = null;
	
	public abstract void updateSource();

	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @param actionType The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new UnsupportedOperationException("The SimpleStatistics object does not support " + type + " operation.");
	}

	public Object[] getSourceArray() {	return sourceList; }
			
	public static class Double extends CrossSection implements IDoubleArraySource
	{
		protected double[] valueList;
	
		protected DoubleInvoker invoker;
		protected Collection<?> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IDoubleSource objects.
		 *  @param source The collection containing IDoubleSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Double(Collection<?> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IDoubleSource objects. 
		 *  It uses the IDoubleSource.DEFAULT variable id.
		 *  @param source The collection containing IDoubleSource object.
		 */
		public Double(Collection<?> source)
		{ 
			target = source;
			this.valueID = IDoubleSource.Variables.Default;
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
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
			buf.append(valueList[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new double[target.size()];
			sourceList = new Object[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getDouble(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((IDoubleSource) obj).getDoubleValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = invoker.getDouble(o);
						sourceList[i++] = o;
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = ((IDoubleSource) o).getDoubleValue(valueID);
						sourceList[i++] = o;
					}
						
		}

	}
	

	public static class Long extends CrossSection implements ILongArraySource
	{
		protected long[] valueList;
	
		protected LongInvoker invoker;
		protected Collection<?> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of ILongSource objects.
		 *  @param source The collection containing ILongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(Collection<?> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of ILongSource objects.
		 * It uses the ILongSource.DEFAULT variable id.
		 *  @param source The collection containing ILongSource object.
		 */
		public Long(Collection<?> source)
		{ 
			target = source;
			this.valueID = ILongSource.Variables.Default;
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = ILongSource.Variables.Default;
			invoker = new LongInvoker(objectClass, valueName, getFromMethod);
		}
		
		public long[] getLongArray() 
		{ 
			return valueList;	
		}
	
		
		public double[] getDoubleArray()
		{ 
			double[] list = new double[valueList.length];
			for (int i = 0; i < valueList.length; i++)
				list[i] = (double) valueList[i];

			return list; 
		}
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new long[target.size()];
			sourceList = new Object[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getLong(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((ILongSource) obj).getLongValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = invoker.getLong(o);
						sourceList[i++] = o;
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = ((ILongSource) o).getLongValue(valueID);
						sourceList[i++] = o;
					}
						
		}
	}
	
	public static class Integer extends CrossSection implements IIntArraySource
	{
		protected int[] valueList;
	
		protected IntegerInvoker invoker;
		protected Collection<?> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IIntSource objects.
		 *  @param source The collection containing IIntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(Collection<?> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IIntSource objects. 
		 *  It uses the IIntSource.DEFAULT variable id.
		 *  @param source The collection containing IIntSource object.
		 */
		public Integer(Collection<?> source)
		{ 
			target = source;
			this.valueID = IIntSource.Variables.Default;
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = IIntSource.Variables.Default;
			invoker = new IntegerInvoker(objectClass, valueName, getFromMethod);
		}
		
		public int[] getIntArray() 
		{ 
			return valueList;
		}
	
		public double[] getDoubleArray()
		{ 
			double[] list = new double[valueList.length];
			for (int i = 0; i < valueList.length; i++)
				list[i] = (double) valueList[i];

			return list; 
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]");
			return buf.toString();
		}
		
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new int[target.size()];
			sourceList = new Object[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getInt(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((IIntSource) obj).getIntValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = invoker.getInt(o);
						sourceList[i++] = o;
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = ((IIntSource) o).getIntValue(valueID);
						sourceList[i++] = o;
					}
						
		}
	}
	
	public static class Float extends CrossSection implements IFloatArraySource
	{
		protected float[] valueList;
	
		protected FloatInvoker invoker;
		protected Collection<?> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IFloatSource objects.
		 *  @param source The collection containing IFloatSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Float(Collection<?> source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
		}
		
		/** Create a statistic probe on a collection of IFloatSource objects. 
		 *  It uses the IFloatSource.DEFAULT variable id.
		 *  @param source The collection containing IFloatSource object.
		 */
		public Float(Collection<?> source)
		{ 
			target = source;
			this.valueID = IFloatSource.Variables.Default;
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Float(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
		{ 
			target = source;
			this.valueID = IFloatSource.Variables.Default;
			invoker = new FloatInvoker(objectClass, valueName, getFromMethod);
		}
			
		public float[] getFloatArray() 
		{ 
			return valueList;
		}
	
		public double[] getDoubleArray()
		{ 
			double[] list = new double[valueList.length];
			for (int i = 0; i < valueList.length; i++)
				list[i] = (double) valueList[i];

			return list; 
		}
			
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i] + " ");
			}
			buf.append(valueList[size] + "]");
			return buf.toString();
		}
			
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
				
			valueList = new float[target.size()];
			sourceList = new Object[valueList.length];
			
			int i = 0;
			if (filter != null)
			{
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] = invoker.getFloat(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object obj = it.next();
						if (filter.isFiltered(obj))
						{
							valueList[i] =((IFloatSource) obj).getFloatValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.colt.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.colt.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = invoker.getFloat(o);
						sourceList[i++] = o;
					}
				else
					for (Iterator<?> it = target.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						valueList[i] = ((IFloatSource) o).getFloatValue(valueID);
						sourceList[i++] = o;
					}
						
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