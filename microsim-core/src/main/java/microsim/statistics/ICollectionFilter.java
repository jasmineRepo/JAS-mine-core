package microsim.statistics;

/**
 * A collection filter is used by the CrossSection class to filter elements of 
 * the source collection.
 * <p>Imagine to have a list of agents of the class MyAgent, which contains a boolean variable
 * isFemale and an int variable age.</p>
 * <p>In order to obtain the list of ages of the males in the collection, the user can
 * crate a class inheriting from ICollectionFilter</p>
 * <p>During the cross section iteration of the list, the isFiltered method is passed the 
 * current object and the relative value is read only if the filter returns true.</p>
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
 * <p>
 */
public interface ICollectionFilter {
	/**
	 * Return if the passed object must be considered by the CrossSection iterator.
	 * @param object The current object in the CrossSection iteration of the source collection.
	 * @return True if object has the required status, false otherwise.
	 */
	public boolean isFiltered(Object object);
}
