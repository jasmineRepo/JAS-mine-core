package microsim.engine;


public interface SimulationObserverManager extends SimulationCollectorManager {
	
	public void setCollectorManager(SimulationCollectorManager manager);
	
	public SimulationCollectorManager getCollectorManager();
	
}
