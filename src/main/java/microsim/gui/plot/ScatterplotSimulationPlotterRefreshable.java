package microsim.gui.plot;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JInternalFrame;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.reflection.ReflectionUtils;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatSource;
import microsim.statistics.IIntSource;
import microsim.statistics.ILongSource;
import microsim.statistics.IUpdatableSource;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.FloatInvoker;
import microsim.statistics.reflectors.IntegerInvoker;
import microsim.statistics.reflectors.LongInvoker;

import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A ScatterplotSimulationPlotterRefreshable is able to trace one or more pairs
 * of data sources
 * between scheduled simulation time-steps, creating a scatterplot chart that is
 * updated on
 * demand (e.g. for visualising the progress of the simulation between
 * time-steps).
 * It is based on ScatterplotSimulationPlotter, which is itself based on
 * JFreeChart library and
 * uses data sources based on the microsim.statistics.* interfaces.<br>
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
public class ScatterplotSimulationPlotterRefreshable extends JInternalFrame implements EventListener {

    private static final long serialVersionUID = 1L;

    private ArrayList<Pair<Source, Source>> sources;

    private XYSeriesCollection dataset;

    private int maxSamples;

    /**
     * Constructor for scatterplot chart objects with chart legend displayed by
     * default and
     * all data samples shown, accumulating as time moves forward. If it is desired
     * to turn
     * the legend off, or set a limit to the number of previous time-steps of data
     * displayed
     * in the chart, use the constructor
     * ScatterplotSimulationPlotter(String title, String xaxis, String yaxis,
     * boolean includeLegend, int maxSamples)
     * 
     * @param title - title of the chart
     * @param xaxis - name of the x-axis
     * @param yaxis - name of the y-axis
     * 
     */
    public ScatterplotSimulationPlotterRefreshable(String title, String xaxis, String yaxis) { // Includes legend by
                                                                                               // default and will
                                                                                               // accumulate data
                                                                                               // samples by default (if
                                                                                               // wanting only the most
                                                                                               // recent data points,
                                                                                               // use the other
                                                                                               // constructor)
        this(title, xaxis, yaxis, true, 0);
    }

    /**
     * Constructor for scatterplot chart objects, featuring a toggle to hide the
     * chart legend
     * and to set the number of previous time-steps of data to display in the chart.
     * 
     * @param title         - title of the chart
     * @param xaxis         - name of the x-axis
     * @param yaxis         - name of the y-axis
     * @param includeLegend - toggles whether to include the legend. If displaying a
     *                      very large number of different series in the chart, it
     *                      may be useful to turn
     *                      the legend off as it will occupy a lot of space in the
     *                      GUI.
     * @param maxSamples    - the number of 'snapshots' of data displayed in the
     *                      chart.
     *                      Only data from the last 'maxSamples' updates will be
     *                      displayed in the chart,
     *                      so if the chart is updated at each 'time-step', then
     *                      only the most recent
     *                      'maxSamples' time-steps will be shown on the chart. If
     *                      the user wishes to
     *                      accumulate all data points from the simulation run, i.e.
     *                      to display all
     *                      available data from all previous time-steps, set this to
     *                      0.
     */
    public ScatterplotSimulationPlotterRefreshable(String title, String xaxis, String yaxis, boolean includeLegend,
            int maxSamples) { // Can specify whether to include legend and how many samples (updates) to
                              // display
        super();
        this.setResizable(true);
        this.setTitle(title);
        this.maxSamples = maxSamples;

        sources = new ArrayList<Pair<Source, Source>>();

        dataset = new XYSeriesCollection();

        final JFreeChart chart = ChartFactory.createScatterPlot(
                title, // chart title
                xaxis, // x axis label
                yaxis, // y axis label
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
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true); // Shapes only
        // renderer.setSeriesLinesVisible(0, false);
        // renderer.setSeriesShapesVisible(1, false);
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
        double x = 0.0, y = 0.0;
        for (int i = 0; i < sources.size(); i++) {
            Source source_X = sources.get(i).getFirst();
            Source source_Y = sources.get(i).getSecond();
            XYSeries series = dataset.getSeries(i);
            x = source_X.getDouble();
            y = source_Y.getDouble();
            series.add(x, y);
            // if (maxSamples > 0 && series.getItemCount() > maxSamples ) { //Should no
            // longer be necessary if using XYSeries.setMaximumItemCount()
            // XYDataItem xy = series.remove(0);
            // System.out.println(series.getItemCount() + ", (" + xy.getXValue() + ", " +
            // xy.getYValue() + ")");
            // }
        }
    }

    private abstract class Source {
        // public String label;
        public Enum<?> vId;
        protected boolean isUpdatable;

        public abstract double getDouble();

        // public String getLabel() {
        // return label;
        // }
        //
        // public void setLabel(String string) {
        // label = string;
        // }

    }

    private class DSource extends Source {
        public IDoubleSource source;

        public DSource(String label, IDoubleSource source, Enum<?> varId) {
            // super.label = label;
            this.source = source;
            super.vId = varId;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double getDouble() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            return source.getDoubleValue(vId);
        }
    }

    private class FSource extends Source {
        public IFloatSource source;

        public FSource(String label, IFloatSource source, Enum<?> varId) {
            // super.label = label;
            this.source = source;
            super.vId = varId;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double getDouble() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            return source.getFloatValue(vId);
        }
    }

    private class ISource extends Source {
        public IIntSource source;

        public ISource(String label, IIntSource source, Enum<?> varId) {
            // super.label = label;
            this.source = source;
            super.vId = varId;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double getDouble() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            return source.getIntValue(vId);
        }
    }

    private class LSource extends Source {
        public ILongSource source;

        public LSource(String label, ILongSource source, Enum<?> varId) {
            // super.label = label;
            this.source = source;
            super.vId = varId;
            isUpdatable = (source instanceof IUpdatableSource);
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.plot.TimePlot.Source#getDouble()
         */
        public double getDouble() {
            if (isUpdatable)
                ((IUpdatableSource) source).updateSource();
            return source.getLongValue(vId);
        }
    }

    /**
     * Build a series of paired values, retrieving data from two IDoubleSource
     * objects, using the
     * default variableId.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface to produce values for the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface to produce values for the y-axis (range).
     */
    public void addSeries(String legend, IDoubleSource plottableObject_X, IDoubleSource plottableObject_Y) {
        DSource sourceX = new DSource(legend, plottableObject_X, IDoubleSource.Variables.Default);
        DSource sourceY = new DSource(legend, plottableObject_Y, IDoubleSource.Variables.Default);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values, retrieving data from two IDoubleSource
     * objects.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface producing values of the x-axis (domain).
     * @param variableID_X
     *                          The variable id of the source object producing
     *                          values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface producing values of the y-axis (range).
     * @param variableID_Y
     *                          The variable id of the source object producing
     *                          values of the y-axis (range).
     */
    public void addSeries(String legend, IDoubleSource plottableObject_X,
            Enum<?> variableID_X, IDoubleSource plottableObject_Y, Enum<?> variableID_Y) {
        DSource sourceX = new DSource(legend, plottableObject_X, variableID_X);
        DSource sourceY = new DSource(legend, plottableObject_Y, variableID_Y);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two IFloatSource objects, using the
     * default variableId.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the IFloatSource
     *                          interface to produce values for the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the IFloatSource
     *                          interface to produce values for the y-axis (range).
     * 
     */
    public void addSeries(String legend, IFloatSource plottableObject_X, IFloatSource plottableObject_Y) {
        // sources.add(new FSource(legend, plottableObject,
        // IFloatSource.Variables.Default));
        FSource sourceX = new FSource(legend, plottableObject_X, IFloatSource.Variables.Default);
        FSource sourceY = new FSource(legend, plottableObject_Y, IFloatSource.Variables.Default);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));

        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two IFloatSource objects.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the IFloatSource
     *                          interface producing values of the x-axis (domain).
     * @param variableID_X
     *                          The variable id of the source object producing
     *                          values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the IFloatSource
     *                          interface producing values of the y-axis (range).
     * @param variableID_Y
     *                          The variable id of the source object producing
     *                          values of the y-axis (range).
     */
    public void addSeries(String legend, IFloatSource plottableObject_X,
            Enum<?> variableID_X, IFloatSource plottableObject_Y, Enum<?> variableID_Y) {
        FSource sourceX = new FSource(legend, plottableObject_X, variableID_X);
        FSource sourceY = new FSource(legend, plottableObject_Y, variableID_Y);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two ILongSource objects, using the
     * default variableId.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the ILongSource
     *                          interface producing values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the ILongSource
     *                          interface producing values of the y-axis (range).
     */
    public void addSeries(String legend, ILongSource plottableObject_X, ILongSource plottableObject_Y) {
        // sources.add(new LSource(legend, plottableObject,
        // ILongSource.Variables.Default));
        LSource sourceX = new LSource(legend, plottableObject_X, ILongSource.Variables.Default);
        LSource sourceY = new LSource(legend, plottableObject_Y, ILongSource.Variables.Default);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));

        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two ILongSource objects
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the ILongSource
     *                          interface producing values of the x-axis (domain).
     * @param variableID_X
     *                          The variable id of the source object producing
     *                          values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the ILongSource
     *                          interface producing values of the y-axis (range).
     * @param variableID_Y
     *                          The variable id of the source object producing
     *                          values of the y-axis (range).
     */
    public void addSeries(String legend, ILongSource plottableObject_X,
            Enum<?> variableID_X, ILongSource plottableObject_Y, Enum<?> variableID_Y) {
        LSource sourceX = new LSource(legend, plottableObject_X, variableID_X);
        LSource sourceY = new LSource(legend, plottableObject_Y, variableID_Y);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two IIntSource objects, using the
     * default variableId.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the IIntSource
     *                          interface
     *                          producing values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the IIntSource
     *                          interface
     *                          producing values of the y-axis (range).
     */
    public void addSeries(String legend, IIntSource plottableObject_X, IIntSource plottableObject_Y) {
        // sources.add(new ISource(legend, plottableObject,
        // IIntSource.Variables.Default));
        ISource sourceX = new ISource(legend, plottableObject_X, IIntSource.Variables.Default);
        ISource sourceY = new ISource(legend, plottableObject_Y, IIntSource.Variables.Default);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));

        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two IIntSource objects.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the IIntSource
     *                          interface
     *                          producing values of the x-axis (domain).
     * @param variableID_X
     *                          The variable id of the source object producing
     *                          values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the IIntSource
     *                          interface
     *                          producing values of the y-axis (range).
     * @param variableID_Y
     *                          The variable id of the source object producing
     *                          values of the y-axis (range).
     * 
     */
    public void addSeries(String legend, IIntSource plottableObject_X,
            Enum<?> variableID_X, IIntSource plottableObject_Y, Enum<?> variableID_Y) {
        // sources.add(new ISource(legend, plottableObject, variableID));
        ISource sourceX = new ISource(legend, plottableObject_X, variableID_X);
        ISource sourceY = new ISource(legend, plottableObject_Y, variableID_Y);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));

        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values from two generic objects.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param target_X
     *                        The data source object for x-axis values (domain).
     * @param variableName_X
     *                        The variable or method name of the source object
     *                        producing
     *                        values for the x-axis (domain).
     * @param getFromMethod_X
     *                        Specifies if the variableName_X is a field or a
     *                        method.
     * @param target_Y
     *                        The data source object for y-axis values (range).
     * @param variableName_Y
     *                        The variable or method name of the source object
     *                        producing
     *                        values for the y-axis (range).
     * @param getFromMethod_Y
     *                        Specifies if the variableName_Y is a field or a
     *                        method.
     */
    public void addSeries(String legend, Object target_X, String variableName_X,
            boolean getFromMethod_X, Object target_Y, String variableName_Y,
            boolean getFromMethod_Y) {

        // First, look at X values
        Source sourceX = null;
        if (ReflectionUtils.isDoubleSource(target_X.getClass(), variableName_X,
                getFromMethod_X))
            sourceX = new DSource(legend, new DoubleInvoker(target_X,
                    variableName_X, getFromMethod_X), IDoubleSource.Variables.Default);
        else if (ReflectionUtils.isFloatSource(target_X.getClass(), variableName_X,
                getFromMethod_X))
            sourceX = new FSource(legend, new FloatInvoker(target_X, variableName_X,
                    getFromMethod_X), IFloatSource.Variables.Default);
        else if (ReflectionUtils.isIntSource(target_X.getClass(), variableName_X,
                getFromMethod_X))
            sourceX = new ISource(legend, new IntegerInvoker(target_X,
                    variableName_X, getFromMethod_X), IIntSource.Variables.Default);
        else if (ReflectionUtils.isLongSource(target_X.getClass(), variableName_X,
                getFromMethod_X))
            sourceX = new LSource(legend, new LongInvoker(target_X, variableName_X,
                    getFromMethod_X), ILongSource.Variables.Default);
        else
            throw new IllegalArgumentException("The target_X object " + target_X
                    + " does not provide a value of a valid data type.");

        // Now for Y values
        Source sourceY = null;
        if (ReflectionUtils.isDoubleSource(target_Y.getClass(), variableName_Y,
                getFromMethod_Y))
            sourceY = new DSource(legend, new DoubleInvoker(target_Y,
                    variableName_Y, getFromMethod_Y), IDoubleSource.Variables.Default);
        else if (ReflectionUtils.isFloatSource(target_Y.getClass(), variableName_Y,
                getFromMethod_Y))
            sourceY = new FSource(legend, new FloatInvoker(target_Y, variableName_Y,
                    getFromMethod_Y), IFloatSource.Variables.Default);
        else if (ReflectionUtils.isIntSource(target_Y.getClass(), variableName_Y,
                getFromMethod_Y))
            sourceY = new ISource(legend, new IntegerInvoker(target_Y,
                    variableName_Y, getFromMethod_Y), IIntSource.Variables.Default);
        else if (ReflectionUtils.isLongSource(target_Y.getClass(), variableName_Y,
                getFromMethod_Y))
            sourceY = new LSource(legend, new LongInvoker(target_Y, variableName_Y,
                    getFromMethod_Y), ILongSource.Variables.Default);
        else
            throw new IllegalArgumentException("The target_Y object " + target_Y
                    + " does not provide a value of a valid data type.");

        // sources.add(source);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
        // plot.addLegend(sources.size() - 1, legend);
    }

    /**
     * Build a series of paired values, retrieving x-axis data from an IDoubleSource
     * object and y-axis data
     * from an ILongSource object, using the default variableId.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface to produce values for the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the ILongSource
     *                          interface to produce values for the y-axis (range).
     */
    public void addSeries(String legend, IDoubleSource plottableObject_X, ILongSource plottableObject_Y) {
        DSource sourceX = new DSource(legend, plottableObject_X, IDoubleSource.Variables.Default);
        LSource sourceY = new LSource(legend, plottableObject_Y, ILongSource.Variables.Default);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values, retrieving x-axis data from an IDoubleSource
     * object and y-axis data
     * from an ILongSource object.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface producing values of the x-axis (domain).
     * @param variableID_X
     *                          The variable id of the source object producing
     *                          values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the ILongSource
     *                          interface producing values of the y-axis (range).
     * @param variableID_Y
     *                          The variable id of the source object producing
     *                          values of the y-axis (range).
     */
    public void addSeries(String legend, IDoubleSource plottableObject_X,
            Enum<?> variableID_X, ILongSource plottableObject_Y, Enum<?> variableID_Y) {
        DSource sourceX = new DSource(legend, plottableObject_X, variableID_X);
        LSource sourceY = new LSource(legend, plottableObject_Y, variableID_Y);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values, retrieving x-axis data from an ILongSource
     * object and
     * y-axis data from an IDoubleSource object, using the default variableId.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the ILongSource
     *                          interface to produce values for the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface to produce values for the y-axis (range).
     */
    public void addSeries(String legend, ILongSource plottableObject_X, IDoubleSource plottableObject_Y) {
        LSource sourceX = new LSource(legend, plottableObject_X, ILongSource.Variables.Default);
        DSource sourceY = new DSource(legend, plottableObject_Y, IDoubleSource.Variables.Default);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series of paired values, retrieving x-axis data from an ILongSource
     * object and
     * y-axis data from an IDoubleSource object.
     * 
     * @param legend
     *                          The legend name of the series.
     * @param plottableObject_X
     *                          The data source object implementing the ILongSource
     *                          interface producing values of the x-axis (domain).
     * @param variableID_X
     *                          The variable id of the source object producing
     *                          values of the x-axis (domain).
     * @param plottableObject_Y
     *                          The data source object implementing the
     *                          IDoubleSource
     *                          interface producing values of the y-axis (range).
     * @param variableID_Y
     *                          The variable id of the source object producing
     *                          values of the y-axis (range).
     */
    public void addSeries(String legend, ILongSource plottableObject_X,
            Enum<?> variableID_X, IDoubleSource plottableObject_Y, Enum<?> variableID_Y) {
        LSource sourceX = new LSource(legend, plottableObject_X, variableID_X);
        DSource sourceY = new DSource(legend, plottableObject_Y, variableID_Y);
        sources.add(new Pair<Source, Source>(sourceX, sourceY));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Max samples parameters allow to define a maximum number of time-steps used
     * in the scatter plot. When set, the oldest data points are removed as time
     * moves forward to maintain the number of samples (time-steps) in the chart.
     */
    public int getMaxSamples() {
        return maxSamples;
    }

    /**
     * Set the max sample parameter.
     * 
     * @param maxSamples Maximum number of time-steps rendered on x axis.
     */
    public void setMaxSamples(int maxSamples) {
        this.maxSamples = maxSamples;
    }

    public void refresh() {

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            dataset.getSeries(i).clear();
        }

    }

    public void reset() {

        dataset.removeAllSeries();
        sources.clear();

    }

}
