package microsim.gui.colormap;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * An object used to map integer values to colors.
 * It is used by {@code Layered<Type>Drawer} to draw objects of the
 * LayeredSurfaceFrame.
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
 */
public class FixedColorMap implements ColorMap {
    protected Color[] colorList;
    protected int[][] colorComponents;
    protected int colors = 0;
    protected Map<Integer, Integer> mapper;

    /** Create a color map. */
    public FixedColorMap() {
        colorList = new Color[0];
        colorComponents = new int[0][3];
        mapper = new HashMap<Integer, Integer>();
    }

    /**
     * Create a color map with an initial capacity of mapping positions.
     * 
     * @param colors The number of colors to be mapped. Must be a non-zero positive.
     */
    public FixedColorMap(int colors) {
        if (colors <= 0)
            throw new ArrayIndexOutOfBoundsException("ColorMap: 'colors' must be a positive number.");

        colorList = new Color[colors];
        colorComponents = new int[colors][3];
        mapper = new HashMap<Integer, Integer>(colors);
    }

    private void ensureCapacity(int unitsRequired) {
        if (colorList.length > unitsRequired)
            return;

        Color[] c = new Color[unitsRequired];
        int[][] cc = new int[unitsRequired][3];

        System.arraycopy(colorList, 0, c, 0, colorList.length);
        System.arraycopy(colorComponents, 0, cc, 0, colorComponents.length);

        colorList = c;
        colorComponents = cc;
    }

    /**
     * Add a color to the map.
     * 
     * @param value The integer value that maps to the given color.
     * @param color The color to be mapped.
     * @throws ArrayIndexOutOfBoundsException If value is greather than the defined
     *                                        mapped colors.
     */
    public void addColor(int value, Color color) {
        if (value < 0)
            throw new IllegalArgumentException("ColorMap.addColor: value parameter must be positive.");

        if (mapper.containsKey(value))
            throw new IllegalArgumentException("ColorMap.addColor: value " + value + " already added.");

        ensureCapacity(colors + 1);

        colorList[colors] = color;
        colorComponents[colors][0] = color.getRed();
        colorComponents[colors][1] = color.getGreen();
        colorComponents[colors][2] = color.getBlue();
        mapper.put(value, colors++);
    }

    /**
     * Add a color to the map.
     * 
     * @param value The integer value that maps to the given color.
     * @param red   The red component of the color to be mapped. [0-255] range
     *              accepted.
     * @param green The green component of the color to be mapped. [0-255] range
     *              accepted.
     * @param blue  The blue component of the color to be mapped. [0-255] range
     *              accepted.
     * @throws ArrayIndexOutOfBoundsException If one of the three color components
     *                                        is out of (0, 255) range.
     */
    public void addColor(int value, int red, int green, int blue) {
        if (red < 0 || red > 255)
            throw new ArrayIndexOutOfBoundsException("ColorMap.addColor: Red component must be in range [0, 255]");
        if (green < 0 || green > 255)
            throw new ArrayIndexOutOfBoundsException("ColorMap.addColor: Green component must be in range [0, 255]");
        if (blue < 0 || blue > 255)
            throw new ArrayIndexOutOfBoundsException("ColorMap.addColor: Blue component must be in range [0, 255]");

        addColor(value, new Color(red, green, blue));
    }

    /**
     * Return the color to at the given index position.
     * 
     * @param index The value to be mapped.
     * @return The color corresponding to the value.
     */
    public Color getColor(int index) {
        return colorList[index];
    }

    /**
     * Return the color list.
     * 
     * @return An array of Color. The index represent the mapping value.
     */
    public Color[] toArray() {
        return colorList;
    }

    public int[] getColorComponents(int index) {
        return colorComponents[index];
    }

    /**
     * Return the color index.
     * 
     * @param value The value to be mapped.
     * @return The index of the color list mapping the value.
     */
    public int getColorIndex(int value) {
        int k = mapper.get(value);
        if (k == Integer.MIN_VALUE)
            throw new ArrayIndexOutOfBoundsException("ColorMap.getColorIndex: Value "
                    + value + " not mapped.");
        return k;
    }

    /**
     * Return the color index.
     * 
     * @param value The value to be mapped.
     * @return The index of the color list mapping the value.
     */
    public int getColorIndex(double value) {
        int k = mapper.get((int) value);
        if (k == Integer.MIN_VALUE)
            throw new ArrayIndexOutOfBoundsException("ColorMap.getColorIndex: Value "
                    + value + " not mapped.");
        return k;
    }

    /**
     * Map the given value with the right color.
     * 
     * @param value The value to be mapped.
     * @return The color corresponding to the value.
     */
    public Color getMappedColor(int value) {
        return colorList[getColorIndex(value)];
    }
}
