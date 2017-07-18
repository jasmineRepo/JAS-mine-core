package microsim.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import microsim.engine.SimulationEngine;
import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.reflection.ReflectionUtils;

import org.apache.log4j.Logger;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.FloatArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.list.LongArrayList;

/**
 * It is a collection of series (data panel). It contains more series synching
 * their time.
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
 * 59 Temple 8Place, Suite 330, Boston, MA 02111-1307, USA.
 * 
 * @author Michele Sonnessa
 *         <p>
 */
public class TimeSeries implements EventListener, IUpdatableSource {
	private static Logger log = Logger.getLogger(TimeSeries.class);

	/** A custom event identifier for perfomAction method. Save to disk. */
	public static final int EVENT_SAVE = 1;
	/** The character used to separate data in the output file. */
	public static final char DEFAULT_SEPARATOR = ',';

	private String fileName = "timeSeries.txt";

	private double lastTimeUpdate = -1.;

	protected ArrayList<Series> series;
//	protected LongArrayList absTimes;
	protected DoubleArrayList absTimes;
	protected ArrayList<String> descTimes;

	/** Create a new time series container. */
	public TimeSeries() {
		series = new ArrayList<Series>();
		absTimes = new DoubleArrayList();
		descTimes = new ArrayList<String>();
	}

	/**
	 * Set the output file name.
	 * 
	 * @param fileName
	 *            A valid path for the output file name.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Return the current output file name.
	 * 
	 * @return The output file name.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Add a new series.
	 * 
	 * @param aSeries
	 *            An instance of the SeriesStats class.
	 * @throws IllegalArgumentException
	 *             If the series name already exists.
	 */
	public void addSeries(Series aSeries) {
		series.add(aSeries);
	}

	/**
	 * Add a new series.
	 * 
	 * @param name
	 *            Name of the statistic object.
	 * @param source
	 *            The IDoubleSource object.
	 * @param valueID
	 *            The value identifier defined by source object.
	 */
	public void addSeries(String name, IDoubleSource source, Enum<?> valueID) {
		series.add(new Series.Double(source, valueID));
	}

	/**
	 * Add a new series.
	 * 
	 * @param name
	 *            Name of the statistic object.
	 * @param source
	 *            The IFloatSource object.
	 * @param valueID
	 *            The value identifier defined by source object.
	 */
	public void addSeries(String name, IFloatSource source, Enum<?> valueID) {
		series.add(new Series.Float(source, valueID));
	}

	/**
	 * Add a new series.
	 * 
	 * @param name
	 *            Name of the statistic object.
	 * @param source
	 *            The IIntSource object.
	 * @param valueID
	 *            The value identifier defined by source object.
	 */
	public void addSeries(String name, IIntSource source, Enum<?> valueID) {
		series.add(new Series.Integer(source, valueID));
	}

	/**
	 * Add a new series.
	 * 
	 * @param name
	 *            Name of the statistic object.
	 * @param source
	 *            The ILongSource object.
	 * @param valueID
	 *            The value identifier defined by source object.
	 */
	public void addSeries(String name, ILongSource source, Enum<?> valueID) {
		series.add(new Series.Long(source, valueID));
	}

	/**
	 * Add a new series.
	 * 
	 * @param target
	 *            A generic source object.
	 * @param variableName
	 *            The name of the field or the method returning the variable to
	 *            be probed.
	 * @param getFromMethod
	 *            Specifies if valueName is a method or a property value.
	 */
	public void addSeries(Object target, String variableName,
			boolean getFromMethod) {
		Series aSeries = null;
		if (ReflectionUtils.isDoubleSource(target.getClass(), variableName,
				getFromMethod))
			aSeries = new Series.Double(target, variableName, getFromMethod);
		else if (ReflectionUtils.isFloatSource(target.getClass(), variableName,
				getFromMethod))
			aSeries = new Series.Float(target, variableName, getFromMethod);
		else if (ReflectionUtils.isIntSource(target.getClass(), variableName,
				getFromMethod))
			aSeries = new Series.Integer(target, variableName, getFromMethod);
		else if (ReflectionUtils.isLongSource(target.getClass(), variableName,
				getFromMethod))
			aSeries = new Series.Long(target, variableName, getFromMethod);
		else
			throw new IllegalArgumentException(
					"The passed argument is not a valid number source");

		series.add(aSeries);
	}

	/** Update all the contained time series and the current time. */
	public void updateSource() {
		if (SimulationEngine.getInstance().getEventQueue().getTime() == lastTimeUpdate)
			return;

		Iterator<Series> it = series.iterator();
		while (it.hasNext())
			it.next().updateSource();

		absTimes.add(SimulationEngine.getInstance().getEventQueue().getTime());
		descTimes.add(""
				+ SimulationEngine.getInstance().getEventQueue().getTime());

		lastTimeUpdate = SimulationEngine.getInstance().getEventQueue()
				.getTime();
	}

	/**
	 * Return the list of contained time series.
	 * 
	 * @return An array list containing SeriesStats objects.
	 */
	public ArrayList<Series> getSeriesList() {
		return series;
	}

	/**
	 * Return a series at the given index.
	 * 
	 * @param seriesIndex
	 *            The name of the series.
	 * @return The asked series. Null if series does not exists.
	 * @throws IndexOutOfBoundsException
	 *             If seriesIndex is out of bounds.
	 */
	public Series getSeries(int seriesIndex) {
		if (seriesIndex >= series.size())
			throw new IndexOutOfBoundsException(seriesIndex
					+ " is out of max bound " + series.size());

		return (Series) series.get(seriesIndex);
	}

	/**
	 * Return the number of series.
	 * 
	 * @return The number of series.
	 */
	public int getSeriesCount() {
		return series.size();
	}

	/**
	 * Store the entire data content on the output file. It is used the default
	 * separator and the time description is stored with the absolute one.
	 */
	public void saveToFile() {
		saveToFile("", fileName, true, DEFAULT_SEPARATOR);
	}

	/**
	 * Store the entire data content on the given output file. It is used the
	 * default separator and the time description is stored with the absolute
	 * one.
	 * 
	 * @param path
	 *            The optional path string. Passing an empty string it is
	 *            ignored.
	 * @param fileName
	 *            The name of the output file.
	 */
	public void saveToFile(String path, String fileName) {
		saveToFile(path, fileName, true, DEFAULT_SEPARATOR);
	}

	/**
	 * Store the entire data content on the given output file. It is used the
	 * default separator.
	 * 
	 * @param path
	 *            The optional path string. Passing an empty string it is
	 *            ignored.
	 * @param fileName
	 *            The name of the output file.
	 * @param withTimes
	 *            If true time description is saved. Only absolute time is saved
	 *            if false.
	 */
	public void saveToFile(String path, String fileName, boolean withTimes) {
		saveToFile(path, fileName, withTimes, DEFAULT_SEPARATOR);
	}

	/**
	 * Store the entire data content on the given output file.
	 * 
	 * @param path
	 *            The optional path string. Passing an empty string it is
	 *            ignored.
	 * @param fileName
	 *            The name of the output file.
	 * @param withTimes
	 *            If true time description is saved. Only absolute time is saved
	 *            if false.
	 * @param separator
	 *            The character used to separate data.
	 * @throws IOException
	 *             In case of IO error.
	 */
	public void saveToFile(String path, String fileName, boolean withTimes,
			char separator) {

		if (SimulationEngine.getInstance().getCurrentRunNumber() != 0)
			fileName = getNumberedFile(fileName);

		path += File.separator + fileName;

		File file = new File(path);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			if (withTimes)
				out.write("real time" + separator);
			out.write("time" + separator);

			// String[] rowLabels = getSeriesNames();
			// for (int i = 0; i < rowLabels.length; i++)
			// out.write(rowLabels[i] + separator);
			// out.newLine();

			for (int i = 0; i < absTimes.size() - 1; i++) {
				if (withTimes)
					out.write(descTimes.get(i).toString() + separator);
				out.write("" + absTimes.get(i) + separator);

				Iterator<Series> it = series.iterator();
				while (it.hasNext()) {
					Series s = it.next();
					if (s instanceof Series.Double) {
						DoubleArrayList dl;
						dl = ((Series.Double) s).getDoubleArrayList();
						out.write("" + dl.get(i) + separator);
					} else if (s instanceof Series.Integer) {
						IntArrayList dl;
						dl = ((Series.Integer) s).getIntArrayList();
						out.write("" + dl.get(i) + separator);
					} else if (s instanceof Series.Float) {
						FloatArrayList dl;
						dl = ((Series.Float) s).getFloatArrayList();
						out.write("" + dl.get(i) + separator);
					} else {
						LongArrayList dl;
						dl = ((Series.Long) s).getLongArrayList();
						out.write("" + dl.get(i) + separator);
					}
					out.newLine();
				}
			}
			out.newLine();
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			log.error("Error saving " + path + ioe.getMessage());
		}
	}

	private String getNumberedFile(String fileName) {
		int index;
		if ((index = fileName.lastIndexOf(".")) == 0)
			return fileName + "_"
					+ SimulationEngine.getInstance().getCurrentRunNumber();
		else {
			String name = fileName.substring(0, index);
			String ext = fileName.substring(index);
			return name + "_"
					+ SimulationEngine.getInstance().getCurrentRunNumber()
					+ ext;
		}
	}

	/**
	 * Perform one of the defined actions.
	 * 
	 * @param actionType
	 *            One of the following actions:<br>
	 *            <i>Sim.EVENT_UPDATE</i> calls the <i>update()</i> method.<br>
	 *            <i>TimeSeries.EVENT_SAVE</i> calls the <i>saveToFile()</i>
	 *            method.<br>
	 * */
	public void onEvent(Enum<?> type) {
		if (type instanceof CommonEventType) {
			switch ((CommonEventType) type) {
			case Update:
				updateSource();
				break;
			case Save:
				saveToFile();
				break;
			}
		}
	}

}