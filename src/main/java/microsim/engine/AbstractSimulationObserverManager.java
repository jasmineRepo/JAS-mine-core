package microsim.engine;


import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSimulationObserverManager extends AbstractSimulationCollectorManager implements
		SimulationObserverManager {

	@Setter @Getter private SimulationCollectorManager simulationCollectionManager;
	
	public AbstractSimulationObserverManager(SimulationManager manager,
											 SimulationCollectorManager simulationCollectionManager) {
		super(manager);
		setCollectorManager(simulationCollectionManager);	
	}
}
