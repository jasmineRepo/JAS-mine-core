package microsim.engine;


public interface SimulationObserverManager extends SimulationCollectorManager {

    SimulationCollectorManager getCollectorManager();

    void setCollectorManager(SimulationCollectorManager manager);

}
