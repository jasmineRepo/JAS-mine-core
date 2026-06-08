package microsim.engine;

/**
 * The model deals mainly with specification issues, creating objects, relations between objects, 
 * and defining the order of events that take place in the simulation.
 */
public interface SimulationManager {

	public String getId();
	
	public void setEngine(SimulationEngine engine);
	
	public SimulationEngine getEngine();
	
	public void buildObjects();
	
	public void buildSchedule();
	
	public void dispose();
	
}
