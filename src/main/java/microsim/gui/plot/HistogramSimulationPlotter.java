package microsim.gui.plot;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JInternalFrame;

import microsim.engine.SimulationEngine;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.statistics.IDoubleArraySource;
import microsim.statistics.IFloatArraySource;
import microsim.statistics.IIntArraySource;
import microsim.statistics.ILongArraySource;
import microsim.statistics.IUpdatableSource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

/**
 * A HistogramSimulationPlotter is able to display a histogram of one or more
 * data
 * sources, which can be updated during the simulation. It is based on
 * JFreeChart
 * library and uses data sources based on the microsim.statistics.*
 * interfaces.<br>
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
 *         <p>
 */
public class HistogramSimulationPlotter extends JInternalFrame implements EventListener {

    private static final long serialVersionUID = 1L;

    final JFreeChart chart;

    private ArrayList<ArraySource> sources;

    private HistogramDataset dataset;

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
    public HistogramSimulationPlotter(String title, String xaxis, HistogramType type, int bins) { // Includes legend by
                                                                                                  // default and will
                                                                                                  // accumulate data
                                                                                                  // samples by default
                                                                                                  // (if wanting only
                                                                                                  // the most recent
                                                                                                  // data points, use
                                                                                                  // the other
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
    public HistogramSimulationPlotter(String title, String xaxis, HistogramType type, int bins, Double minimum,
            Double maximum, boolean includeLegend) { // Can specify whether to include legend and how many samples
                                                     // (updates) to display
        super();
        this.setResizable(true);
        this.setTitle(title);
        this.type = type;
        this.bins = bins;
        this.minimum = minimum;
        this.maximum = maximum;

        sources = new ArrayList<ArraySource>();

        dataset = new HistogramDataset();

        String yaxis;
        if (type.equals(HistogramType.FREQUENCY)) {
            yaxis = "Frequency";
        } else if (type.equals(HistogramType.RELATIVE_FREQUENCY)) {
            yaxis = "Relative Frequency";
        } else if (type.equals(HistogramType.SCALE_AREA_TO_1)) {
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

    public void onEvent(Enum<?> type) {
        if (type instanceof CommonEventType && type.equals(CommonEventType.Update)) {
            update();
        }
    }

    public void update() {

        dataset = new HistogramDataset();
        dataset.setType(type);
        chart.getXYPlot().setDataset(dataset);

        // int s = 0;
        // Color color = (Color) chart.getXYPlot().getRenderer().getItemPaint(s, 0);
        // int r = color.getRed();
        // int g = color.getGreen();
        // int b = color.getBlue();
        // chart.getXYPlot().getRenderer().setSeriesPaint(s, new Color(r, g, b, 130));

        for (int i = 0; i < sources.size(); i++) {
            ArraySource cs = (ArraySource) sources.get(i);
            double[] vals = cs.getDoubleArray();

            if (minimum != null && maximum != null) {
                dataset.addSeries(cs.label, vals, bins, minimum, maximum);
            } else
                dataset.addSeries(cs.label, vals, bins);

        }
        dataset.seriesChanged(
                new SeriesChangeEvent(new String("Update at time " + SimulationEngine.getInstance().getTime())));

    }

    private abstract class ArraySource {
        public String label;
        protected boolean isUpdatable;

        public abstract double[] getDoubleArray();

    }

    private class DArraySource extends ArraySource {
        public IDoubleArraySource source;

        public DArraySource(String label, IDoubleArraySource source) {
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
    }

    private class FArraySource extends ArraySource {
        public IFloatArraySource source;

        public FArraySource(String label, IFloatArraySource source) {
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
    }

    private class IArraySource extends ArraySource {
        public IIntArraySource source;

        public IArraySource(String label, IIntArraySource source) {
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
    }

    private class LArraySource extends ArraySource {
        public ILongArraySource source;

        public LArraySource(String label, ILongArraySource source) {
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
    }

    /**
     * Add a new series buffer, retrieving value from IDoubleSource objects in a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IDoubleArraySource source) {
        DArraySource sequence = new DArraySource(name, source);
        sources.add(sequence);
    }

    /**
     * Add a new series buffer, retrieving value from IDoubleSource objects in a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IFloatArraySource source) {
        FArraySource sequence = new FArraySource(name, source);
        sources.add(sequence);
    }

    /**
     * Add a new series buffer, retrieving value from IDoubleSource objects in a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, IIntArraySource source) {
        IArraySource sequence = new IArraySource(name, source);
        sources.add(sequence);
    }

    /**
     * Add a new series buffer, retrieving value from IDoubleSource objects in a
     * collection.
     * 
     * @param name
     *               The name of the series, which is shown in the legend.
     * @param source
     *               A collection containing the sources.
     */
    public void addCollectionSource(String name, ILongArraySource source) {
        LArraySource sequence = new LArraySource(name, source);
        sources.add(sequence);
    }

}
