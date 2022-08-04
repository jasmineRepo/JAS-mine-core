package microsim.statistics;

import lombok.Getter;
import lombok.Setter;
import microsim.engine.SimulationEngine;

/**
 * The time checker is used by all the classes implementing the <i>UpdatableSource</i> interface to
 * avoid repetitive updates.<br>
 * Every time the <i>updateSource</i> method of the user class is invoked, it asks to TimeChecker
 * if the update has been done yet at the current simulation time.<br>
 * If the time checker is disabled it force the users to always update themselves.
 */
public class TimeChecker {
	private double lastUpdateTime = -1.;
	@Setter @Getter private boolean enabled = true;
	
	/**
	 * The method checks if a call has been already done at current simulation time. In case it has not
	 * yet invoked it updates to current time.
	 * @return True if the method has not yet been invoked at the current time, false otherwise  
	 */
	public boolean isUpToDate()
	{
		if (!enabled)
			return false;
			
		double t = SimulationEngine.getInstance().getEventQueue().getTime();
		if (t == lastUpdateTime)
			return true;
		else
		{
			lastUpdateTime = t;
			return false;
		}
	}
}
