package microsim.gui.colormap;

import java.awt.Color;

/**
 * It builds automatically a color map oscillating from a bottom color to a
 * middle one and from the middle
 * to a top one, on a variable range.<br>
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
 *
 */
public class TripleRangeColorMap extends FixedColorMap {

    private int redStart, blueStart, greenStart;
    private int redMiddle, blueMiddle, greenMiddle;
    private int redEnd, blueEnd, greenEnd;
    private double rangeSize;

    public TripleRangeColorMap(int gradients, Color bottomColor, Color middleColor, Color topColor,
            double minValue, double midValue, double maxValue) {
        super(gradients);

        if (maxValue <= midValue || midValue <= minValue)
            throw new ArrayIndexOutOfBoundsException("ColorTripleRangeMap: range parameters are not corrected.");

        redStart = bottomColor.getRed();
        blueStart = bottomColor.getBlue();
        greenStart = bottomColor.getGreen();

        redMiddle = middleColor.getRed();
        blueMiddle = middleColor.getBlue();
        greenMiddle = middleColor.getGreen();

        redEnd = topColor.getRed();
        blueEnd = topColor.getBlue();
        greenEnd = topColor.getGreen();

        rangeSize = (maxValue - minValue) / gradients;

        int lowGradients = (int) ((midValue - minValue) / (maxValue - minValue) * gradients);

        for (int i = 0; i < lowGradients; i++) {
            // int[] c = getComponents(getBoundedCol(i * gap));
            int[] c = new int[] { 0, 0, 0 };
            c[0] = redStart + (int) ((redMiddle - redStart) * i / gradients);
            c[1] = greenStart + (int) ((greenMiddle - greenStart) * i / gradients);
            c[2] = blueStart + (int) ((blueMiddle - blueStart) * i / gradients);

            addColor(i, new Color(c[0], c[1], c[2]));
        }

        for (int i = lowGradients; i < gradients; i++) {
            // int[] c = getComponents(getBoundedCol(i * gap));
            int[] c = new int[] { 0, 0, 0 };
            c[0] = redMiddle + (int) ((redEnd - redMiddle) * i / gradients);
            c[1] = greenMiddle + (int) ((greenEnd - greenMiddle) * i / gradients);
            c[2] = blueMiddle + (int) ((blueEnd - blueMiddle) * i / gradients);

            addColor(i, new Color(c[0], c[1], c[2]));
        }

    }

    public int getColorIndex(double value) {
        int i = (int) (value * rangeSize);
        if (i < 0)
            i = 0;
        if (i >= colorList.length)
            i = colorList.length - 1;
        return i;
    }
}
