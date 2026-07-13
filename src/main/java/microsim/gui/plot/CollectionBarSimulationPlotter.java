package microsim.gui.plot;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JInternalFrame;

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
 *         <p>
 */
public class CollectionBarSimulationPlotter extends JInternalFrame implements EventListener {

    private static final long serialVersionUID = 1L;

    private ArrayList<ArraySource> sources;
    private ArrayList<String> categories;

    private DefaultCategoryDataset dataset;

    private BarRenderer renderer;

    private Integer maxBars;

    private abstract class ArraySource {
        // public String label;
        protected boolean isUpdatable;

        public abstract double[] getDoubleArray();

    }

    private class DArraySource extends ArraySource {
        public IDoubleArraySource source;

        public DArraySource(String label, IDoubleArraySource source) {
            // super.label = label;
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
            // super.label = label;
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
            // super.label = label;
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
            // super.label = label;
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

    public CollectionBarSimulationPlotter(String title, String yaxis) {
        super();
        this.setResizable(true);
        this.setTitle(title);

        sources = new ArrayList<ArraySource>();
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
        renderer = (BarRenderer) plot.getRenderer();
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
            var cs = sources.get(i);
            final String category = categories.get(i);

            double[] vals = cs.getDoubleArray();
            for (int j = 0; j < vals.length && (j < (maxBars == null ? Integer.MAX_VALUE : maxBars)); j++)
                dataset.addValue(vals[j], category, "" + j);
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
        categories.add(name);
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
        categories.add(name);
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
        categories.add(name);
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
        categories.add(name);
    }

    public Integer getMaxBars() {
        return maxBars;
    }

    public void setMaxBars(Integer maxBars) {
        this.maxBars = maxBars;
    }

}
