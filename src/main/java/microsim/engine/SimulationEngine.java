package microsim.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import microsim.data.ExperimentManager;
import microsim.data.db.Experiment;
import microsim.event.EventQueue;
import microsim.event.SystemEventType;
import microsim.exception.SimulationException;
import microsim.exception.SimulationRuntimeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

/**
 * The simulation engine. The engine keeps a reference to an EventQueue object to
 * manage temporal sequence of events. Every object of the running simulation
 * can schedule events at a specified time point and the engine will notify to
 * it at the right time. The SimEngine stores a list of windows created by
 * models. Using the addSimWindow() method each simulation windows is managed by
 * the engine. It is able to show windows destroyed by user. When the windows is
 * shown the engine put the windows in the location where it was when the
 * project document was saved to disk. The window size is stored, too.
 */
@Log public class SimulationEngine extends Thread {

	/** Set the delay time beetween two simulation steps, ms. */
	@Setter private int eventThresold = 0;

	@Setter @Getter private int currentRunNumber = 1;

	private Experiment currentExperiment = null;

	@Setter @Getter private String multiRunId = null;

	/**
	 * Return a reference to the current EventQueue.
	 *
	 * @return The event queue.
	 */
	@Getter private EventQueue eventQueue;
	private List<SimulationManager> models;
	private Map<String, SimulationManager> modelMap;
	private boolean modelBuild = false;

	/**
	 * Return a reference to the current Random generator.
	 *
	 * @return The current random generator.
	 */
	@Getter private static Random rnd;

	/**
	 * Return the current random seed.
	 *
	 * @return The current random seed.
	 */
	@Getter private long randomSeed;

	@Getter protected ArrayList<EngineListener> engineListeners;

	@Getter private boolean runningStatus = false;

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
	@Getter private boolean turnOffDatabaseConnection = false;
	
	/** 
	 * (Quando costruisco un modello se è disabilitato silent mode viene creato il db. Durante
	 * il run posso dinamicamente abilitare o disabilitare. Nel caso invece sia partito in turnOffDatabaseConnection
	 * il db non esiste e quindi il flag non può essere cambiato.) */
	@Getter private boolean turnOffDatabaseConnectionAvailable = true;
	
	@Setter @Getter private ClassLoader classLoader = null;
	
	private static SimulationEngine instance;	
		
	@Getter private Class<?> builderClass = null;
	
	@Setter @Getter private ExperimentBuilder experimentBuilder = null;

	/**
	 * Build a new SimEngine with the given time unit.
	 *
	 *            The time uint id. See the public constants in the SimTime
	 *            class.
	 */
	protected SimulationEngine() {
		eventQueue = new EventQueue();
		models = new ArrayList<>();
		modelMap = new HashMap<>();
		randomSeed = System.currentTimeMillis();
		rnd = new RandomNumberGenerator(randomSeed);
		engineListeners = new ArrayList<>();
		
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
	static class RandomNumberGenerator extends Random implements RandomGenerator {

		@Serial private static final long serialVersionUID = 5942825728562046996L;

		RandomNumberGenerator(long seed) {
			super(seed);
		}
		
		@Override public void setSeed(int seed) {
			setSeed((long)seed);
		}

		@Override public void setSeed(int[] seed) {
			throw new RuntimeException("SimulationEngine's RandomNumberGenerator class "
					+ "is derived from the Java.util.Random class, which doesn't "
					+ "implement a constructor taking an int[] argument.  This method "
					+ "should not be used!\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}
	}

	public void setTurnOffDatabaseConnection(boolean turnOffDatabaseConnection) {
		if (turnOffDatabaseConnection && ! turnOffDatabaseConnectionAvailable)
			return;
		
		this.turnOffDatabaseConnection = turnOffDatabaseConnection;
		ExperimentManager.getInstance().saveExperimentOnDatabase = ! turnOffDatabaseConnection;
	}

	@Deprecated
	public void setBuilderClass(Class<?> builderClass) {
		if (! ExperimentBuilder.class.isAssignableFrom(builderClass)) 
			throw new RuntimeException(builderClass + " does not implement ExperimentBuilder interface!");
		
		this.builderClass = builderClass;
	}

	public static @NotNull SimulationEngine getInstance() {
		return instance == null ? new SimulationEngine() : instance;
	}

	public SimulationManager getManager(String id) {
		return modelMap.get(id);
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

	public void setup() {
		if (builderClass != null)
			try {
				((ExperimentBuilder) builderClass.getDeclaredConstructor().newInstance()).buildExperiment(this);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				log.log(Level.SEVERE, e.getMessage());
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
	 * Return a reference to the current SimTime.
	 * 
	 * @return The current time object.
	 */
	public double getTime() {
		return eventQueue.getTime();
	}

	/**
	 * Make forSteps simulation steps.
	 * 
	 * @param forSteps
	 *            The number of steps to be done.
	 * @throws SimulationException //TODO finish this
	 */
	public void step(int forSteps) throws SimulationException {
		for (int i = 0; i < forSteps; i++)
			step();
	}
	
	public void reset() {
		pause();
		eventQueue = new EventQueue();
		models = new ArrayList<>();
		modelMap = new HashMap<>();
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
		for (SimulationManager model : models) model.dispose();
		models.clear();
		notifySimulationListeners(SystemEventType.Shutdown);
		System.exit(0);
	}

	/**
	 * Notify the engine to manage a SimModel. This method is mandatory to let a
	 * model work. The current event queue is joined to the given model.
	 *
	 *            The model to be added.
	 */
	public SimulationManager addSimulationManager(SimulationManager simulationManager) {
		modelMap.put(simulationManager.getId(), simulationManager);
		models.add(simulationManager);
		simulationManager.setEngine(this);
		
		return simulationManager;
	}

	public SimulationManager addSimulationManager(String managerClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SimulationManager simulationManager;
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
			currentExperiment = ExperimentManager.getInstance()
							.setupExperiment(currentExperiment, models.toArray(new SimulationManager[models.size()]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (modelBuild)
			return;

		for (SimulationManager manager : models) {
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
			SimulationManager model = models.get(i);
			cls[i] = model.getClass();
			model.dispose();
		}
		models.clear();
		modelMap.clear();
		
		System.gc();

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
			case Stop -> {
				pause();
				for (SimulationManager model : models) model.dispose();
			}
			case Restart -> rebuildModels();
			case Shutdown -> quit();
			default -> {
			}
		}

		for (EngineListener engineListener : engineListeners) engineListener.onEngineEvent(actionType);
	}

	private synchronized void checkIdle() throws InterruptedException {
		while (!runningStatus)
			wait();
	}

	/** Set current simulation running status. */
	public void setRunningStatus(boolean running) {
		runningStatus = running;
		if (runningStatus)
			resumeRun();
	}

	private synchronized void resumeRun() {
		notify();
	}

	public synchronized void step() throws SimulationException {
		if (!modelBuild)
			buildModels();

		eventQueue.step();
		notifySimulationListeners(SystemEventType.Step);
		this.yield();
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
		 * while (true) { try { this.yield(); if (EVENT_TRESHOLD > 0)
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
					log.log(Level.SEVERE, "Interrupt: " + e.getMessage());
				}
			// this is now called in step() method.
			else
				this.yield();
		}

	}

	public Experiment getCurrentExperiment() {
		return currentExperiment == null ? new Experiment() : currentExperiment;
	}
}