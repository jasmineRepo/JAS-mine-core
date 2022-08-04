package microsim.engine;


public interface SimulationCollectorManager extends SimulationManager {
	
	void setManager(SimulationManager manager);
	
	SimulationManager getManager();

}
