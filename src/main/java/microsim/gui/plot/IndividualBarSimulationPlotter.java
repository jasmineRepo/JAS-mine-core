package microsim.gui.plot;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;

import javax.swing.JInternalFrame;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.gui.colormap.ColorMap;
import microsim.gui.colormap.FixedColorMap;
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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * A bar chart plotter showing elements manually added by user. It is based on
 * JFreeChart library. It is compatible with the microsim.statistics.* classes.
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002-13 Michele Sonnessa
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
 * @author Michele Sonnessa
 */
public class IndividualBarSimulationPlotter extends JInternalFrame implements EventListener {

    private static final long serialVersionUID = 1L;

    private ArrayList<Source> sources;
    private ArrayList<String> categories;

    private DefaultCategoryDataset dataset;

    private BarRenderer renderer;

    private String yaxis;

    private FixedColorMap colorMap;

    public IndividualBarSimulationPlotter(String title, String yaxis) {
        super();
        this.setResizable(true);
        this.setTitle(title);
        this.yaxis = yaxis;
        colorMap = new FixedColorMap();

        sources = new ArrayList<Source>();
        categories = new ArrayList<String>();

        dataset = new DefaultCategoryDataset();

        final JFreeChart chart = ChartFactory.createBarChart(
                title, // chart title
                "Categories", // x axis label
                yaxis, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only... Ross: Why???
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        // rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

        // disable bar outlines...
        // renderer = (BarRenderer) plot.getRenderer();
        renderer = new ColoredBarRenderer(colorMap);
        plot.setRenderer(renderer);
        renderer.setDrawBarOutline(false);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

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
        for (int i = 0; i < sources.size(); i++) {
            Source source = sources.get(i);
            double d = source.getDouble();
            String category = categories.get(i);

            dataset.addValue(d, yaxis, category);
        }
    }

    private abstract class Source {
        // public String label;
        public Enum<?> vId;
        protected boolean isUpdatable;

        public abstract double getDouble();

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
    public void addSources(String legend, IDoubleSource plottableObject) {
        sources.add(new DSource(legend, plottableObject, IDoubleSource.Variables.Default));
        // set up gradient paints for series...
        categories.add(legend);
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
    public void addSources(String legend, Object target, String variableName,
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

    }

    // --------------------------------------------------------------------------
    //
    // Methods to specify colour of each bar (source)
    //
    // --------------------------------------------------------------------------

    /**
     * Build a series retrieving data from a IDoubleSource object, using the
     * default variableId and specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IDoubleSource
     *                        interface.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, IDoubleSource plottableObject, Color color) {
        addSources(legend, plottableObject);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series retrieving data from a IDoubleSource object and specifying the
     * colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IDoubleSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, IDoubleSource plottableObject,
            Enum<?> variableID, Color color) {
        sources.add(new DSource(legend, plottableObject, variableID));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a IFloatSource object, using the default variableId and
     * specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IFloatSource
     *                        interface.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, IFloatSource plottableObject, Color color) {
        sources.add(new FSource(legend, plottableObject, IFloatSource.Variables.Default));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a IFloatSource object and specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IFloatSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, IFloatSource plottableObject,
            Enum<?> variableID, Color color) {
        sources.add(new FSource(legend, plottableObject, variableID));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a ILongSource object, using the default variableId and
     * specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the ILongSource
     *                        interface.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, ILongSource plottableObject, Color color) {
        sources.add(new LSource(legend, plottableObject, ILongSource.Variables.Default));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a ILongSource object and specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IDblSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, ILongSource plottableObject,
            Enum<?> variableID, Color color) {
        sources.add(new LSource(legend, plottableObject, variableID));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a IIntSource object, using the default variableId and
     * specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IIntSource
     *                        interface.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, IIntSource plottableObject, Color color) {
        sources.add(new ISource(legend, plottableObject, IIntSource.Variables.Default));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a IIntSource object and specifying the colour.
     * 
     * @param legend
     *                        The legend name of the series.
     * @param plottableObject
     *                        The data source object implementing the IIntSource
     *                        interface.
     * @param variableID
     *                        The variable id of the source object.
     * @param color
     *                        Specifies the color of the bar
     */
    public void addSources(String legend, IIntSource plottableObject,
            Enum<?> variableID, Color color) {
        sources.add(new ISource(legend, plottableObject, variableID));
        categories.add(legend);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    /**
     * Build a series from a generic object and specifying the colour.
     * 
     * @param legend
     *                      The legend name of the series.
     * @param target
     *                      The data source object.
     * @param variableName
     *                      The variable or method name of the source object.
     * @param getFromMethod
     *                      Specifies if the variableName is a field or a method.
     * @param color
     *                      Specifies the color of the bar
     */
    public void addSources(String legend, Object target, String variableName,
            boolean getFromMethod, Color color) {
        addSources(legend, target, variableName, getFromMethod);
        int seriesNum = sources.size() - 1; // Start with value of 0
        colorMap.addColor(seriesNum, color);
    }

    class ColoredBarRenderer extends BarRenderer {

        private static final long serialVersionUID = -7678490515617294057L;

        private FixedColorMap colormap;

        ColoredBarRenderer(FixedColorMap colormap) {
            this.colormap = colormap;
        }

        public Paint getItemPaint(final int row, final int column) {
            // returns color for each column
            return (colormap.getColor(column));
            // return (colormap.getMappedColor(column));
        }
    }
}
