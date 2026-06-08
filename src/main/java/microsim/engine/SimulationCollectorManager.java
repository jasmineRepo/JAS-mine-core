package microsim.engine;


public interface SimulationCollectorManager extends SimulationManager {
	
	public void setManager(SimulationManager manager);
	
	public SimulationManager getManager();

}
