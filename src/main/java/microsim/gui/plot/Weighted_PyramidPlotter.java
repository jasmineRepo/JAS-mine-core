package microsim.gui.plot;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.swing.JInternalFrame;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.IUpdatableSource;
import microsim.statistics.weighted.IWeightedDoubleArraySource;
import microsim.statistics.weighted.IWeightedFloatArraySource;
import microsim.statistics.weighted.IWeightedIntArraySource;
import microsim.statistics.weighted.IWeightedLongArraySource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.general.DatasetUtils;

/**
 * A PyramidPlotter is able to display a pyramid using two weighted
 * cross-sections of a variable (e.g. dag males/females for a
 * population pyramid). It can be updated during the simulation. It
 * is based on JFreeChart library and uses data sources based on the
 * microsim.statistics.weighted* interfaces.<br>
 * Note that the weights are taken into account by adding the weight to the
 * count
 * of each group. Groups can be optionally provided by the caller.
 * 
 * 
 * <p>
 * Title: JAS-mine
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2020 Kostas Manios
 * </p>
 * 
 * This work is based on "Weighted_HistogramSimulationPlotter.java" by Ross
 * Richardson
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 * 
 * @author Kostas Manios
 * 
 *         <p>
 */
public class Weighted_PyramidPlotter extends JInternalFrame implements EventListener {

    /**
     * Default values
     */
    public static String DEFAULT_TITLE = "Population Chart";
    public static String DEFAULT_XAXIS = "Age Group";
    public static String DEFAULT_YAXIS = "Population";
    public static String DEFAULT_LEFT_CAT = "Males";
    public static String DEFAULT_RIGHT_CAT = "Females";
    public static Boolean DEFAULT_REVERSE_ORDER = false;
    public static String DEFAULT_YAXIS_FORMAT = "#.##";
    private static int MAXIMUM_VISIBLE_CATEGORIES = 20;

    /**
     * Variables
     */

    private static final long serialVersionUID = 1L;

    private JFreeChart chart;

    private WeightedArraySource[] sources;

    private Weighted_PyramidDataset dataset;

    private String xaxis;

    private String yaxis;

    private String yaxisFormat = DEFAULT_YAXIS_FORMAT;

    private final String[] catNames = new String[2];

    private GroupName[] groupNames;

    private double[][] groupRanges; // These need to be doubles for the DatasetUtilities.createCategoryDataset
                                    // method

    private double scalingFactor; // This scales the sample (e.g. to the whole population)

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * Default values are used for all parameters: title, x-axis, y-axis, category
     * names, age group names/ranges, reverseOrder
     * It generates one age group per unique age, whose title is that age.
     * 
     */
    public Weighted_PyramidPlotter() {
        this(DEFAULT_TITLE, DEFAULT_XAXIS, DEFAULT_YAXIS, DEFAULT_LEFT_CAT, DEFAULT_RIGHT_CAT);
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * Default values are used for the following parameters: x-axis, y-axis,
     * category names, age group names/ranges, reverseOrder
     * It generates one age group per unique age, whose title is that age.
     * 
     * @param title - title of the chart
     * 
     */
    public Weighted_PyramidPlotter(String title) {
        this(title, DEFAULT_XAXIS, DEFAULT_YAXIS, DEFAULT_LEFT_CAT, DEFAULT_RIGHT_CAT);
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * Default values are used for the following parameters: category names, age
     * group names/ranges, reverseOrder
     * It generates one age group per unique age, whose title is that age.
     * 
     * @param title - title of the chart
     * @param xaxis - name of the x-axis
     * @param yaxis - name of the y-axis
     * 
     *
     *              public PopulationPyramidPlotter(String title, String xaxis,
     *              String yaxis) {
     *              this(title, xaxis, yaxis, DEFAULT_LEFT_CAT, DEFAULT_RIGHT_CAT);
     *              }
     */

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * Default values are used for the following parameters: age group names/ranges,
     * reverseOrder
     * It generates one age group per unique age, whose title is that age.
     * 
     * @param title    - title of the chart
     * @param xaxis    - name of the x-axis
     * @param yaxis    - name of the y-axis
     * @param leftCat  - the name of the left category
     * @param rightCat - the name of the right category
     * 
     */
    public Weighted_PyramidPlotter(String title, String xaxis, String yaxis, String leftCat, String rightCat) {
        // fix the titles and prepare the plotter, leaving the groups null
        fixTitles(title, xaxis, yaxis, leftCat, rightCat);
        preparePlotter();
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * It generates groups names and ranges using the start/end/step values
     * provided.
     * Default values are used for the following parameters: x-axis, y-axis,
     * category names, age group names/ranges, reverseOrder
     * 
     * @param start - the minimum accepted value in groups
     * @param end   - the maximum accepted value in groups
     * @param step  - the step used to separate value into groups
     * 
     */
    public Weighted_PyramidPlotter(int start, int end, int step) {
        this(DEFAULT_TITLE, DEFAULT_XAXIS, DEFAULT_YAXIS, DEFAULT_LEFT_CAT, DEFAULT_RIGHT_CAT, start, end, step,
                DEFAULT_REVERSE_ORDER, DEFAULT_YAXIS_FORMAT);
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * It generates groups names and ranges using the start/end/step and order
     * values provided.
     * Default values are used for the following parameters: x-axis, y-axis,
     * category names, age group names/ranges
     * 
     * @param start        - the minimum accepted value in groups
     * @param end          - the maximum accepted value in groups
     * @param step         - the step used to separate value into groups
     * @param reverseOrder - if true, it will reverse the groups
     * 
     */
    public Weighted_PyramidPlotter(int start, int end, int step, Boolean reverseOrder) {
        this(DEFAULT_TITLE, DEFAULT_XAXIS, DEFAULT_YAXIS, DEFAULT_LEFT_CAT, DEFAULT_RIGHT_CAT, start, end, step,
                reverseOrder, DEFAULT_YAXIS_FORMAT);
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * It generates groups names and ranges using the start/end/step values
     * provided.
     * Descending order is used by default.
     * 
     * @param title        - title of the chart
     * @param xaxis        - name of the x-axis
     * @param yaxis        - name of the y-axis
     * @param leftCat      - the name of the left category
     * @param rightCat     - the name of the right category
     * @param start        - the minimum accepted value in groups
     * @param end          - the maximum accepted value in groups
     * @param step         - the step used to separate value into groups
     * @param reverseOrder - if true, it will reverse the groups
     * 
     */
    public Weighted_PyramidPlotter(String title, String xaxis, String yaxis, String leftCat, String rightCat, int start,
            int end, int step, Boolean reverseOrder, String format) {
        if (step == 0)
            return;
        fixTitles(title, xaxis, yaxis, leftCat, rightCat);
        yaxisFormat = format;

        // Create the groups based on the range, and save them to "this"
        GroupDetails gd = makeGroupsFromRange(start, end, step, reverseOrder, format);
        this.groupNames = gd.groupNames;
        this.groupRanges = gd.groupRanges;

        preparePlotter();
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * It generates groups based on the names and ranges provided.
     * Default values are used for the following parameters: title, x-axis, y-axis,
     * category names
     * 
     * @param groupNames  - an array of the name of each group
     * @param groupRanges - an array of the min/max values of each group
     * 
     */
    public Weighted_PyramidPlotter(String[] groupNames, double[][] groupRanges) {
        this(DEFAULT_TITLE, DEFAULT_XAXIS, DEFAULT_YAXIS, DEFAULT_LEFT_CAT, DEFAULT_RIGHT_CAT, groupNames, groupRanges,
                DEFAULT_YAXIS_FORMAT);
    }

    /**
     * Constructor for pyramid objects, showing only the latest data as time moves
     * forward.
     * It generates groups based on the names and ranges provided.
     * 
     * @param title       - title of the chart
     * @param xaxis       - name of the x-axis
     * @param yaxis       - name of the y-axis
     * @param leftCat     - the name of the left category
     * @param rightCat    - the name of the right category
     * @param groupNames  - an array of the name of each group
     * @param groupRanges - an array of the min/max values of each group
     * 
     */
    public Weighted_PyramidPlotter(String title, String xaxis, String yaxis, String leftCat, String rightCat,
            String[] groupNames, double[][] groupRanges, String format) {
        fixTitles(title, xaxis, yaxis, leftCat, rightCat);
        yaxisFormat = format;

        // Fix names
        this.groupNames = groupNames == null ? null : getGroupNamesFromStrings(groupNames);
        this.groupRanges = groupRanges;

        preparePlotter();
    }

    // The function that prepares the titles
    private void fixTitles(String title, String xaxis, String yaxis, String leftCat, String rightCat) {
        this.setTitle(title);
        this.xaxis = xaxis;
        this.yaxis = yaxis;
        this.catNames[0] = leftCat;
        this.catNames[1] = rightCat;
    }

    // the function that calculates groups from a range
    private GroupDetails makeGroupsFromRange(int start, int end, int step, Boolean reverseOrder, String format) {
        // First we calculate the optimal (visually at least!) number of groups, so that
        // the last group ends with "max" and its size is "(0.5 * step) < size <
        // (1.5*step)"
        int noOfGroups = (int) Math
                .max(Math.round((double) (end - start) / (double) step) + (Math.abs(step) == 1 ? 1 : 0), 1);
        // *Note: should we enforce equal groups sizes?

        // Then, if required, we reverse the order
        if (reverseOrder) {
            int temp = start;
            start = end;
            end = temp;
            step = -step;
        }

        // Then we calculate the group ranges & names
        String[] groupNames = new String[noOfGroups];
        double[][] groupRanges = new double[noOfGroups][2];

        // asc checks whether we are ascending or descending
        Boolean asc = start <= end;
        for (int i = 0; i < noOfGroups; i++) {
            // The range needs to always be stored in ascending order, hence the extended
            // use of "asc" here. Sorry! :)
            // <from> is calculated based on the current step value
            groupRanges[i][asc ? 0 : 1] = start + i * step;
            // <to> is equal to the next group's "<from> - 1", but for the last group it is
            // equal to "end"
            groupRanges[i][asc ? 1 : 0] = (i == noOfGroups - 1) ? end : (start + (i + 1) * step) - (asc ? 1 : -1);
            // for the name, if step=1 use the step value, else show as "from - to"
            // (inclusive)
            groupNames[i] = groupRanges[i][0] == groupRanges[i][1] ? new DecimalFormat(format).format(groupRanges[i][0])
                    : new DecimalFormat(format).format(groupRanges[i][asc ? 0 : 1]) + " - "
                            + new DecimalFormat(format).format(groupRanges[i][asc ? 1 : 0]);

        }

        return new GroupDetails(getGroupNamesFromStrings(groupNames), groupRanges);
    }

    private static GroupName[] getGroupNamesFromStrings(String[] groupStrings) {
        GroupName[] groupNames = new GroupName[groupStrings.length];

        int stepShow = (int) Math.ceil((double) groupStrings.length / (double) MAXIMUM_VISIBLE_CATEGORIES);

        // Show only every Nth string and always the first & last
        for (int i = 0; i < groupStrings.length; i++) {
            groupNames[i] = new GroupName(groupStrings[i], (i % stepShow == 0 || i == groupStrings.length - 1));
        }

        return groupNames;
    }

    private void preparePlotter() {
        this.setResizable(true);
        sources = new WeightedArraySource[2];

        chart = ChartFactory.createStackedBarChart(
                title, // chart title
                this.xaxis, // x axis label
                this.yaxis, // y axis label
                DatasetUtils.createCategoryDataset(this.catNames, new String[] { "" },
                        new double[][] { { 0 }, { 0 } }),
                PlotOrientation.HORIZONTAL,
                true, // include legend
                true,
                true);

        setChartProperties();

        chart.getCategoryPlot().getRangeAxis().setVisible(false);

        final ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        setContentPane(chartPanel);

        this.setSize(400, 400);
    }

    public void onEvent(Enum<?> type) {
        if (type instanceof CommonEventType && type.equals(CommonEventType.Update)) {
            update();
        }
    }

    // This function generates a new chart based on the latest data
    public void update() {
        if (sources.length != 2 || catNames.length != 2)
            return;
        GroupName[] groupNames = null;
        double[][] groupRanges = null;

        // Get the source data
        var leftData = sources[0];
        var rightData = sources[1];
        final double[][] vals = new double[][] { leftData.getDoubleArray(), rightData.getDoubleArray() };
        final double[][] weights = new double[][] { leftData.getWeights(), rightData.getWeights() };

        // If there are no groups defined, create one for each age between the min/max
        // found in the data
        // *Note: do we want this done in every repetition, or should we save to "this"?
        if (this.groupNames == null || this.groupRanges == null) {
            int min = (int) Math.min(Arrays.stream(vals[0]).min().orElse(0), Arrays.stream(vals[1]).min().orElse(0)); // if
                                                                                                                      // there
                                                                                                                      // is
                                                                                                                      // no
                                                                                                                      // data,
                                                                                                                      // set
                                                                                                                      // min
                                                                                                                      // to
                                                                                                                      // 0
            int max = (int) Math.min(Arrays.stream(vals[0]).max().orElse(100),
                    Arrays.stream(vals[1]).max().orElse(100)); // if there is no data, set max to 100
            // Create the groups based on the range, and save them to the local variables
            GroupDetails gd = makeGroupsFromRange(min, max, 1, true, yaxisFormat);
            groupNames = gd.groupNames;
            groupRanges = gd.groupRanges;
        } else {
            // else, just use the existing groups
            groupNames = this.groupNames;
            groupRanges = this.groupRanges;
        }

        // Create the dataset and add the data
        dataset = new Weighted_PyramidDataset(groupNames, groupRanges, scalingFactor);
        dataset.addSeries(this.catNames, vals, weights);

        chart = ChartFactory.createStackedBarChart(
                this.title, // chart title
                this.xaxis, // x axis label
                this.yaxis, // y axis label
                DatasetUtils.createCategoryDataset(
                        dataset.getSeriesKeys(),
                        groupNames,
                        dataset.getDataArray()), // data
                PlotOrientation.HORIZONTAL,
                true, // include legend
                true,
                true);

        setChartProperties();

        final ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        setContentPane(chartPanel);

    }

    /**
     * This function sets the default Chart Properties.
     */
    private void setChartProperties() {
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);
        plot.setForegroundAlpha(0.85f);
        plot.setShadowGenerator(null);
        final StackedBarRenderer renderer = new StackedBarRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        plot.setRenderer(renderer);

        // hide the sign for negative numbers in yAxis
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(new DecimalFormat("0; 0 "));

    }

    private class GroupDetails {
        public GroupName[] groupNames;
        public double[][] groupRanges;

        public GroupDetails(GroupName[] groupNames, double[][] groupRanges) {
            this.groupNames = groupNames;
            this.groupRanges = groupRanges;
        }
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    public static class GroupName implements Comparable<GroupName> {
        String value;
        Boolean show;

        GroupName(String val, Boolean sh) {
            value = val;
            show = sh;
        }

        public int compareTo(GroupName key) {
            return value.compareTo(key.value);
        }

        public String toString() {
            return show ? value : "";
        }
    }

    private abstract class WeightedArraySource {
        public String label;
        protected boolean isUpdatable;

        public abstract double[] getDoubleArray();

        public abstract double[] getWeights();
    }

    private class DWeightedArraySource extends WeightedArraySource {
        public IWeightedDoubleArraySource source;

        public DWeightedArraySource(String label, IWeightedDoubleArraySource source) {
            super.label = label;
            this.source = source;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double[] getDoubleArray() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            return source.getDoubleArray();
        }

        @Override
        public double[] getWeights() {
            return source.getWeights();
        }
    }

    private class FWeightedArraySource extends WeightedArraySource {
        public IWeightedFloatArraySource source;

        public FWeightedArraySource(String label, IWeightedFloatArraySource source) {
            super.label = label;
            this.source = source;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double[] getDoubleArray() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            float[] array = source.getFloatArray();
            double[] output = new double[array.length];
            for (int i = 0; i < array.length; i++)
                output[i] = array[i];

            return output;
        }

        @Override
        public double[] getWeights() {
            return source.getWeights();
        }
    }

    private class IWeightedArraySource extends WeightedArraySource {
        public IWeightedIntArraySource source;

        public IWeightedArraySource(String label, IWeightedIntArraySource source) {
            super.label = label;
            this.source = source;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double[] getDoubleArray() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            int[] array = source.getIntArray();
            double[] output = new double[array.length];
            for (int i = 0; i < array.length; i++)
                output[i] = array[i];

            return output;
        }

        @Override
        public double[] getWeights() {
            return source.getWeights();
        }
    }

    private class LWeightedArraySource extends WeightedArraySource {
        public IWeightedLongArraySource source;

        public LWeightedArraySource(String label, IWeightedLongArraySource source) {
            super.label = label;
            this.source = source;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double[] getDoubleArray() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            long[] array = source.getLongArray();
            double[] output = new double[array.length];
            for (int i = 0; i < array.length; i++)
                output[i] = array[i];

            return output;
        }

        @Override
        public double[] getWeights() {
            return source.getWeights();
        }
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedDoubleSource objects
     * in a
     * collection.
     * 
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(IWeightedDoubleArraySource[] source) {
        if (source.length != 2)
            return;
        if (catNames.length != 2)
            return;
        sources[0] = new DWeightedArraySource(catNames[0], source[0]);
        sources[1] = new DWeightedArraySource(catNames[1], source[1]);
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedFloatSource objects
     * in a
     * collection.
     * 
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(IWeightedFloatArraySource[] source) {
        if (source.length != 2)
            return;
        if (catNames.length != 2)
            return;
        sources[0] = new FWeightedArraySource(catNames[0], source[0]);
        sources[1] = new FWeightedArraySource(catNames[1], source[1]);
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedIntArraySource
     * objects in a
     * collection.
     * 
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(IWeightedIntArraySource[] source) {
        if (source.length != 2)
            return;
        if (catNames.length != 2)
            return;
        sources[0] = new IWeightedArraySource(catNames[0], source[0]);
        sources[1] = new IWeightedArraySource(catNames[1], source[1]);
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedLongSource objects in
     * a
     * collection.
     * 
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(IWeightedLongArraySource[] source) {
        if (source.length != 2)
            return;
        if (catNames.length != 2)
            return;
        sources[0] = new LWeightedArraySource(catNames[0], source[0]);
        sources[1] = new LWeightedArraySource(catNames[1], source[1]);
    }

}
