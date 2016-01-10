package microsim.engine;

public interface SimulationManager {

	public String getId();
	
	public void setEngine(SimulationEngine engine);
	
	public SimulationEngine getEngine();
	
	public void buildObjects();
	
	public void buildSchedule();
	
	public void dispose();
	
}
