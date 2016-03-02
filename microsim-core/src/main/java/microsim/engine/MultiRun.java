package microsim.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import microsim.data.ExperimentManager;
import microsim.data.ParameterDomain;
import microsim.event.SystemEventType;

/**
 * MultiRun is a template abstract class useful to guide the modeller to build
 * an automatic simulation launcher, able to change interactively parameters on
 * the basis of the last run.<br>
 * The best way to understand hot it works is to see the MultiRun example in the
 * JAS/examples directory. <br>
 * <br>
 * The key methods of multi run are <i>startModel()</i> and <i>nextModel()</i>.
 * The first one must create the simulation model(s), set its(their) parameters
 * a vector containing them. <br>
 * The jas engine will attach the returning list of model and execute the
 * simulation. When the SIMULATION_END signal is sent to Sim.engine the multi
 * run will execute the <i>nextModel()</i> method. Here user can observe the
 * result of the last run and decide which parameters to use at next run. If
 * method returns true another simulation run will be executed and the
 * <i>startModel()</i> method will be called again, otherwise the progam will
 * exit.
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright: Copyright (C) 2002 Michele Sonnessa
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Michele Sonnessa
 */

public abstract class MultiRun extends Thread implements EngineListener, ExperimentBuilder {

	protected SimulationEngine engine;
	protected int counter;
	protected boolean executionActive, toBeContinued;
	protected static boolean copyInputFolderStructure = false;

	private ExperimentBuilder experimentBuilder;
		
	private List<MultiRunListener> multiRunListeners;
	
	private List<EngineListener> engineListeners;
	
	private String multiRunId = null;
	
	private List<ParameterDomain> parameterDomains = null;
	
	/**
	 * Create a new multi run session.
	 * 
	 * @param title
	 *            is the title of the multi run control panel (see MultiRunFrame
	 *            API).
	 * @param maxRuns
	 *            is the length of the progress bar. It is not so important. It
	 *            has only a simbolic meaning.
	 */
	public MultiRun() {		
		counter = 0;
		
		parameterDomains = new ArrayList<ParameterDomain>();

		executionActive = false;
		toBeContinued = true;
		
		multiRunListeners = new ArrayList<MultiRunListener>();
		engineListeners = new ArrayList<EngineListener>();
		
//		multiRunId = "Run " + new Random().nextInt();		//Ross: Why is this Random?
//		multiRunId = "Run " + SimulationEngine.getRnd().nextInt();		//Ross: Why is this Random?
		multiRunId = "Run " + counter;
	}

	/**
	 * When a SIMULATION_END signal is sent to JAS by one of the running models,
	 * simulation is stopped and this method is called. If it returns true the
	 * multi run will continue with the next run, otherwise the program will
	 * exit.
	 * 
	 * @return a value deciding if simulation is to be continued.
	 */
	public abstract boolean nextModel();

	public abstract String setupRunLabel();
	
	/**
	 * MultiRun is an independent thread. The run method controls the sequence
	 * of simulations.
	 */
	public synchronized void run() {
		while (toBeContinued) {
			executionActive = true;
			go();
			while (executionActive)
				try {
					sleep(300);
				} catch (Exception e) {
					System.out.println("Interrupt: " + e.getMessage());
				}
		}

		System.exit(0);
	}

	/** The go method starts the multi-run simulation. */
	public synchronized void go() {
		counter++;
		
		engine = new SimulationEngine();
		
		if(counter==2) {		//After the first simulation (which by default copies the input files to the output folder), check the settings on whether to copy input files to new folder for each simulation run
			ExperimentManager.getInstance().copyInputFolderStructure = copyInputFolderStructure;		//By default do not copy the input folder for each simulation run after the first simulation.
		}
		engine.setCurrentRunNumber(counter);
		engine.setMultiRunId(setupRunLabel());
		engine.addEngineListener(this);
		if (experimentBuilder != null)
			engine.setExperimentBuilder(experimentBuilder);
		else
			engine.setExperimentBuilder(this);
		
		for (EngineListener engineListener : engineListeners) 
			engine.addEngineListener(engineListener);
		
		engine.setup();
		
		if (multiRunListeners != null)
			for (MultiRunListener listener : multiRunListeners) 
				listener.beforeSimulationStart(engine);
			
		engine.startSimulation();
		
	}

	/**
	 * Implementing the ISimEngineListener. This method monitors the
	 * Sim.EVENT_SIMULATION_END signal. When it is raised the MultiRun class
	 * shutdowns current run and invokes the <i>nextModel()</i> method.
	 * 
	 * @param actionType
	 *            a valid system eventID.
	 */
	public void onEngineEvent(SystemEventType event) {
		if (event.equals(SystemEventType.End)) {
			if (multiRunListeners != null)
				for (MultiRunListener listener : multiRunListeners) 
					listener.afterSimulationCompleted(engine);
			
			yield();
			toBeContinued = nextModel();
			engine.disposeModels();
			executionActive = false;
			engine = null;
		}
	}

	public SimulationEngine getEngine() {
		return engine;
	}

	public int getCounter() {
		return counter;
	}

	public ExperimentBuilder getExperimentBuilder() {
		return experimentBuilder;
	}

	public void setExperimentBuilder(ExperimentBuilder experimentBuilder) {
		this.experimentBuilder = experimentBuilder;
	}

	public List<MultiRunListener> getMultiRunListeners() {
		return multiRunListeners;
	}

	public void setMultiRunListeners(List<MultiRunListener> multiRunListeners) {
		this.multiRunListeners = multiRunListeners;
	}

	public String getMultiRunId() {
		return multiRunId;
	}

	public void setMultiRunId(String multiRunId) {
		this.multiRunId = multiRunId;
	}

	public List<EngineListener> getEngineListeners() {
		return engineListeners;
	}

	public void setEngineListeners(List<EngineListener> engineListeners) {
		this.engineListeners = engineListeners;
	}

	public List<ParameterDomain> getParameterDomains() {
		return parameterDomains;
	}

	public void setParameterDomains(List<ParameterDomain> parameterDomains) {
		this.parameterDomains = parameterDomains;
	}
	
	public MultiRun addParameterDomain(ParameterDomain parameterDomain) {
		parameterDomains.add(parameterDomain);
		return this;
	}

	public int getMaxConfigurations() {
		int maxCounter = 1;
		
		for (int i = 0; i < parameterDomains.size(); i++) {
			ParameterDomain parameterDomain = parameterDomains.get(i);
			maxCounter *= parameterDomain.getValues().length;
		}
		
		return maxCounter;
	}
	
	public Map<String, Object> getConfiguration(int counter) {
		
		HashMap<String, Object> current = new HashMap<String, Object>();
		for (int i = 0; i < parameterDomains.size(); i++) {
			ParameterDomain parameterDomain = parameterDomains.get(i);
			
			int residual = 1;
			for (int j = i + 1; j < parameterDomains.size(); j++) 
				residual *= parameterDomains.get(j).getValues().length;
			
			int idx = counter / residual;
			current.put(parameterDomain.getName(), parameterDomain.getValues()[idx]);
			counter -= residual * idx;
			
		}
		
		return current;
	}

	/**
	 * Sets whether to copy the input files into a new input folder within a new output folder for each simulation run
	 * @param copyInputFolderStructure - set to true if wanting a copy of the input files to be stored in the output folder for each simulation run, otherwise set to false
	 * @author richardsonr
	 */
	public static void setCopyInputFolderStructure(boolean copyInputFolderStructure_) {
		copyInputFolderStructure = copyInputFolderStructure_;
	}

	
}