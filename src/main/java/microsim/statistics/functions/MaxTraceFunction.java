package microsim.statistics.functions;

import lombok.Getter;
import microsim.event.CommonEventType;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.DoubleSource;
import microsim.statistics.FloatSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;
import microsim.statistics.UpdatableSource;
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
 */
public abstract class MaxTraceFunction extends AbstractFunction implements DoubleSource {

    protected int count = 0;

    /**
     * Collect a value from the source.
     */
    public void applyFunction() {
        count++;
    }

    /**
     * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
     *
     * @param type The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
     * @throws UnsupportedOperationException If actionType is not supported.
     */
    @Override
    public void onEvent(Enum<?> type) {
        if (type.equals(CommonEventType.Update)) updateSource();
        else throw new SimulationRuntimeException("The SimpleStatistics object does not support " + type + " operation.");
    }

    public enum Variables {
        LastValue,
        Max
    }

    /**
     * An implementation of the MemorylessSeries class, which manages long type data sources.
     */
    public static class Long extends MaxTraceFunction implements LongSource {
        private final Enum<?> valueID;
        @Getter
        protected long max = java.lang.Long.MIN_VALUE;
        protected LongSource target;
        @Getter
        private long lastRead;

        /**
         * Create a basic statistic probe on a IDblSource object.
         *
         * @param source  The LongSource object.
         * @param valueID The value identifier defined by source object.
         */
        public Long(LongSource source, Enum<?> valueID) {
            super();
            target = source;
            this.valueID = valueID;
        }

        /**
         * Create a basic statistic probe on a generic object.
         *
         * @param source        A generic source object.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Long(Object source, String valueName, boolean getFromMethod) {
            super();
            target = new LongInvoker(source, valueName, getFromMethod);
            valueID = LongSource.Variables.Default;
        }

        /**
         * Read the source values and update statistics.
         */
        public void applyFunction() {
            super.applyFunction();
            if (target instanceof UpdatableSource)
                ((UpdatableSource) target).updateSource();
            lastRead = target.getLongValue(valueID);

            if (lastRead > max)
                max = lastRead;
        }

        /**
         * Return the result of a given statistic.
         *
         * @param valueID One of the F_ constants representing available statistics.
         * @return The computed value.
         * @throws UnsupportedOperationException If the given valueID is not supported.
         */
        public double getDoubleValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> (double) lastRead;
                case Max -> (double) max;
            };
        }

        /**
         * Return the result of a given statistic.
         *
         * @param valueID One of the F_ constants representing available statistics.
         * @return The computed value.
         * @throws UnsupportedOperationException If the given valueID is not supported.
         */
        public long getLongValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> lastRead;
                case Max -> max;
            };
        }
    }

    /**
     * An implementation of the MemorylessSeries class, which manages double type data sources.
     */
    public static class Double extends MaxTraceFunction implements DoubleSource {
        private final Enum<?> valueID;
        @Getter
        protected double max = java.lang.Double.MIN_VALUE;
        protected DoubleSource target;
        @Getter
        private double lastRead;

        /**
         * Create a basic statistic probe on a IDblSource object.
         *
         * @param source  The IDblSource object.
         * @param valueID The value identifier defined by source object.
         */
        public Double(DoubleSource source, Enum<?> valueID) {
            super();
            target = source;
            this.valueID = valueID;
        }

        /**
         * Create a basic statistic probe on a generic object.
         *
         * @param source        A generic source object.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Double(Object source, String valueName, boolean getFromMethod) {
            super();
            target = new DoubleInvoker(source, valueName, getFromMethod);
            valueID = DoubleSource.Variables.Default;
        }

        /**
         * Read the source values and update statistics.
         */
        public void applyFunction() {
            super.applyFunction();
            if (target instanceof UpdatableSource)
                ((UpdatableSource) target).updateSource();
            lastRead = target.getDoubleValue(valueID);

            if (lastRead > max)
                max = lastRead;
        }

        /**
         * Return the result of a given statistic.
         *
         * @param valueID One of the F_ constants representing available statistics.
         * @return The computed value.
         * @throws UnsupportedOperationException If the given valueID is not supported.
         */
        public double getDoubleValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> lastRead;
                case Max -> max;
            };
        }
    }

    /**
     * An implementation of the MemorylessSeries class, which manages integer type data sources.
     */
    public static class Integer extends MaxTraceFunction implements IntSource {
        private final Enum<?> valueID;
        @Getter
        protected int max = java.lang.Integer.MIN_VALUE;
        protected IntSource target;
        @Getter
        private int lastRead;

        /**
         * Create a basic statistic probe on a IDblSource object.
         *
         * @param source  The IntSource object.
         * @param valueID The value identifier defined by source object.
         */
        public Integer(IntSource source, Enum<?> valueID) {
            super();
            target = source;
            this.valueID = valueID;
        }

        /**
         * Create a basic statistic probe on a generic object.
         *
         * @param source        A generic source object.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Integer(Object source, String valueName, boolean getFromMethod) {
            super();
            target = new IntegerInvoker(source, valueName, getFromMethod);
            valueID = IntSource.Variables.Default;
        }

        /**
         * Read the source values and update statistics.
         */
        public void applyFunction() {
            super.applyFunction();
            if (target instanceof UpdatableSource)
                ((UpdatableSource) target).updateSource();
            lastRead = target.getIntValue(valueID);

            if (lastRead > max)
                max = lastRead;
        }

        /**
         * Return the result of a given statistic.
         *
         * @param valueID One of the F_ constants representing available statistics.
         * @return The computed value.
         * @throws UnsupportedOperationException If the given valueID is not supported.
         */
        public double getDoubleValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> lastRead;
                case Max -> max;
            };
        }

        public int getIntValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> lastRead;
                case Max -> max;
            };
        }
    }

    /**
     * An implementation of the MemorylessSeries class, which manages float type data sources.
     */
    public static class Float extends MaxTraceFunction implements FloatSource {
        private final Enum<?> valueID;
        @Getter
        protected float max = java.lang.Float.MIN_VALUE;
        protected FloatSource target;
        @Getter
        private float lastRead;

        /**
         * Create a basic statistic probe on a IDblSource object.
         *
         * @param source  The FloatSource object.
         * @param valueID The value identifier defined by source object.
         */
        public Float(FloatSource source, Enum<?> valueID) {
            super();
            target = source;
            this.valueID = valueID;
        }

        /**
         * Create a basic statistic probe on a generic object.
         *
         * @param source        A generic source object.
         * @param valueName     The name of the field or the method returning the variable to be probed.
         * @param getFromMethod Specifies if valueName is a method or a property value.
         */
        public Float(Object source, String valueName, boolean getFromMethod) {
            super();
            target = new FloatInvoker(source, valueName, getFromMethod);
            valueID = FloatSource.Variables.Default;
        }

        /**
         * Read the source values and update statistics.
         */
        public void applyFunction() {
            super.applyFunction();
            if (target instanceof UpdatableSource)
                ((UpdatableSource) target).updateSource();
            lastRead = target.getFloatValue(valueID);

            if (lastRead > max)
                max = lastRead;
        }

        /**
         * Return the result of a given statistic.
         *
         * @param valueID One of the F_ constants representing available statistics.
         * @return The computed value.
         * @throws UnsupportedOperationException If the given valueID is not supported.
         */
        public double getDoubleValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> (double) lastRead;
                case Max -> (double) max;
            };
        }

        /**
         * Return the result of a given statistic.
         *
         * @param valueID One of the F_ constants representing available statistics.
         * @return The computed value.
         * @throws UnsupportedOperationException If the given valueID is not supported.
         */
        public float getFloatValue(Enum<?> valueID) {
            return switch ((MaxTraceFunction.Variables) valueID) {
                case LastValue -> lastRead;
                case Max -> max;
            };
        }
    }
}
