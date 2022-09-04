package microsim.engine;


public interface SimulationObserverManager extends SimulationCollectorManager {

	void setCollectorManager(SimulationCollectorManager manager);

	SimulationCollectorManager getCollectorManager();

}
