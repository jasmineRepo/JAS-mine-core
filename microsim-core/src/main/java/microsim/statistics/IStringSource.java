package microsim.statistics;

/**
 * Used by statistical object to access string data. Each variable must have a unique integer id.
 *
 * <p>Title: JAS</p>
 * <p>Description: Java Agent-based Simulation library</p>
 * <p>Copyright (C) 2002-3 Michele Sonnessa</p>
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
 * <p>
 */
public interface IStringSource {
	/** The default variable id.*/
	public static final int DEFAULT = 0; 
	/**
	 * Return the double value corresponding to the given variableID
	 * @param variableID A unique identifier for a variable.
	 * @return The current double value of the required variable.
	 */
	public String getStringValue(Enum<?> variableID);
}
