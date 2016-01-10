package microsim.statistics;

/**
 * An updatable source is a class implementing one of the I*Source interfaces, which needs
 * to be updated to refresh its data. Typically an data source which retrieves data from
 * other source keeps them into a cache and refreshes the cache when the consumer needs
 * a new refresh.<br>
 * A CrossSection, for instance, is an updatable source, since it collects data from a 
 * collection. Accessing its source interface, the consumer can obtain the lastest cached
 * data, but if it want updated ones it has to invoke the <i>updateSource()</i> method of 
 * the CrossSection.<br>
 * Each statistical object contained in the <i>jas.statistics</i> package automatically refreshes
 * the IUpdatableSource source before retrieving the latest data.
 * 
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
public interface IUpdatableSource {
	/**
	 * Force the source to update its currently cached data.
	 */
	public void updateSource();
}
