package microsim.statistics.functions;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.IUpdatableSource;
import microsim.statistics.TimeChecker;

/**
 * An abstract skeleton for the statistical function able to manage update time checking.
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
public abstract class AbstractFunction implements EventListener, IUpdatableSource {

	protected TimeChecker timeChecker;

	public AbstractFunction()
	{
		timeChecker = new TimeChecker();
	}
	
	/**
	 * Update the source, invoking the <i>updateSource()</i> method.
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
	 * @see jas.statistics.IUpdatableSource#updateSource()
	 */
	public void updateSource()
	{
		if (timeChecker.isUpToDate())
			return;
		applyFunction();
	}
		
	public abstract void applyFunction();
}
