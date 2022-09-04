package microsim.engine;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSimulationManager implements SimulationManager {

	@Setter @Getter private SimulationEngine engine;

	public abstract void buildObjects();

	public abstract void buildSchedule();

	public void dispose() {
		this.engine = null;
	}

	public String getId() {
		return this.getClass().getCanonicalName();
	}

}
