package microsim.event;

import lombok.NonNull;
import microsim.engine.SimulationEngine;
import microsim.exception.SimulationException;

/**
 * System events are directly processed by the simulation engine. There are some special events that engine is able to
 * understand. For instance, you can schedule the end of simulation using a system event.
 * <br><i> SystemEvent e = new SystemEvent(Sim.EVENT_SIMULATION_END);</i><br>
 * eventQueue.schedule(100, e);
 * <i>
 * The above code make the engine stop at 100. When this event happens the simulationEnd() method of the current running
 * models is called.
 */
public class SystemEvent extends Event {

    SystemEventType type;
    SimulationEngine engine;

    public SystemEvent(final @NonNull SimulationEngine engine, final @NonNull SystemEventType type) {
        this.type = type;
        this.engine = engine;
    }

    public void fireEvent() throws SimulationException {
        switch (type) {
            case Start -> engine.startSimulation();
            case Restart -> engine.rebuildModels();
            case Stop -> engine.pause();
            case Shutdown -> engine.quit();
            case Build -> engine.buildModels();
            case Step -> engine.step();
            case End -> engine.end();
            case Setup -> engine.setup();
        }
    }

    /**
     * Return a string describing event.
     */
    public @NonNull String toString() {
        return "SystemEvent(@" + getTime() + " " + type;
    }
}
