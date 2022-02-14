package microsim.statistics;

/**
 * Used by statistical object to access object data. Each variable must have a unique integer id.
 *
 * <p>Title: JAS-mine</p>
 * <p>Description: Java Agent-based Simulation library.  Modelling In a Networked Environment.</p>
 * <p>Copyright (C) 2015 Ross Richardson</p>
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
 * @author Ross Richardson
 * <p>
 */
public interface IObjectSource {

	public enum Variables {
		Default;
	}
	
	/**
	 * Return the value corresponding to the given variableID
	 * @param variableID A unique identifier for a variable.
	 * @return The current value of the required variable.
	 */
	public Object getObjectValue(Enum<?> variableID);
}
