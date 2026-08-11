package microsim.gui.plot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

import javax.swing.JInternalFrame;

import microsim.engine.SimulationEngine;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.statistics.HistogramType;

/**
 * A Weighted_HistogramSimulationPlotter is able to display a histogram of one
 * or more
 * data sources that each implements the Weight interface,
 * and can be updated during the simulation.
 * It is based on JFreeChart library and uses data sources based on the
 * microsim.statistics.weighted* interfaces.<br>
 * Note that the weights are taken into account by adding the weight to the
 * count
 * of the histogram bin corresponding to the value associated with the weight.
 * E.g, if a weighted object has value of 1.6 and weight of 5.3, the count of
 * 5.3
 * is placed in the histogram bin appropriate for the value of 1.6.
 * 
 * 
 * <p>
 * Title: JAS-mine
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2017 Ross Richardson
 * </p>
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
 * @author Ross Richardson
 */
public class Weighted_HistogramSimulationPlotter extends JInternalFrame implements EventListener {

    private static final long serialVersionUID = 1L;

    final JFreeChart chart;

    private ArrayList<Supplier<? extends List<? extends Number>>> sources;
    private ArrayList<Supplier<? extends List<? extends Number>>> weights;
    private ArrayList<String> labels;

    private Weighted_HistogramDataset dataset;

    private HistogramType type;

    private int bins;

    private Double minimum;

    private Double maximum;

    /**
     * Constructor for histogram chart objects with chart legend displayed by
     * default and
     * all data samples shown, showing only the latest population data as time moves
     * forward.
     * Note - values falling on the boundary of adjacent bins will be assigned to
     * the higher
     * indexed bin. If it is desired set the minimum and maximum values displayed,
     * or to turn
     * the legend off, use the constructor:
     * HistogramSimulationPlotter(String title, String xaxis, HistogramType type,
     * int bins, double minimum, double maximum, boolean includeLegend)
     * 
     * @param title - title of the chart
     * @param xaxis - name of the x-axis
     * @param type  - the type of the histogram: either FREQUENCY,
     *              RELATIVE_FREQUENCY, or SCALE_AREA_TO_1
     * @param bins  - the number of bins in the histogram
     * 
     */
    public Weighted_HistogramSimulationPlotter(String title, String xaxis, HistogramType type, int bins) { // Includes
                                                                                                           // legend by
                                                                                                           // default
                                                                                                           // and will
                                                                                                           // accumulate
                                                                                                           // data
                                                                                                           // samples by
                                                                                                           // default
                                                                                                           // (if
                                                                                                           // wanting
                                                                                                           // only the
                                                                                                           // most
                                                                                                           // recent
                                                                                                           // data
                                                                                                           // points,
                                                                                                           // use the
                                                                                                           // other
                                                                                                           // constructor)
        this(title, xaxis, type, bins, null, null, true);
    }

    /**
     * Constructor for scatterplot chart objects, featuring a toggle to hide the
     * chart legend
     * and to set the minimum and maximum values displayed in the chart, with values
     * below the minimum
     * assigned to the first bin, and values above the maximum assigned to the last
     * bin. Note -
     * values falling on the boundary of adjacent bins will be assigned to the
     * higher indexed bin.
     * 
     * @param title         - title of the chart
     * @param xaxis         - name of the x-axis
     * @param type          - the type of the histogram: either FREQUENCY,
     *                      RELATIVE_FREQUENCY, or SCALE_AREA_TO_1
     * @param bins          - the number of bins in the histogram
     * @param minimum       - any data value less than minimum will be assigned to
     *                      the first bin
     * @param maximum       - any data value greater than maximum will be assigned
     *                      to the last bin
     * @param includeLegend - toggles whether to include the legend. If displaying a
     *                      very large number of different series in the chart, it
     *                      may be useful to turn
     *                      the legend off as it will occupy a lot of space in the
     *                      GUI.
     */
    public Weighted_HistogramSimulationPlotter(String title, String xaxis, HistogramType type, int bins, Double minimum,
            Double maximum, boolean includeLegend) { // Can specify whether to include legend and how many samples
                                                     // (updates) to display
        // super(title, xaxis, type, bins, minimum, maximum, includeLegend); //invoke
        // HistogramSimulationPlotter constructor
        this.setResizable(true);
        this.setTitle(title);
        this.type = type;
        this.bins = bins;
        this.minimum = minimum;
        this.maximum = maximum;

        sources = new ArrayList<>();
        weights = new ArrayList<>();
        labels = new ArrayList<>();

        dataset = new Weighted_HistogramDataset();

        String yaxis;
        if (type.equals(HistogramType.FREQUENCY)) {
            yaxis = "Frequency";
        } else if (type.equals(HistogramType.RELATIVE_FREQUENCY)) {
            yaxis = "Relative Frequency";
            throw new IllegalArgumentException(
                    "ERROR - RELATIVE_FREQUENCY Histogram Type is not currently available for Weighted_HistogramSimulationPlotter!  Please use FREQUENCY (or possibly SCALE_AREA_TO_1) as the Histogram Type instead.");
        } else if (type.equals(HistogramType.SCALE_AREA_TO_1)) {
            System.out.println(
                    "WARNING - the SCALE_AREA_TO_1 Weighted_HistogramSimulationPlotter has not been tested and may produce incorrect output!");
            yaxis = "Density (area scaled to 1)";
        } else
            throw new IllegalArgumentException(
                    "Incorrect HistogramType argument when calling HistogramSimulationPlotter constructor!");

        chart = ChartFactory.createHistogram(
                title, // chart title
                xaxis, // x axis label
                yaxis, // y axis label
                // type.toString(), //y axis label based on the type of the histogram
                dataset, // data
                PlotOrientation.VERTICAL,
                includeLegend, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        // plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setForegroundAlpha(0.85f);

        final XYBarRenderer renderer = new XYBarRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        plot.setRenderer(renderer);

        final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

        final ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        setContentPane(chartPanel);

        this.setSize(400, 400);
    }

    public void addSource(String label,
            Supplier<? extends List<? extends Number>> values,
            Supplier<? extends List<? extends Number>> weights) {
        this.sources.add(values);
        this.weights.add(weights);
        this.labels.add(label);
    }

    public void onEvent(Enum<?> type) {
        if (type instanceof CommonEventType && type.equals(CommonEventType.Update)) {
            update();
        }
    }

    public void update() {

        dataset = new Weighted_HistogramDataset();
        dataset.setType(type);
        chart.getXYPlot().setDataset(dataset);

        // int s = 0;
        // Color color = (Color) chart.getXYPlot().getRenderer().getItemPaint(s, 0);
        // int r = color.getRed();
        // int g = color.getGreen();
        // int b = color.getBlue();
        // chart.getXYPlot().getRenderer().setSeriesPaint(s, new Color(r, g, b, 130));

        for (int i = 0; i < sources.size(); i++) {
            var vals = sources.get(i).get().stream().mapToDouble(Number::doubleValue).toArray();
            var wgts = weights.get(i).get().stream().mapToDouble(Number::doubleValue).toArray();
            var label = labels.get(i);
            if (minimum != null && maximum != null) {
                dataset.addSeries(label, vals, wgts, bins, minimum, maximum);
            } else
                dataset.addSeries(label, vals, wgts, bins);

        }
        dataset.seriesChanged(
                new SeriesChangeEvent(new String("Update at time " + SimulationEngine.getInstance().getTime())));

    }

    private abstract class WeightedArraySource {
        protected boolean isUpdatable;

        public abstract double[] getDoubleArray();

        public abstract double[] getWeights();

        public List<Double> values() {
            return DoubleStream.of(this.getDoubleArray()).boxed().toList();
        }

        public List<Double> weights() {
            return DoubleStream.of(this.getWeights()).boxed().toList();
        }
    }

    private class DWeightedArraySource extends WeightedArraySource {
        public IWeightedDoubleArraySource source;

        public DWeightedArraySource(IWeightedDoubleArraySource source) {
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

        public FWeightedArraySource(IWeightedFloatArraySource source) {
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

        public IWeightedArraySource(IWeightedIntArraySource source) {
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

        public LWeightedArraySource(IWeightedLongArraySource source) {
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
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IWeightedDoubleArraySource source) {
        var sequence = new DWeightedArraySource(source);
        this.addSource(name, sequence::values, sequence::weights);
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedFloatSource objects
     * in a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IWeightedFloatArraySource source) {
        var sequence = new FWeightedArraySource(source);
        this.addSource(name, sequence::values, sequence::weights);
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedIntArraySource
     * objects in a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IWeightedIntArraySource source) {
        var sequence = new IWeightedArraySource(source);
        this.addSource(name, sequence::values, sequence::weights);
    }

    /**
     * Add a new series buffer, retrieving value from IWeightedLongSource objects in
     * a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IWeightedLongArraySource source) {
        var sequence = new LWeightedArraySource(source);
        this.addSource(name, sequence::values, sequence::weights);
    }

}
