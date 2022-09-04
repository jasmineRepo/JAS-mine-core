package microsim.statistics;

import lombok.Getter;
import lombok.Setter;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

import java.util.Collection;

/**
 * A cross section is a collection of values each of them representing the status of a given
 * variable of an element of a collection of agents.
 */
public abstract class CrossSection implements EventListener, UpdatableSource, SourceObjectArray
{
	protected Object[] sourceList;

	protected TimeChecker timeChecker = new TimeChecker();;

	@Setter @Getter	protected CollectionFilter filter = null;

	public abstract void updateSource();

	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @param type The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new UnsupportedOperationException("The SimpleStatistics object does not support " + type + " operation.");
	}

	public Object[] getSourceArray() {	return sourceList; }

	public static class Double extends CrossSection implements DoubleArraySource
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
			this.valueID = DoubleSource.Variables.Default;
		}

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
		{
			target = source;
			this.valueID = DoubleSource.Variables.Default;
			invoker = new DoubleInvoker(objectClass, valueName, getFromMethod);
		}

		public double[] getDoubleArray()
		{
			return valueList;
		}

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("CrossSection.Double [");//duplicates again
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i]).append(" ");
			}
			buf.append(valueList[size]).append("]");
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
					for (Object obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = invoker.getDouble(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Object obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = ((DoubleSource) obj).getDoubleValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.mateba.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.mateba.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Object o : target) {
						valueList[i] = invoker.getDouble(o);
						sourceList[i++] = o;
					}
				else
					for (Object o : target) {
						valueList[i] = ((DoubleSource) o).getDoubleValue(valueID);
						sourceList[i++] = o;
					}

		}

	}

	public static class Long extends CrossSection implements LongArraySource
	{
		protected long[] valueList;

		protected LongInvoker invoker;
		protected Collection<?> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of LongSource objects.
		 *  @param source The collection containing LongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(Collection<?> source, Enum<?> valueID)
		{
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of LongSource objects.
		 * It uses the LongSource.DEFAULT variable id.
		 *  @param source The collection containing LongSource object.
		 */
		public Long(Collection<?> source)
		{
			target = source;
			this.valueID = LongSource.Variables.Default;
		}

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
		{
			target = source;
			this.valueID = LongSource.Variables.Default;
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
			StringBuilder buf = new StringBuilder();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i]).append(" ");
			}
			buf.append(valueList[size]).append("]");
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
					for (Object obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = invoker.getLong(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Object obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = ((LongSource) obj).getLongValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.mateba.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.mateba.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Object o : target) {
						valueList[i] = invoker.getLong(o);
						sourceList[i++] = o;
					}
				else
					for (Object o : target) {
						valueList[i] = ((LongSource) o).getLongValue(valueID);
						sourceList[i++] = o;
					}

		}
	}

	public static class Integer extends CrossSection implements IntArraySource
	{
		protected int[] valueList;

		protected IntegerInvoker invoker;
		protected Collection<?> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IntSource objects.
		 *  @param source The collection containing IntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(Collection<?> source, Enum<?> valueID)
		{
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IntSource objects.
		 *  It uses the IntSource.DEFAULT variable id.
		 *  @param source The collection containing IntSource object.
		 */
		public Integer(Collection<?> source)
		{
			target = source;
			this.valueID = IntSource.Variables.Default;
		}

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Collection<?> source, Class<?> objectClass,	String valueName, boolean getFromMethod)
		{
			target = source;
			this.valueID = IntSource.Variables.Default;
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
				list[i] = valueList[i];

			return list;
		}

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i]).append(" ");
			}
			buf.append(valueList[size]).append("]");
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
					for (Object obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = invoker.getInt(obj);
							sourceList[i++] = obj;
						}
					}
				else
					for (Object obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = ((IntSource) obj).getIntValue(valueID);
							sourceList[i++] = obj;
						}
					}
				valueList = cern.mateba.Arrays.trimToCapacity(valueList, i);
				sourceList = cern.mateba.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Object o : target) {
						valueList[i] = invoker.getInt(o);
						sourceList[i++] = o;
					}
				else
					for (Object o : target) {
						valueList[i] = ((IntSource) o).getIntValue(valueID);
						sourceList[i++] = o;
					}

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
