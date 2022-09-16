package microsim.statistics;

import cern.mateba.list.tdouble.DoubleArrayList;
import cern.mateba.list.tint.IntArrayList;
import cern.mateba.list.tlong.LongArrayList;
import lombok.NonNull;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

import java.io.Serial;
import java.util.stream.IntStream;

/**
 * A series is a sequential collection of values coming from a given variable source over time.
 */
public abstract class Series implements EventListener, UpdatableSource {
    protected TimeChecker timeChecker = new TimeChecker();

    public abstract void updateSource();

    /**
     * {@link EventListener} callback function. It supports only {@link CommonEventType#Update} event.
     *
     * @param type The action id. Only {@link CommonEventType#Update} is supported.
     * @throws UnsupportedOperationException If actionType is not supported.
     */
    public void onEvent(final @NonNull Enum<?> type) {
        if (type.equals(CommonEventType.Update)) updateSource();
        else throw new UnsupportedOperationException("The SimpleStatistics object does not support " + type +
            " operation.");

    }

    /**
     * Return the current status of the time checker. A time checker avoid the object to update more than one time per
     * simulation step. The default value is enabled (true).
     *
     * @return True if the computer is currently checking time before update cached data, false if disabled.
     */
    public boolean isCheckingTime() {
        return timeChecker.isEnabled();
    }

    /**
     * Set the current status of the time checker. A time checker avoid the object to update more than one time per
     * simulation step. The default value is enabled (true).
     *
     * @param b True if the computer is currently checking time before update cached data, false if disabled.
     */
    public void setCheckingTime(final boolean b) {
        timeChecker.setEnabled(b);
    }

    private static class BufferedDoubleArrayList extends DoubleArrayList {
        @Serial
        private static final long serialVersionUID = -5282364300130897480L;

        public void add(final double element) {
            if (size == elements.length) ensureCapacity(size + 50);
            elements[size++] = element;
        }
    }

    private static class BufferedIntArrayList extends IntArrayList {
        @Serial
        private static final long serialVersionUID = -378494696495647634L;

        public void add(final int element) {
            if (size == elements.length) ensureCapacity(size + 50);
            elements[size++] = element;
        }
    }

    private static class BufferedLongArrayList extends LongArrayList {
        @Serial
        private static final long serialVersionUID = 2437067294390409378L;

        public void add(final long element) {
            if (size == elements.length) ensureCapacity(size + 50);
            elements[size++] = element;
        }
    }

    public static class Double extends Series implements DoubleArraySource {
        protected BufferedDoubleArrayList valueList;

        protected DoubleSource target;
        protected Enum<?> valueID;

        /**
         * Create a statistic probe on a collection of {@link DoubleSource} objects.
         *
         * @param source  The collection containing {@link DoubleSource} object.
         * @param valueID The value identifier defined by source object.
         */
        public Double(final @NonNull DoubleSource source, final @NonNull Enum<?> valueID) {
            target = source;
            this.valueID = valueID;
            valueList = new BufferedDoubleArrayList();
        }

        /**
         * Create a statistic probe on a collection of {@link DoubleSource} objects.
         * It uses the {@link DoubleSource.Variables#Default} variable id.
         *
         * @param source The collection containing {@link DoubleSource} object.
         */
        public Double(DoubleSource source) {
            target = source;
            this.valueID = DoubleSource.Variables.Default;
            valueList = new BufferedDoubleArrayList();
        }

        /**
         * Create a basic statistic probe on a collection of objects.
         *
         * @param source        A collection of generic objects.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Double(final @NonNull Object source, final @NonNull String valueName, final boolean getFromMethod) {
            this.valueID = DoubleSource.Variables.Default;
            target = new DoubleInvoker(source, valueName, getFromMethod);
            valueList = new BufferedDoubleArrayList();
        }

        public double @NonNull [] getDoubleArray() {
            return cern.mateba.Arrays.trimToCapacity(valueList.elements(), valueList.size());
        }

        public @NonNull DoubleArrayList getDoubleArrayList() {
            return valueList;
        }

        public void updateSource() {
            if (timeChecker.isUpToDate()) return;
            if (target instanceof UpdatableSource) ((UpdatableSource) target).updateSource();
            valueList.add(target.getDoubleValue(valueID));
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Series.Double [");
            int size = valueList.size() - 1;
            IntStream.range(0, size).forEachOrdered(i -> buf.append(valueList.getQuick(i)).append(" "));
            buf.append(valueList.getQuick(size)).append("]\n");
            return buf.toString();
        }
    }

    public static class Long extends Series implements LongArraySource {
        protected BufferedLongArrayList valueList;

        protected LongSource target;
        protected Enum<?> valueID;

        /**
         * Create a statistic probe on a collection of {@link LongSource} objects.
         *
         * @param source  The collection containing {@link LongSource} object.
         * @param valueID The value identifier defined by source object.
         */
        public Long(final @NonNull LongSource source, final @NonNull Enum<?> valueID) {
            target = source;
            this.valueID = valueID;
            valueList = new BufferedLongArrayList();
        }

        /**
         * Create a statistic probe on a collection of {@link LongSource} objects.
         * It uses the {@link LongSource.Variables#Default} variable id.
         *
         * @param source The collection containing {@link LongSource} object.
         */
        public Long(final @NonNull LongSource source) {
            target = source;
            this.valueID = LongSource.Variables.Default;
            valueList = new BufferedLongArrayList();
        }

        /**
         * Create a basic statistic probe on a collection of objects.
         *
         * @param source        A collection of generic objects.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Long(final @NonNull Object source, final @NonNull String valueName, final boolean getFromMethod) {
            this.valueID = LongSource.Variables.Default;
            target = new LongInvoker(source, valueName, getFromMethod);
            valueList = new BufferedLongArrayList();
        }

        public long @NonNull [] getLongArray() {
            return cern.mateba.Arrays.trimToCapacity(valueList.elements(), valueList.size());
        }

        public double @NonNull [] getDoubleArray() {
            return IntStream.range(0, valueList.size()).mapToDouble(i -> (double) valueList.elements()[i]).toArray();
        }

        public @NonNull LongArrayList getLongArrayList() {
            return valueList;
        }

        public @NonNull String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Series.Long [");
            int size = valueList.size() - 1;
            IntStream.range(0, size).forEachOrdered(i -> buf.append(valueList.getQuick(i)).append(" "));
            buf.append(valueList.getQuick(size)).append("]");
            return buf.toString();
        }

        public void updateSource() {
            if (timeChecker.isUpToDate()) return;
            if (target instanceof UpdatableSource) ((UpdatableSource) target).updateSource();
            valueList.add(target.getLongValue(valueID));
        }
    }

    public static class Integer extends Series implements IntArraySource {
        protected BufferedIntArrayList valueList;

        protected IntSource target;
        protected Enum<?> valueID;

        /**
         * Create a statistic probe on a collection of {@link IntSource} objects.
         *
         * @param source  The collection containing {@link IntSource} object.
         * @param valueID The value identifier defined by source object.
         */
        public Integer(final @NonNull IntSource source, final @NonNull Enum<?> valueID) {
            target = source;
            this.valueID = valueID;
            valueList = new BufferedIntArrayList();
        }

        /**
         * Create a statistic probe on a collection of {@link IntSource} objects.
         * It uses the {@link IntSource} variable id.
         *
         * @param source The collection containing {@link IntSource} object.
         */
        public Integer(final @NonNull IntSource source) {
            target = source;
            this.valueID = IntSource.Variables.Default;
            valueList = new BufferedIntArrayList();
        }

        /**
         * Create a basic statistic probe on a collection of objects.
         *
         * @param source        A collection of generic objects.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Integer(final @NonNull Object source, final @NonNull String valueName, final boolean getFromMethod) {
            this.valueID = IntSource.Variables.Default;
            target = new IntegerInvoker(source, valueName, getFromMethod);
            valueList = new BufferedIntArrayList();
        }

        public int @NonNull [] getIntArray() {
            return cern.mateba.Arrays.trimToCapacity(valueList.elements(), valueList.size());
        }

        public double @NonNull [] getDoubleArray() {
            return IntStream.range(0, valueList.size()).mapToDouble(i -> (double) valueList.elements()[i]).toArray();
        }

        public @NonNull IntArrayList getIntArrayList() {
            return valueList;
        }

        public @NonNull String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Series.Integer [");
            int size = valueList.size() - 1;
            IntStream.range(0, size).forEachOrdered(i -> buf.append(valueList.getQuick(i)).append(" "));
            buf.append(valueList.getQuick(size)).append("]\n");
            return buf.toString();
        }

        public void updateSource() {
            if (timeChecker.isUpToDate()) return;
            if (target instanceof UpdatableSource) ((UpdatableSource) target).updateSource();
            valueList.add(target.getIntValue(valueID));
        }
    }
}
