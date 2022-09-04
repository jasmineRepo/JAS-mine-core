package microsim.agent;

import jakarta.persistence.Transient;

import microsim.engine.SimulationEngine;
import microsim.event.EventListener;

/**
 * An abstract class for objects representing agents.
 *
 * @param <T>
 */
public abstract class Agent<T> implements EventListener {

    private T t;

    @SuppressWarnings("unchecked")
    @Transient
    public T getModel() {
        t = null;
        return (T) SimulationEngine.getInstance().getManager(t.getClass().getCanonicalName());
    }
}
