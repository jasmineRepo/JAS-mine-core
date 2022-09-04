package microsim.engine;


import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSimulationCollectorManager extends AbstractSimulationManager implements
		SimulationCollectorManager {

	@Setter @Getter private SimulationManager manager;

	public AbstractSimulationCollectorManager(SimulationManager manager) {
		super();
		setManager(manager);
	}
}
