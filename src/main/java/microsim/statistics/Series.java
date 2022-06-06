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

import java.io.Serial;
import java.util.Arrays;

/**
 * A series is a sequential collection of values coming from a given variable source over time.
 */
public abstract class Series implements EventListener, UpdatableSource
{
	protected TimeChecker timeChecker = new TimeChecker();
			
	public abstract void updateSource();

	/**
	 * SimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @param type The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new UnsupportedOperationException("The SimpleStatistics object does not support " + type + " operation.");
		
	}
	
	private static class BufferedDoubleArrayList extends DoubleArrayList
	{
		@Serial private static final long serialVersionUID = 1L;

		public void add(double element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}
	
	private static class BufferedFloatArrayList extends FloatArrayList
	{
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		@Serial private static final long serialVersionUID = 1L;

		public void add(float element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}
	
	private static class BufferedIntArrayList extends IntArrayList
	{
		@Serial private static final long serialVersionUID = 1L;

		public void add(int element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}
	
	private static class BufferedLongArrayList extends LongArrayList
	{
		@Serial private static final long serialVersionUID = 1L;

		public void add(long element)
		{
			if (size == elements.length) ensureCapacity(size + 50); 
			elements[size++] = element;			
		}
	}		
	
	public static class Double extends Series implements DoubleArraySource
	{
		protected BufferedDoubleArrayList valueList;
	
		protected DoubleSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IDoubleSource objects.
		 *  @param source The collection containing IDoubleSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Double(DoubleSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedDoubleArrayList();
		}

		/** Create a statistic probe on a collection of IDoubleSource objects.
		 * It uses the IDoubleSource.DEFAULT variable id.
		 *  @param source The collection containing IDoubleSource object.
     */
		public Double(DoubleSource source)
		{ 
			target = source;
			this.valueID = DoubleSource.Variables.Default;
			valueList = new BufferedDoubleArrayList();
		}		

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = DoubleSource.Variables.Default;
			target = new DoubleInvoker(source, valueName, getFromMethod);
			valueList = new BufferedDoubleArrayList();
		}

		public double[] getDoubleArray() 
		{
			return cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
		}
	
		public DoubleArrayList getDoubleArrayList(){ return valueList; }
		

		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof UpdatableSource)
				((UpdatableSource) target).updateSource();
			valueList.add( target.getDoubleValue(valueID) );
		}
		
		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (var i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i)).append(" ");
			}
			buf.append(valueList.getQuick(size)).append("]\n");
			return buf.toString();
		}
	}

	public static class Long extends Series implements LongArraySource
	{
		protected BufferedLongArrayList valueList;

		protected LongSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of LongSource objects.
		 *  @param source The collection containing LongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(LongSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedLongArrayList();
		}

		/** Create a statistic probe on a collection of LongSource objects.
		 * It uses the LongSource variable id.
		 *  @param source The collection containing LongSource object.
		*/
		public Long(LongSource source)
		{ 
			target = source;
			this.valueID = LongSource.Variables.Default;
			valueList = new BufferedLongArrayList();
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = LongSource.Variables.Default;
			target = new LongInvoker(source, valueName, getFromMethod);
			valueList = new BufferedLongArrayList();
		}
		
		public long[] getLongArray() 
		{
			return cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
		}
	
		public double[] getDoubleArray() // fixme make all functions consistent
		{ 
			long[] elements = valueList.elements();
			double[] list = new double[valueList.size()];
			Arrays.setAll(list, i -> elements[i]);

			return list;	
		}
			
		public LongArrayList getLongArrayList(){ return valueList; }

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (var i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i)).append(" ");
			}
			buf.append(valueList.getQuick(size)).append("]");
			return buf.toString();
		}

		public void updateSource() 
		{
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof UpdatableSource)
				((UpdatableSource) target).updateSource();
			valueList.add( target.getLongValue(valueID) );
		}
	}
	
	public static class Integer extends Series implements IntArraySource
	{
		protected BufferedIntArrayList valueList;
	
		protected IntSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IntSource objects.
		 *  @param source The collection containing IntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(IntSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedIntArrayList();
		}

		/** Create a statistic probe on a collection of IntSource objects.
		 * It uses the IntSource variable id.
		 *  @param source The collection containing IntSource object.
		*/
		public Integer(IntSource source)
		{ 
			target = source;
			this.valueID = IntSource.Variables.Default;
			valueList = new BufferedIntArrayList();
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = IntSource.Variables.Default;
			target = new IntegerInvoker(source, valueName, getFromMethod);
			valueList = new BufferedIntArrayList();
		}

		public int[] getIntArray() 
		{
			return cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
		}
		
		public double[] getDoubleArray() 
		{ 
			int[] elements = valueList.elements();
			double[] list = new double[valueList.size()];
			Arrays.setAll(list, i -> elements[i]);

			return list;	
		}	
	
		public IntArrayList getIntArrayList(){ return valueList; }
		
		public String toString()
		{
			StringBuilder buf = new StringBuilder();// fixme duplicates, tons of them
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i)).append(" ");
			}
			buf.append(valueList.getQuick(size)).append("]\n");
			return buf.toString();
		}
	
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof UpdatableSource)
				((UpdatableSource) target).updateSource();
			valueList.add( target.getIntValue(valueID) );
		}
	}
	
	
	public static class Float extends Series implements FloatArraySource
	{
		protected BufferedFloatArrayList valueList;
	
		protected FloatSource target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of FloatSource objects.
		 *  @param source The collection containing FloatSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Float(FloatSource source, Enum<?> valueID)
		{ 
			target = source;
			this.valueID = valueID;
			valueList = new BufferedFloatArrayList();
		}

		/** Create a statistic probe on a collection of FloatSource objects.
		 * It uses the FloatSource variable id.
		 *  @param source The collection containing FloatSource object.
	 	*/
		public Float(FloatSource source)
		{ 
			target = source;
			this.valueID = FloatSource.Variables.Default;
			valueList = new BufferedFloatArrayList();
		}		
		
		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Float(Object source, String valueName, boolean getFromMethod)
		{ 
			this.valueID = FloatSource.Variables.Default;
			target = new FloatInvoker(source, valueName, getFromMethod);
			valueList = new BufferedFloatArrayList();
		}
			
		public float[] getFloatArray() 
		{
			return cern.colt.Arrays.trimToCapacity(valueList.elements(), valueList.size());
		}
	
		public double[] getDoubleArray() 
		{ 
			float[] elements = valueList.elements();
			double[] list = new double[valueList.size()];
			Arrays.setAll(list, i -> elements[i]);

			return list;	
		}	
			
		public FloatArrayList getFloatArrayList(){ return valueList; }
		
		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("Series.Double [");
			int size = valueList.size() - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList.getQuick(i)).append(" ");
			}
			buf.append(valueList.getQuick(size)).append("]\n");
			return buf.toString();
		}
		
		public void updateSource() {
			if (timeChecker.isUpToDate())
				return;
			if (target instanceof UpdatableSource)
				((UpdatableSource) target).updateSource();
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
