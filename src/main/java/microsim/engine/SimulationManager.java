package microsim.engine;

/**
 * The model deals mainly with specification issues, creating objects, relations between objects,
 * and defining the order of events that take place in the simulation.
 */
public interface SimulationManager {

    String getId();

    SimulationEngine getEngine();

    void setEngine(SimulationEngine engine);

    void buildObjects();

    void buildSchedule();

    void dispose();
}
