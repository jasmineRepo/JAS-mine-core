package microsim.gui.colormap;

/**
 * A generic interface for color mappers. This interface
 * is required by {@code Layer<NativeType>Drawer} objects to
 * paint values on the screen.
 *
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa
 * </p>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 *         <p>
 */
public interface ColorMap {

    /**
     * Return the components of the color stored at given index.
     * 
     * @param index The index of the color. It is a 0-based index of the color
     *              corresponding to the adding order.
     * @return An array of 3 integers representing the RGB components of the color.
     */
    public int[] getColorComponents(int index);

    /**
     * Return the index of the color mapped to the given value.
     * 
     * @param value The value mapped to the color.
     * @return The array index of the requested color.
     */
    public int getColorIndex(int value);

    /**
     * Return the index of the color mapped to the given value.
     * 
     * @param value The value mapped to the color.
     * @return The array index of the requested color.
     */
    public int getColorIndex(double value);

}
