package microsim.gui.colormap;

import java.awt.Color;

/**
 * It builds automatically a color map varying between two colors on a variable
 * range.
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002 under GPL library
 * </p>
 * 
 * @author Matteo Morini and Michele Sonnessa
 */

public class DoubleRangeColorMap extends FixedColorMap {
    private int redStart, blueStart, greenStart;
    private int redEnd, blueEnd, greenEnd;
    private double rangeSize;

    public DoubleRangeColorMap(int gradients, Color bottomColor, Color topColor,
            double minValue, double maxValue) {
        super(gradients);

        if (maxValue <= minValue)
            throw new ArrayIndexOutOfBoundsException("ColorDualRangeMap: range parameters are not corrected.");

        redStart = bottomColor.getRed();
        blueStart = bottomColor.getBlue();
        greenStart = bottomColor.getGreen();

        redEnd = topColor.getRed();
        blueEnd = topColor.getBlue();
        greenEnd = topColor.getGreen();

        rangeSize = (maxValue - minValue) / gradients;

        for (int i = 0; i < gradients; i++) {
            // int[] c = getComponents(getBoundedCol(i * gap));
            int[] c = new int[] { 0, 0, 0 };
            c[0] = redStart + ((redEnd - redStart) * i / gradients);
            c[1] = greenStart + ((greenEnd - redStart) * i / gradients);
            c[2] = blueStart + ((blueEnd - redStart) * i / gradients);

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
