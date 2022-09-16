package microsim.statistics.functions;

import lombok.NonNull;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.UpdatableSource;
import microsim.statistics.TimeChecker;

/**
 * An abstract skeleton for the statistical function able to manage update time checking.
 */
public abstract class AbstractFunction implements EventListener, UpdatableSource {

    protected TimeChecker timeChecker;

    public AbstractFunction() {
        timeChecker = new TimeChecker();
    }

    /**
     * Update the source, invoking the {@link #updateSource()} method.
     *
     * @param type Accepts only the {@link CommonEventType#Update} value.
     * @throws UnsupportedOperationException if actionId is not equal to the {@link CommonEventType#Update} value.
     */
    public void onEvent(final @NonNull Enum<?> type) {
        if (type.equals(CommonEventType.Update)) updateSource();
        else throw new SimulationRuntimeException("The action " + type + " is not supported by an ArrayFunction");
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
    public void setCheckingTime(boolean b) {
        timeChecker.setEnabled(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSource() {
        if (timeChecker.isUpToDate()) return;
        applyFunction();
    }

    public abstract void applyFunction();
}
