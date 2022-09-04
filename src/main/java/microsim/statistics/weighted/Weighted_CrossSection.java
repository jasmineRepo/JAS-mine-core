package microsim.statistics.weighted;

import lombok.Getter;
import lombok.Setter;
import microsim.agent.Weight;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.*;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

import java.util.Collection;

/**
 * A weighted cross section is a collection of values each of them representing the status of a given
 * variable of a weighted element of a collection of agents.
 */
public abstract class Weighted_CrossSection implements EventListener, UpdatableSource, SourceObjectArray //fixme rename
{
	protected Object[] sourceList;

	protected TimeChecker timeChecker = new TimeChecker();

	@Setter	@Getter protected CollectionFilter filter = null;

	public abstract void updateSource();

	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @param type The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update)) updateSource();
		else throw new UnsupportedOperationException("The SimpleStatistics object does not support " + type +
				" operation.");
	}

	public Object[] getSourceArray() {	return sourceList; }

	public static class Double extends Weighted_CrossSection implements WeightedDoubleArraySource {
		@Getter protected double[] valueList;
		@Getter protected double[] weights;

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
			this.valueID = DoubleSource.Variables.Default;
		}

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Double(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{
			target = source;
			this.valueID = DoubleSource.Variables.Default;
			invoker = new DoubleInvoker(objectClass, valueName, getFromMethod);
		}

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("CrossSection.Double [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i]).append(" ");
			}
			buf.append(valueList[size]).append("]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i]).append(" ");
			}
			buf.append(weights[size]).append("]");
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
					for (Weight obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = invoker.getDouble(obj);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				else
					for (Weight obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = ((DoubleSource) obj).getDoubleValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.mateba.Arrays.trimToCapacity(valueList, i);
				weights = cern.mateba.Arrays.trimToCapacity(weights, i);
				sourceList = cern.mateba.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Weight o : target) {
						valueList[i] = invoker.getDouble(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Weight o : target) {
						valueList[i] = ((DoubleSource) o).getDoubleValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
		}

		public double[] getDoubleArray() {
			return valueList;
		}
	}



	public static class Integer extends Weighted_CrossSection implements WeightedIntArraySource
	{
		protected int[] valueList;
		protected double[] weights;

		protected IntegerInvoker invoker;
		protected Collection<? extends Weight> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of IntSource objects.
		 *  @param source The collection containing IntSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Integer(Collection<? extends Weight> source, Enum<?> valueID)
		{
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of IntSource objects.
		 *  It uses the IntSource.DEFAULT variable id.
		 *  @param source The collection containing IntSource object.
		 */
		public Integer(Collection<? extends Weight> source)
		{
			target = source;
			this.valueID = IntSource.Variables.Default;
		}

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Integer(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{
			target = source;
			this.valueID = IntSource.Variables.Default;
			invoker = new IntegerInvoker(objectClass, valueName, getFromMethod);
		}

		public int[] getIntArray()
		{
			return valueList;
		}

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("CrossSection.Integer [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i]).append(" ");
			}
			buf.append(valueList[size]).append("]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i]).append(" ");
			}
			buf.append(weights[size]).append("]");
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
					for (Weight obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = invoker.getInt(obj);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				else
					for (Weight obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = ((IntSource) obj).getIntValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.mateba.Arrays.trimToCapacity(valueList, i);
				weights = cern.mateba.Arrays.trimToCapacity(weights, i);
				sourceList = cern.mateba.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Weight o : target) {
						valueList[i] = invoker.getInt(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Weight o : target) {
						valueList[i] = ((IntSource) o).getIntValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}

		}

		@Override
		public double[] getWeights() {
			return weights;
		}

	}

	public static class Long extends Weighted_CrossSection implements WeightedLongArraySource
	{
		protected long[] valueList;
		protected double[] weights;

		protected LongInvoker invoker;
		protected Collection<? extends Weight> target;
		protected Enum<?> valueID;

		/** Create a statistic probe on a collection of LongSource objects.
		 *  @param source The collection containing LongSource object.
		 *  @param valueID The value identifier defined by source object. */
		public Long(Collection<? extends Weight> source, Enum<?> valueID)
		{
			target = source;
			this.valueID = valueID;
		}

		/** Create a statistic probe on a collection of LongSource objects.
		 *  It uses the LongSource.DEFAULT variable id.
		 *  @param source The collection containing LongSource object.
		 */
		public Long(Collection<? extends Weight> source)
		{
			target = source;
			this.valueID = LongSource.Variables.Default;
		}

		/** Create a basic statistic probe on a collection of objects.
		 *  @param source A collection of generic objects.
		 *  @param objectClass The class of the objects contained by collection source.
		 *  @param valueName The name of the field or the method returning the variable to be probed.
		 *  @param getFromMethod Specifies if valueName is a method or a property value. */
		public Long(Collection<? extends Weight> source, Class<? extends Weight> objectClass,	String valueName, boolean getFromMethod)
		{
			target = source;
			this.valueID = LongSource.Variables.Default;
			invoker = new LongInvoker(objectClass, valueName, getFromMethod);
		}

		public long[] getLongArray()
		{
			return valueList;
		}

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("CrossSection.Long [");
			int size = valueList.length - 1;
			for (int i = 0; i < size; i++) {
				buf.append(valueList[i]).append(" ");
			}
			buf.append(valueList[size]).append("]; weights [");
			for (int i = 0; i < size; i++) {
				buf.append(weights[i]).append(" ");
			}
			buf.append(weights[size]).append("]");
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
					for (Weight obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = invoker.getLong(obj);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				else
					for (Weight obj : target) {
						if (filter.isFiltered(obj)) {
							valueList[i] = ((LongSource) obj).getLongValue(valueID);
							weights[i] = obj.getWeight();
							sourceList[i++] = obj;
						}
					}
				valueList = cern.mateba.Arrays.trimToCapacity(valueList, i);
				weights = cern.mateba.Arrays.trimToCapacity(weights, i);
				sourceList = cern.mateba.Arrays.trimToCapacity(sourceList, i);
			}
			else
				if (invoker != null)
					for (Weight o : target) {
						valueList[i] = invoker.getLong(o);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}
				else
					for (Weight o : target) {
						valueList[i] = ((LongSource) o).getLongValue(valueID);
						weights[i] = o.getWeight();
						sourceList[i++] = o;
					}

		}

		@Override
		public double[] getWeights() {
			return weights;
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
