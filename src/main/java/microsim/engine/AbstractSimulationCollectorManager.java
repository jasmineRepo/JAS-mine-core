package microsim.engine;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class AbstractSimulationCollectorManager extends AbstractSimulationManager
    implements SimulationCollectorManager {

    @Setter
    @Getter
    private SimulationManager manager;

    public AbstractSimulationCollectorManager(final @NonNull SimulationManager manager) {
        super();
        setManager(manager);
    }
}
