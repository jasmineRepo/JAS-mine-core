package microsim.statistics;

import microsim.engine.SimulationEngine;

/**
 * The time checker is used by all the classes implementing the <i>IUpdatableSource</i> interface to 
 * avoid repetitive updates.<br>
 * Every time the <i>updateSource</i> method of the user class is invoked, it asks to TimeChecker
 * if the update has been done yet at the current simulation time.<br>
 * If the time checker is disabled it force the users to always update themselves.
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
 * @author Michele Sonnessa
 * 
 */
public class TimeChecker {
	private double lastUpdateTime = -1.;
	private boolean disabled = false;
	
	/**
	 * The method checks if a call has been already done at current simulation time. In case it has not
	 * yet invoked it updates to current time.
	 * @return True if the method has not yet been invoked at the current time, false otherwise  
	 */
	public boolean isUpToDate()
	{
		if (disabled)
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
	
	/** Return the current enabling state.
	 * @return True if the time checking is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return !disabled;
	}

	/** Enable/disable the time checking.
	 * @param b True to enable and false to diable time checking.
	 */
	public void setEnabled(boolean b) {
		disabled = !b;
	}

}
