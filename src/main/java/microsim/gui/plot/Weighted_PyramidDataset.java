package microsim.gui.plot;

/* (C) Copyright 2020, by Kostas Manios
 * Based on Ross Richardson's Weighted_HistogramDataset.java, which in turn 
 * was based on JFreeChart's HistogramDataset.java by Object Refinery Limited 
 * and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * 
 * ---------------------
 * Weighted_PyramidDataset.java
 * ---------------------
 * (C) Copyright 2020, by Kostas Manios
 * 
 * (C) Copyright 2017, by Ross Richardson
 * 
 * Based on JFreeChart's HistogramDataset.java:
 * (C) Copyright 2003-2013, by Jelai Wang and Contributors.
 *
 * Original Author:  Jelai Wang (jelaiw AT mindspring.com);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Cameron Hayne;
 *                   Rikard Bj?rklind;
 *                   Thomas A Caswell (patch 2902842);
 *
 *
 */

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.category.CategoryDataset;

import microsim.gui.plot.Weighted_PyramidPlotter.GroupName;

/**
 * A weighted dataset that can be used for creating weighted pyramids.
 */
public class Weighted_PyramidDataset extends AbstractSeriesDataset
        implements CategoryDataset, Cloneable, PublicCloneable,
        Serializable {
    /** For serialization. */
    private static final long serialVersionUID = -6875925093485823495L;

    /** A list of maps. */
    private Map<String, Map<GroupName, Double>> dataMap;
    private double[][] groupRanges;
    private GroupName[] groupNames;
    private double scalingFactor = 1.0;

    /**
     * Creates a new dataset using the provided groupNames and
     * groupRanges to build a HashMap of total group weight.
     * The weights are adjusted by the provided scalingFactor.
     * 
     * @param groupNames    the names of each group to be generated
     *                      (<code>null</code> not permitted).
     * @param groupRanges   the ranges of each group to be generated
     *                      (<code>null</code> not permitted).
     * @param scalingFactor the scaling factor for the weights (<code>null</code>
     *                      not permitted).
     */
    public Weighted_PyramidDataset(GroupName[] groupNames, double[][] groupRanges, double scalingFactor) {
        Args.nullNotPermitted(groupNames, "groupNames");
        Args.nullNotPermitted(groupRanges, "groupRanges");
        Args.nullNotPermitted(scalingFactor, "scalingFactor");
        this.dataMap = new HashMap<String, Map<GroupName, Double>>();
        this.groupNames = groupNames;
        this.groupRanges = groupRanges;
        this.scalingFactor = scalingFactor;
    }

    /**
     * Adds the couple of series to the dataMap. Each value is assigned
     * to a group when it matches the group's min/max limits.
     *
     * @param keys       the series key (<code>null</code> not permitted).
     * @param values     the raw observations. (<code>null</code> not permitted).
     * @param weightings the weights associated with the values, i.e.
     *                   weight i indicates the number of times the value i appears
     *                   (<code>null</code> not permitted).
     */
    public void addSeries(String[] keys, double[][] values, double[][] weightings) {
        Args.nullNotPermitted(keys, "key");
        Args.nullNotPermitted(values, "values");
        Args.nullNotPermitted(weightings, "weightings");
        if (values.length != 2 || weightings.length != 2) {
            throw new IllegalArgumentException(
                    "You must provide a pair of series!");
        }
        if (values[0].length != weightings[0].length || values[1].length != weightings[1].length) {
            throw new IllegalArgumentException(
                    "The length of weightings array must be the same as the values array for each series!");
        }

        // Create and add the two series to the dataMap
        for (int s = 0; s < 2; s++) {
            // for each series create a new bucket to store the variable sums
            Map<GroupName, Double> bucket = new HashMap<GroupName, Double>();

            for (int v = 0; v < values[s].length; v++) { // for each value
                for (int g = 0; g < this.groupNames.length; g++) { // for each group
                    // if the value matches the group, add to the correct bucket element
                    if (values[s][v] >= this.groupRanges[g][0] && values[s][v] <= this.groupRanges[g][1]) {
                        // if the element does not exist, create it
                        if (!bucket.containsKey(this.groupNames[g]))
                            bucket.put(this.groupNames[g], 0.);
                        // multiply the weight by the scaling factor and add to the existing sum (negate
                        // if this is the left side),
                        bucket.put(this.groupNames[g], bucket.get(this.groupNames[g])
                                + weightings[s][v] * (s == 1 ? scalingFactor : -scalingFactor));
                        // do not check any more groups for this value
                        break;
                    }
                }
            }
            // store the series bucket
            dataMap.put(keys[s], bucket);
        }
    }

    /**
     * Returns the minimum value in an array of values.
     *
     * @param values the values (<code>null</code> not permitted and
     *               zero-length array not permitted).
     *
     * @return The minimum value.
     */
    private double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException(
                    "Null or zero length 'values' argument.");
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Returns the maximum value in an array of values.
     *
     * @param values the values (<code>null</code> not permitted and
     *               zero-length array not permitted).
     *
     * @return The maximum value.
     */
    private double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException(
                    "Null or zero length 'values' argument.");
        }
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj the object to test against (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Weighted_PyramidDataset)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the dataset.
     *
     * @return A clone of the dataset.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Weighted_PyramidDataset clone = (Weighted_PyramidDataset) super.clone();
        return clone;
    }

    public double[][] getDataArray() {
        double[][] data = new double[dataMap.keySet().size()][groupNames.length];
        int i = 0;
        for (Map<GroupName, Double> v : dataMap.values()) {
            int j = 0;
            for (GroupName entry : groupNames)
                // make sure that if either side of the dataset is missing a value, this is
                // filled with 0
                data[i][j++] = v.containsKey(entry) ? v.get(entry) : 0;
            i++;
        }

        return data;
    }

    @Override
    public List getColumnKeys() {
        // TODO Auto-generated method stub
        return Arrays.asList(this.groupNames);
    }

    @Override
    public Comparable getColumnKey(int column) {
        return this.groupNames[column];
    }

    public String[] getSeriesKeys() {
        return dataMap.keySet().toArray(new String[] {});
    }

    @Override
    public Comparable getRowKey(int row) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRowIndex(Comparable key) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List getRowKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getColumnIndex(Comparable key) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRowCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getColumnCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Number getValue(int row, int column) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addChangeListener(DatasetChangeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeChangeListener(DatasetChangeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public DatasetGroup getGroup() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGroup(DatasetGroup group) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getSeriesCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        // TODO Auto-generated method stub
        return null;
    }

}
