package microsim.gui.plot;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JInternalFrame;

import microsim.engine.SimulationEngine;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A time series plotter is able to trace one or more data sources over time. It
 * is based on JFreeChart library and uses data sources based on the
 * microsim.statistics.* interfaces.<br>
 * 
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002-17 Michele Sonnessa and Ross Richardson
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
 * @author Michele Sonnessa and Ross Richardson
 *         <p>
 */
public class TimeSeriesSimulationPlotter extends JInternalFrame implements EventListener {

    private static final long serialVersionUID = 1L;

    private ArrayList<Source> sources;

    private XYSeriesCollection dataset;

    private XYPlot plot;

    private XYLineAndShapeRenderer renderer;

    private int maxSamples = 0;

    public TimeSeriesSimulationPlotter(String title, String yaxis) { // Include legend by default
        this(title, yaxis, true, 0);
    }

    public TimeSeriesSimulationPlotter(String title, String yaxis, boolean includeLegend, int maxSamples) { // Can
                                                                                                            // specify
                                                                                                            // whether
                                                                                                            // to
                                                                                                            // include
                                                                                                            // legend
        super();
        this.setResizable(true);
        this.setTitle(title);
        this.maxSamples = maxSamples;

        sources = new ArrayList<Source>();

        dataset = new XYSeriesCollection();

        final JFreeChart chart = ChartFactory.createXYLineChart(
                title, // chart title
                "Simulation time", // x axis label
                yaxis, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                includeLegend, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        // String fontName = chart.getLegend().getItemFont().getFontName();
        // int style = chart.getLegend().getItemFont().getStyle();
        // int size = chart.getLegend().getItemFont().getSize();
        // chart.getLegend().setItemFont(new Font(fontName, style,
        // (int)MicrosimShell.scale*size));

        // get a reference to the plot for further customisation...
        plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        renderer = new XYLineAndShapeRenderer();
        // renderer.setSeriesLinesVisible(0, false);
        // renderer.setSeriesShapesVisible(1, false);

        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        // rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits()); // Ross - made this change to allow units
                                                                              // on Y axis for finer ticks, which is
                                                                              // especially important for timeseries
                                                                              // with values < 1.

        final ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        setContentPane(chartPanel);

        this.setSize(400, 400);
    }

    public void onEvent(Enum<?> type) {
        if (type instanceof CommonEventType && type.equals(CommonEventType.Update)) {
            double d = 0.0;
            for (int i = 0; i < sources.size(); i++) {
                Source source = sources.get(i);
                XYSeries series = dataset.getSeries(i);
                d = source.getDouble();
                series.add(SimulationEngine.getInstance().getTime(), d);
                // if (maxSamples > 0 && series.getItemCount() > maxSamples ) {
                // series.remove(0);
                // }
            }
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
     * Build a series retrieving data from a IDoubleSource object, using the
     * default variableId.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IDoubleSource
     *                        interface.
     */
    public void addSeries(String legend, IDoubleSource plottableObject) {
        sources.add(new DSource(legend, plottableObject, IDoubleSource.Variables.Default));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    public void addSeries(String legend, IDoubleSource plottableObject, Color lineColor, boolean shapesFilled,
            boolean isDashed, Shape shape) {
        sources.add(new DSource(legend, plottableObject, IDoubleSource.Variables.Default));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);

        int seriesIndex = dataset.getSeriesIndex(series.getKey()); // Get int Index of series using its key
        Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                new float[] { 10.0f }, 0.0f);
        getRenderer().setSeriesPaint(seriesIndex, lineColor); // Set color of the series in the renderer to what was
                                                              // requested
        getRenderer().setSeriesShapesFilled(seriesIndex, shapesFilled); // Set if shapes should be filled or not
        if (isDashed) {
            getRenderer().setSeriesStroke(seriesIndex, dashed);
        }
        getRenderer().setSeriesShape(seriesIndex, shape);
    }

    /**
     * Build a series retrieving data from a IDoubleSource object.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IDoubleSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     */
    public void addSeries(String legend, IDoubleSource plottableObject,
            Enum<?> variableID) {
        sources.add(new DSource(legend, plottableObject, variableID));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    public void addSeries(String legend, IDoubleSource plottableObject, Enum<?> variableID, Color lineColor,
            boolean shapesFilled, boolean isDashed, Shape shape) {
        sources.add(new DSource(legend, plottableObject, variableID));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
        int seriesIndex = dataset.getSeriesIndex(series.getKey()); // Get int Index of series using its key

        Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                new float[] { 10.0f }, 0.0f);
        getRenderer().setSeriesPaint(seriesIndex, lineColor); // Set color of the series in the renderer to what was
                                                              // requested
        getRenderer().setSeriesShapesFilled(seriesIndex, shapesFilled); // Set if shapes should be filled or not
        if (isDashed) {
            getRenderer().setSeriesStroke(seriesIndex, dashed);
        }
        getRenderer().setSeriesShape(seriesIndex, shape);

    }

    public void addSeries(String legend, IDoubleSource plottableObject, Enum<?> variableID, Color lineColor,
            boolean validation) {
        if (validation) {
            Shape myRectangle = new Rectangle2D.Float(-3, -3, 6, 6);
            addSeries(legend, plottableObject, variableID, lineColor, false, true, myRectangle);
        } else {
            Shape myCircle = new Ellipse2D.Float(-3, -3, 6, 6);
            addSeries(legend, plottableObject, lineColor, true, false, myCircle);

        }

    }

    /**
     * Build a series from a IFloatSource object, using the default variableId.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IFloatSource
     *                        interface.
     */
    public void addSeries(String legend, IFloatSource plottableObject) {
        sources.add(new FSource(legend, plottableObject, IFloatSource.Variables.Default));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series from a IFloatSource object.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IFloatSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     */
    public void addSeries(String legend, IFloatSource plottableObject,
            Enum<?> variableID) {
        sources.add(new FSource(legend, plottableObject, variableID));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series from a ILongSource object, using the default variableId.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the ILongSource
     *                        interface.
     */
    public void addSeries(String legend, ILongSource plottableObject) {
        sources.add(new LSource(legend, plottableObject, ILongSource.Variables.Default));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series from a ILongSource object.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IDblSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     */
    public void addSeries(String legend, ILongSource plottableObject,
            Enum<?> variableID) {
        sources.add(new LSource(legend, plottableObject, variableID));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series from a IIntSource object, using the default variableId.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IIntSource
     *                        interface.
     */
    public void addSeries(String legend, IIntSource plottableObject) {
        sources.add(new ISource(legend, plottableObject, IIntSource.Variables.Default));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series from a IIntSource object.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IIntSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     */
    public void addSeries(String legend, IIntSource plottableObject,
            Enum<?> variableID) {
        sources.add(new ISource(legend, plottableObject, variableID));
        // plot.addLegend(sources.size() - 1, legend);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
    }

    /**
     * Build a series from a generic object.
     * 
     * @param legend
     *                      The legend name of the series.
     * @param target
     *                      The data source object.
     * @param variableName
     *                      The variable or method name of the source object.
     * @param getFromMethod
     *                      Specifies if the variableName is a field or a method.
     */
    public void addSeries(String legend, Object target, String variableName,
            boolean getFromMethod) {
        Source source = null;
        if (ReflectionUtils.isDoubleSource(target.getClass(), variableName,
                getFromMethod))
            source = new DSource(legend, new DoubleInvoker(target,
                    variableName, getFromMethod), IDoubleSource.Variables.Default);
        else if (ReflectionUtils.isFloatSource(target.getClass(), variableName,
                getFromMethod))
            source = new FSource(legend, new FloatInvoker(target, variableName,
                    getFromMethod), IFloatSource.Variables.Default);
        else if (ReflectionUtils.isIntSource(target.getClass(), variableName,
                getFromMethod))
            source = new ISource(legend, new IntegerInvoker(target,
                    variableName, getFromMethod), IIntSource.Variables.Default);
        else if (ReflectionUtils.isLongSource(target.getClass(), variableName,
                getFromMethod))
            source = new LSource(legend, new LongInvoker(target, variableName,
                    getFromMethod), ILongSource.Variables.Default);
        else
            throw new IllegalArgumentException("The target object " + target
                    + " does not provide a value of a valid data type.");

        sources.add(source);
        XYSeries series = new XYSeries(legend);
        if (maxSamples > 0)
            series.setMaximumItemCount(maxSamples);
        dataset.addSeries(series);
        // plot.addLegend(sources.size() - 1, legend);
    }

    /**
     * Max samples parameters allow to define a maximum number of points.
     * When set the plotting window shifts automatically along with time.
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

    public XYLineAndShapeRenderer getRenderer() {
        return (XYLineAndShapeRenderer) plot.getRenderer();
    }

    public void setRenderer(XYLineAndShapeRenderer renderer) {
        plot.setRenderer(renderer);
    }

}
