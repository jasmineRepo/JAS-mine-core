package microsim.statistics.functions;

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

	public AbstractFunction()
	{
		timeChecker = new TimeChecker();
	}
	
	/**
	 * Update the source, invoking the <i>updateSource()</i> method.
	 * @param type Accepts only the jas.engine.Sim.EVENT_UPDATE value.
	 * @throws UnsupportedOperationException if actionId is not equal to the jas.engine.Sim.EVENT_UPDATE value.
	 */	
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new SimulationRuntimeException("The action " + type + " is not supported by an ArrayFunction");
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
	/* (non-Javadoc)
	 * @see jas.statistics.UpdatableSource#updateSource()
	 */
	public void updateSource()
	{
		if (timeChecker.isUpToDate())
			return;
		applyFunction();
	}
		
	public abstract void applyFunction();
}
