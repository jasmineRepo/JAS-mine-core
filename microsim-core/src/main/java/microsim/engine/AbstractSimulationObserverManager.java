package microsim.engine;


public abstract class AbstractSimulationObserverManager extends AbstractSimulationCollectorManager implements SimulationObserverManager {

	private SimulationCollectorManager simulationCollectionManager;
	
	public AbstractSimulationObserverManager(SimulationManager manager, SimulationCollectorManager simulationCollectionManager) {
		super(manager);
		setCollectorManager(simulationCollectionManager);	
	}

	public void setCollectorManager(SimulationCollectorManager manager) {
		simulationCollectionManager = manager;
	}

	public SimulationCollectorManager getCollectorManager() {
		return simulationCollectionManager;
	}
	
}
