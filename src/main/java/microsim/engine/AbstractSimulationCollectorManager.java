package microsim.engine;


public abstract class AbstractSimulationCollectorManager extends AbstractSimulationManager implements SimulationCollectorManager {

	private SimulationManager simulationManager;
	
	public AbstractSimulationCollectorManager(SimulationManager manager) {
		super();
		setManager(manager);
	}

	public void setManager(SimulationManager manager) {
		simulationManager = manager;
	}

	public SimulationManager getManager() {
		return simulationManager;
	}

}
