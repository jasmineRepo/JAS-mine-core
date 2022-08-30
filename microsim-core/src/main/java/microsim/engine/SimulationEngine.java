package microsim.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import microsim.data.ExperimentManager;
import microsim.data.db.Experiment;
import microsim.event.EventQueue;
import microsim.event.SystemEventType;
import microsim.exception.SimulationException;
import microsim.exception.SimulationRuntimeException;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

/**
 * The simulation engine. The engine keeps a reference to an EventQueue object to
 * manage temporal sequence of events. Every object of the running simulation
 * can schedule events at a specified time point and the engine will notify to
 * it at the right time. The SimEngine stores a list of windows created by
 * models. Using the addSimWindow() method each simulation windows is managed by
 * the engine. It is able to show windows destroyed by user. When the windows is
 * shown the engine put the windows in the location where it was when the
 * project document was saved to disk. The window size is stored, too.
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
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 * 
 * @author Michele Sonnessa 
 *         <p>
 */
public class SimulationEngine extends Thread {

	private static Logger log = Logger.getLogger(SimulationEngine.class);
	
	private int eventThresold = 0;

	private int currentRunNumber = 1;

	private Experiment currentExperiment = null;

	private String multiRunId = null;
	
	/**
	 * @supplierCardinality 1
	 */
	private EventQueue eventQueue;
	private List<SimulationManager> models;
	private Map<String, SimulationManager> modelMap;
	private boolean modelBuild = false;

	private static Random rnd;
	private long randomSeed;

	protected ArrayList<EngineListener> engineListeners;

	private boolean runningStatus = false;

	/** If set to true during the build phase of a simulation, 
	 * the simulation run will not be connected to an input / output database.
	 * This may speed up the building and execution of the simulation, however the relational 
	 * database management features provided by JAS-mine cannot then be used and data cannot 
	 * be persisted to the output database. Any data should be exported to CSV files instead.  
	 * If an attempt is made to import data from an input database during the simulation, an
	 * exception will be thrown. 
	 * 
	 * In older versions of JAS-mine, it was possible to control this field on the fly via the 
	 * JAS-mine GUI, so all data persistence could be enabled or disabled during the simulation.  
	 * This was only possible when the turnOffDatabaseConnection was disabled (set to false) during the 
	 * build phase of the simulation (before execution of the simulation).  However, this option has now
	 * been removed from the GUI to avoid misuse by inexperienced users who might attempt to 
	 * import / export data from / from the database after building the simulation with 
	 * turnOffDatabaseConnection set to true, which, as the database connection is not established, 
	 * will not work and may result in an exception being thrown.
	 * 
	 * It is still possible to set this field programmatically, for example, in the Start class of 
	 * a standard JAS-mine project (e.g. created using the JAS-mine Plugin for Eclipse IDE),
	 * using the Simulation Engine's setTurnOffDatabaseConnection() method. */
	private boolean turnOffDatabaseConnection = false;
	
	/** 
	 * (Quando costruisco un modello se è disabilitato silent mode viene creato il db. Durante
	 * il run posso dinamicamente abilitare o disabilitare. Nel caso invece sia partito in turnOffDatabaseConnection
	 * il db non esiste e quindi il flag non può essere cambiato.) */
	private boolean turnOffDatabaseConnectionAvailable = true;
	
	private ClassLoader classLoader = null;
	
	private static SimulationEngine instance;	
		
	private Class<?> builderClass = null;
	
	private ExperimentBuilder experimentBuilder = null;
	
	/**
	 * @link dependency
	 * @stereotype use
	 * @supplierRole 1..
	 **/
	/* #SimModel lnkSimModel; */

	/**
	 * Build a new SimEngine with the given time unit.
	 */
	protected SimulationEngine() {
		eventQueue = new EventQueue();
		models = new ArrayList<SimulationManager>();
		modelMap = new HashMap<String, SimulationManager>();
		randomSeed = System.currentTimeMillis();
//		rnd = new Random(randomSeed);
		rnd = new RandomNumberGenerator(randomSeed);
		engineListeners = new ArrayList<EngineListener>();
		
		instance = this;
	}
	
	/* This class enables the construction of an apache commons math3
	 * MultivariateNormalDistribution class that uses the SimulationEngine's rnd object.
	 * 
	 * RandomNumberGenerator is basically the Java.util.Random object as previously, 
	 * however now it implements the RandomGenerator interface from apache commons math3,
	 * which is compatible with Java.util.Random
	 * 
	 * @author Ross Richardson
	 *
	 */
	class RandomNumberGenerator extends Random implements RandomGenerator {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5942825728562046996L;

		RandomNumberGenerator(long seed) {
			super(seed);
		}
		
		@Override
		public void setSeed(int seed) {
			setSeed((long)seed);
			
		}

		@Override
		public void setSeed(int[] seed) {
			throw new RuntimeException("SimulationEngine's RandomNumberGenerator class "
					+ "is derived from the Java.util.Random class, which doesn't "
					+ "implement a constructor taking an int[] argument.  This method "
					+ "should not be used!\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}
		
		
	}

	public boolean isTurnOffDatabaseConnection() {
		return turnOffDatabaseConnection;
	}

	public void setTurnOffDatabaseConnection(boolean turnOffDatabaseConnection) {
		if (turnOffDatabaseConnection && ! turnOffDatabaseConnectionAvailable)
			return;
		
		this.turnOffDatabaseConnection = turnOffDatabaseConnection;
//		ExperimentManager.getInstance().copyInputFolderStructure = ! turnOffDatabaseConnection;
		ExperimentManager.getInstance().saveExperimentOnDatabase = ! turnOffDatabaseConnection;
	}

	public Class<?> getBuilderClass() {
		return builderClass;
	}

	public ExperimentBuilder getExperimentBuilder() {
		return experimentBuilder;
	}

	public void setExperimentBuilder(ExperimentBuilder experimentBuilder) {
		this.experimentBuilder = experimentBuilder;
	}

	public boolean isTurnOffDatabaseConnectionAvailable() {
		return turnOffDatabaseConnectionAvailable;
	}

	@Deprecated
	public void setBuilderClass(Class<?> builderClass) {
		if (! ExperimentBuilder.class.isAssignableFrom(builderClass)) 
			throw new RuntimeException(builderClass + " does not implement ExperimentBuilder interface!");
		
		this.builderClass = builderClass;
	}

	public static SimulationEngine getInstance() {
		if (instance == null)
			instance = new SimulationEngine();
		
		return instance;
	}
	
	public int getCurrentRunNumber() {
		return currentRunNumber;
	}

	public void setCurrentRunNumber(int currentRunNumber) {
		this.currentRunNumber = currentRunNumber;
	}

	public Experiment getCurrentExperiment() {
		return currentExperiment;
	}

	public SimulationManager getManager(String id) {
		return modelMap.get(id);
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Install a listener for events generated by the simulation engine.
	 * 
	 * @param engineListener
	 *            An object implementing the ISimEngineListener interface.
	 */
	public void addEngineListener(EngineListener engineListener) {
		engineListeners.add(engineListener);
	}

	public void removeEngineListener(EngineListener engineListener) {
		engineListeners.remove(engineListener);
	}

	public ArrayList<EngineListener> getEngineListeners() {
		return engineListeners;
	}

	public void setup() {
		if (builderClass != null)
			try {
				((ExperimentBuilder) builderClass.newInstance()).buildExperiment(this);
			} catch (InstantiationException e) {
				log.error(e.getMessage());
			} catch (IllegalAccessException e) {
				log.error(e.getMessage());
			}
		else if (experimentBuilder != null)
			experimentBuilder.buildExperiment(this);
		
		notifySimulationListeners(SystemEventType.Setup);
	}
	
	/**
	 * Return an array representing the running SimModels.
	 * 
	 * @return A list of running models.
	 */
	public SimulationManager[] getModelArray() {
		return models.toArray(new SimulationManager[] {});
	}

	/**
	 * Return a reference to the current EventQueue.
	 * 
	 * @return The event queue.
	 */
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	/**
	 * Return a reference to the current SimTime.
	 * 
	 * @return The current time object.
	 */
	public double getTime() {
		return eventQueue.getTime();
	}

	/**
	 * Return a reference to the current Random generator.
	 * 
	 * @return The current random generator.
	 */
	public static Random getRnd() {
		return rnd;
	}

	/**
	 * Make forSteps simulation steps.
	 * 
	 * @param forSteps
	 *            The number of steps to be done.
	 * @throws SimulationException
	 */
	public void step(int forSteps) throws SimulationException {
		for (int i = 0; i < forSteps; i++)
			step();
	}
	
	public void reset() {
		pause();
		eventQueue = new EventQueue();
		models = new ArrayList<SimulationManager>();
		modelMap = new HashMap<String, SimulationManager>();
		randomSeed = System.currentTimeMillis();
		rnd = new RandomNumberGenerator(randomSeed);		
	}

	/**
	 * Start simulation. A new thread starts and calls step() method until
	 * something stops it.
	 */
	public void startSimulation() {
		if (!isAlive())
			start();

		if (!modelBuild)
			buildModels();

		setRunningStatus(true);
		
		notifySimulationListeners(SystemEventType.Start);
	}

	/**
	 * Stop simulation. The running thread is freezed until next step is called.
	 */
	public void pause() {
		setRunningStatus(false);
		
		notifySimulationListeners(SystemEventType.Stop);
	}

	/** Stop the simulation, dispose everything and the quit the JVM. */
	public void quit() {
		pause();
		eventQueue = null;
		for (SimulationManager model : models) {
			model.dispose();
		}
		models.clear();
		notifySimulationListeners(SystemEventType.Shutdown);
		System.exit(0);
	}

	/**
	 * Notify the engine to manage a SimModel. This method is mandatory to let a
	 * model work. The current event queue is joined to the given model.
	 * 
	 * @param simulationManager
	 *            The simulationManager to be added.
	 */
	public SimulationManager addSimulationManager(SimulationManager simulationManager) {
		modelMap.put(simulationManager.getId(), simulationManager);
		models.add(simulationManager);
		simulationManager.setEngine(this);
		
		return simulationManager;
	}

	public SimulationManager addSimulationManager(String managerClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SimulationManager simulationManager = null;
		if (classLoader != null)
			simulationManager = (SimulationManager) classLoader.loadClass(managerClassName).newInstance();
		else
			simulationManager = (SimulationManager) Class.forName(managerClassName).newInstance();
		return addSimulationManager(simulationManager);
	}
	
	/** Call the buildModel() method of each active SimModel. */
	public void buildModels() {
		currentExperiment = ExperimentManager.getInstance().createExperiment(multiRunId);
		
		turnOffDatabaseConnectionAvailable = (! turnOffDatabaseConnection);
		
		notifySimulationListeners(SystemEventType.Build);
		
		try {			
			currentExperiment = ExperimentManager.getInstance().setupExperiment(currentExperiment, models.toArray(new SimulationManager[models.size()]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (modelBuild)
			return;
		
		Iterator<SimulationManager> it = models.iterator();
		while (it.hasNext()) {
			final SimulationManager manager = it.next();
			manager.buildObjects();
			manager.buildSchedule();
		}

		modelBuild = true;
	}

	/**
	 * Return true if buildModels() method has been called. False otherwise.
	 * 
	 * @return True is models have been built, false otherwise.
	 */
	public boolean getModelBuildStatus() {
		return modelBuild;
	}

	/**
	 * Dispose from memory all running models. Return an array representing the
	 * Class of each disposed models. It is used by rebuildModels().
	 * 
	 * @return The list of disposed models.
	 */
	public synchronized Class<?>[] disposeModels() {
		eventQueue.clear();

		modelBuild = false;

		// Get models' class type and dispose
		Class<?>[] cls = new Class[models.size()];
		for (int i = 0; i < models.size(); i++) {
			SimulationManager model = (SimulationManager) models.get(i);
			cls[i] = model.getClass();
			model.dispose();
		}
		models.clear();
		modelMap.clear();
		
		System.gc();
//		currentRunNumber = 0;		//Ross: Why do we need to do this?

		turnOffDatabaseConnectionAvailable = true;
		
		return cls;
	}

	/**
	 * Dispose and rebuild each running model. It is used to restart simulation.
	 */
	public void rebuildModels() {
		int k = currentRunNumber;
		disposeModels();
		currentRunNumber = k + 1;

		eventQueue.clear();
		
		setRandomSeed(randomSeed);
		
		notifySimulationListeners(SystemEventType.Restart);
		setup();
	}

	/**
	 * Return the current random seed.
	 * 
	 * @return The current random seed.
	 */
	public long getRandomSeed() {
		return randomSeed;
	}

	/**
	 * Set the current random seed.
	 * 
	 * @param newSeed
	 *            The new random seed.
	 */
	public void setRandomSeed(long newSeed) {
		rnd.setSeed(newSeed);
		randomSeed = newSeed;
	}

	/**
	 * Stops the simulation and call the simulationEnd method of each running
	 * model.
	 */
	public void end() {
		pause();
		eventQueue.clear();
		performAction(SystemEventType.End);
	}

	/**
	 * React to system events.
	 * 
	 * @param actionType
	 *            Reacts in case of EVENT_SIMULATION_END,
	 *            EVENT_SIMULATION_RESTART, EVENT_SHUTDOWN events.
	 */
	public void performAction(SystemEventType actionType) {
		switch (actionType) {
			case Stop:
				pause();
				Iterator<SimulationManager> it = models.iterator();
				while (it.hasNext())
					it.next().dispose();
				break;
			case Restart:
				rebuildModels();
				break;
			case Shutdown:
				quit();
				break;
			default:
				break;
		}

		for (ListIterator<EngineListener> it = engineListeners.listIterator(); it
				.hasNext();)
			it.next().onEngineEvent(actionType);
	}

	private synchronized void checkIdle() throws InterruptedException {
		while (!runningStatus)
			wait();
	}

	/** Return current simulation running status. */
	public boolean getRunningStatus() {
		return runningStatus;
	}

	/** Set current simulation running status. */
	public void setRunningStatus(boolean running) {
		runningStatus = running;
		if (runningStatus)
			resumeRun();
	}

	/** Set the delay time beetween two simulation steps. */
	public void setEventTimeTreshold(int millis) {
		eventThresold = millis;
	}

	private synchronized void resumeRun() {
		notify();
	}

	public synchronized void step() throws SimulationException {
		if (!modelBuild)
			buildModels();

		eventQueue.step();
		notifySimulationListeners(SystemEventType.Step);
		yield();		
	}

	protected synchronized void notifySimulationListeners(SystemEventType event) {
		if (engineListeners != null)
			for (EngineListener listener : engineListeners) {
				listener.onEngineEvent(event);
			}
	}
	
	/**
	 * Start the independent thread running simulation. It fire events only if
	 * running status is set to true.
	 */
	public void run() {
		// System.out.println("JAS enigne started at " + System.);
		/*
		 * while (true) { try { yield(); if (EVENT_TRESHOLD > 0)
		 * sleep(EVENT_TRESHOLD); } catch (Exception e) {
		 * System.out.println("Interrupt: " + e.getMessage()); }
		 * 
		 * if (runningStatus) step(); }
		 */
		while (true) {
			try {
				checkIdle();
			} catch (Exception e) {
			}

			try {
				step();
			} catch (SimulationException e1) {
				throw new SimulationRuntimeException(e1);
			}

			if (eventThresold > 0)
				try {
					sleep(eventThresold);
				} catch (Exception e) {
					log.error("Interrupt: " + e.getMessage());
				}
			// this is now called in step() method.
			else
				yield();
		}

	}
	
	public Random getRandom() {
//		return new Random(rnd.nextLong());
		return rnd;
	}

	public String getMultiRunId() {
		return multiRunId;
	}

	public void setMultiRunId(String multiRunId) {
		this.multiRunId = multiRunId;
	}

}