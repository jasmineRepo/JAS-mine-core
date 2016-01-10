package microsim.engine;



public abstract class AbstractSimulationManager implements SimulationManager {

	private SimulationEngine engine;
		
	public void setEngine(SimulationEngine engine) {
		this.engine = engine;
	}

	public SimulationEngine getEngine() {
		return engine;
	}

	public abstract void buildObjects();
	
	public abstract void buildSchedule();

	public void dispose() {
		this.engine = null;
	}

	public String getId() {
		return this.getClass().getCanonicalName();
	}

}
