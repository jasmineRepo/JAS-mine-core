package microsim.statistics;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.FloatInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.FloatArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.list.LongArrayList;

/**
 * A series is a sequential collection of values coming from a given variable source over time.
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
 * @author Michele Sonnessa, Ross Richardson
 *
 */
public abstract class Series implements EventListener, IUpdatableSource
{
	protected TimeChecker timeChecker = new TimeChecker();
			
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
	
	private class BufferedDoubleArrayList extends DoubleArrayList
	{
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public void add(double element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}
	
	private class BufferedFloatArrayList extends FloatArrayList
	{
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public void add(float element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}
	
	private class BufferedIntArrayList extends IntArrayList
	{
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public void add(int element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}
	
	private class BufferedLongArrayList extends LongArrayList
	{
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public void add(long element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}		
	
	public static class Double extends Series implements IDoubleArraySource
	{
		protected BufferedDoubleArrayList valueList;
	
		protected IDoubleSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IDoubleSource objects.
		 *  @param source The collection containing IDoubleSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Double(IDoubleSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedDoubleArrayList();
		}

		/** Create a statistic probe on a collection of IDoubleSource objects.
		 * It uses the IDoubleSource.DEFAULT variable id.
		 *  @param source The collection containing IDoubleSource object.
     */
		public Double(IDoubleSource source)
		{ 
			target = source;
			this.valueID = IDoubleSource.Variables.Default;
			valueList = new BufferedDoubleArrayList();
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = IDoubleSource.Variables.Default;
			target = new DoubleInvoker(source, valueName, getFromMethod);
			valueList = new BufferedDoubleArrayList();
		}

		public double[] getDoubleArray() 
		{ 
			double[] elements = cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
			return elements;	
		}
	
		public DoubleArrayList getDoubleArrayList(){ return valueList; }
		

		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();				
			valueList.add( target.getDoubleValue(valueID) );
		}
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i) + " ");
			}
			buf.append(valueList.getQuick(size) + "]\n");
			return buf.toString();
		}
	}

	public static class Long extends Series implements ILongArraySource
	{
		protected BufferedLongArrayList valueList;

		protected ILongSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of ILongSource objects.
		 *  @param name Name of the statistic object.
		 *  @param source The collection containing ILongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(ILongSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedLongArrayList();
		}

		/** Create a statistic probe on a collection of ILongSource objects.
		 * It uses the ILongSource variable id.
		 *  @param source The collection containing ILongSource object.
		*/
		public Long(ILongSource source)
		{ 
			target = source;
			this.valueID = ILongSource.Variables.Default;
			valueList = new BufferedLongArrayList();
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = ILongSource.Variables.Default;
			target = new LongInvoker(source, valueName, getFromMethod);
			valueList = new BufferedLongArrayList();
		}
		
		public long[] getLongArray() 
		{ 
			long[] elements = cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
			return elements;	
		}
	
		public double[] getDoubleArray() 
		{ 
			long[] elements = valueList.elements();
			double[] list = new double[valueList.size()];
			for (int i = 0; i < list.length; i++)
				list[i] = elements[i];

			return list;	
		}
			
		public LongArrayList getLongArrayList(){ return valueList; }

		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i) + " ");
			}
			buf.append(valueList.getQuick(size) + "]");
			return buf.toString();
		}

		public void updateSource() 
		{
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			valueList.add( target.getLongValue(valueID) );
		}
	}
	
	public static class Integer extends Series implements IIntArraySource
	{
		protected BufferedIntArrayList valueList;
	
		protected IIntSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IIntSource objects.
		 *  @param name Name of the statistic object.
		 *  @param source The collection containing IIntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(IIntSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedIntArrayList();
		}

		/** Create a statistic probe on a collection of IIntSource objects.
		 * It uses the IIntSource variable id.
		 *  @param source The collection containing IIntSource object.
		*/
		public Integer(IIntSource source)
		{ 
			target = source;
			this.valueID = IIntSource.Variables.Default;
			valueList = new BufferedIntArrayList();
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = IIntSource.Variables.Default;
			target = new IntegerInvoker(source, valueName, getFromMethod);
			valueList = new BufferedIntArrayList();
		}

		public int[] getIntArray() 
		{ 
			int[] elements = cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
			return elements;	
		}
		
		public double[] getDoubleArray() 
		{ 
			int[] elements = valueList.elements();
			double[] list = new double[valueList.size()];
			for (int i = 0; i < list.length; i++)
				list[i] = elements[i];

			return list;	
		}	
	
		public IntArrayList getIntArrayList(){ return valueList; }
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i) + " ");
			}
			buf.append(valueList.getQuick(size) + "]\n");
			return buf.toString();
		}
	
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			valueList.add( target.getIntValue(valueID) );
		}
	}
	
	
	public static class Float extends Series implements IFloatArraySource
	{
		protected BufferedFloatArrayList valueList;
	
		protected IFloatSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IFloatSource objects.
		 *  @param name Name of the statistic object.
		 *  @param source The collection containing IFloatSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Float(IFloatSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedFloatArrayList();
		}

		/** Create a statistic probe on a collection of IFloatSource objects.
		 * It uses the IFloatSource variable id.
		 *  @param source The collection containing IFloatSource object.
	 	*/
		public Float(IFloatSource source)
		{ 
			target = source;
			this.valueID = IFloatSource.Variables.Default;
			valueList = new BufferedFloatArrayList();
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param name Name of the statistic object.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Float(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = IFloatSource.Variables.Default;
			target = new FloatInvoker(source, valueName, getFromMethod);
			valueList = new BufferedFloatArrayList();
		}
			
		public float[] getFloatArray() 
		{ 
			float[] elements = cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
			return elements;	
		}
	
		public double[] getDoubleArray() 
		{ 
			float[] elements = valueList.elements();
			double[] list = new double[valueList.size()];
			for (int i = 0; i < list.length; i++)
				list[i] = elements[i];

			return list;	
		}	
			
		public FloatArrayList getFloatArrayList(){ return valueList; }
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i) + " ");
			}
			buf.append(valueList.getQuick(size) + "]\n");
			return buf.toString();
		}
		
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof IUpdatableSource)
				((IUpdatableSource) target).updateSource();
			valueList.add( target.getFloatValue(valueID) );
		}
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
