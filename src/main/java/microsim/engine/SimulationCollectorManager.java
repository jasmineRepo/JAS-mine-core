package microsim.engine;


public interface SimulationCollectorManager extends SimulationManager {

    SimulationManager getManager();

    void setManager(SimulationManager manager);

}
