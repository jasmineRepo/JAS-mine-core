package microsim.statistics.weighted;

//package microsim.statistics.weighted;

/**
 * Used by statistical object to access array of float values. 
 *  
 * <p>Title: JAS-mine</p>
 * <p>Description: Java Agent-based Simulation library. Modelling in a Networked Environment</p>
 * <p>Copyright (C) 2017 Michele Sonnessa and Ross Richardson</p>
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
 * @author Michele Sonnessa and Ross Richardson
 * <p>
 */
public interface IWeightedFloatArraySource {
	/**
	 * Return the currently cached array of float values.
	 * @return An array of float or a null pointer if the source is empty. 
	 */
	public float[] getFloatArray();
	
	public double[] getWeights();
}
