package microsim.engine;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class AbstractSimulationObserverManager extends AbstractSimulationCollectorManager
    implements SimulationObserverManager {

    @Setter
    @Getter
    private SimulationCollectorManager collectorManager;

    public AbstractSimulationObserverManager(final @NonNull SimulationManager manager,
                                             final @NonNull SimulationCollectorManager simulationCollectionManager) {
        super(manager);
        setCollectorManager(simulationCollectionManager);
    }
}
