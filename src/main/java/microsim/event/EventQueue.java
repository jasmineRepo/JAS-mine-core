package microsim.event;

import lombok.NonNull;
import microsim.engine.SimulationEngine;
import microsim.exception.SimulationException;
import microsim.exception.SimulationRuntimeException;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The {@link EventQueue} manages a time ordered queue of events. It is based on a priority queue. At every simulation
 * step the head of the queue is taken and fired. This class extends a thread, because it runs independently of other
 * processes. When activated it runs the simulation.
 */
public class EventQueue {
    /**
     * The action type passed to step listeners. The int value is 10000.
     */
    public static final int EVENT_LIST_STEP = 10000;

    private static double SIMULATION_TIMEOUT = 100000;

    protected Queue<Event> eventQueue;

    double time;

    /**
     * Build new event queue with TIME_TICKS time unit.
     */
    public EventQueue() {
        eventQueue = new PriorityQueue<>(10);
        time = 0;
    }

    /**
     * Build new event queue inheriting parameters from another EventQueue.
     */
    public EventQueue(final @NonNull EventQueue previousEventQueue) {
        this();
        time = previousEventQueue.time;
    }

    /**
     * Return current simulation timeout. If not previously set it returns the default value.
     */
    public static double getSimulationTimeout() {
        return SIMULATION_TIMEOUT;
    }

    /**
     * Set the simulation timeout. When simulation is started without activating Control Panel, user cannot stop it.
     * By setting this timeout simulation is automatically stopped after given steps.
     */
    public static void setSimulationTimeout(final double steps) {
        SIMULATION_TIMEOUT = steps;
    }

    /**
     * Empty the eventQueue and set up time for a new simulation.
     */
    public void clear() {
        eventQueue.clear();
        time = 0;
    }

    /**
     * Return current simulation timer.
     */
    public double getTime() {
        return time;
    }

    /**
     * Make one simulation step.
     *
     * @throws SimulationRuntimeException
     */
    public synchronized void step() throws SimulationException {
        if (eventQueue.isEmpty()) return;

        Event event = eventQueue.poll();

        time = event.getTime();

        event.fireEvent();
        if (event.getLoop() > 0) {
            event.setTimeAtNextLoop();
            scheduleEvent(event);
        }

    }

    /**
     * Run an entire simulation. If model does not stop itself simulation, it will be stopped automatically at timeout
     * time.
     *
     * @throws SimulationException
     */
    public void simulate() throws SimulationException {
        while (eventQueue.size() > 0 && time < SIMULATION_TIMEOUT)
            step();
    }

    protected void scheduleEvent(final @NonNull Event event) {
        eventQueue.add(event);
    }

    /**
     * Schedule a generic event to occur at a given time.
     *
     * @param atTime       The time when event will be fired.
     * @param withOrdering The order that the event will be fired: for two events e1 and e2 scheduled to occur at the
     *                     same time (e1.time == e2.time), if e1.ordering < e2.ordering, then e1 will be fired first.
     *                     If e1.time == e2.time AND e1.ordering == e2.ordering, the first event that was scheduled
     *                     (added to the EventQueue) will be fired first.
     */
    public EventQueue scheduleOnce(final @NonNull Event event, final double atTime, final int withOrdering) {
        event.setTimeOrderingAndLoopPeriod(atTime, withOrdering, 0);
        scheduleEvent(event);

        return this;
    }

    /**
     * Schedule a generic looped event at a given time and ordering.
     *
     * @param atTime            The time when event will be fired for the first time.
     * @param withOrdering      The order that the event will be fired: for two events e1 and e2 scheduled to occur at
     *                          the same time (e1.time == e2.time), if e1.ordering < e2.ordering, then e1 will be fired
     *                          first. If e1.time == e2.time AND e1.ordering == e2.ordering, the first event that was
     *                          scheduled (added to the EventQueue) will be fired first.
     * @param timeBetweenEvents The time period between repeated firing of the event. If this parameter is set to 0,
     *                          this event will not be fired more than once.
     */
    public EventQueue scheduleRepeat(final @NonNull Event event, final double atTime, final int withOrdering,
                                     final double timeBetweenEvents) {
        event.setTimeOrderingAndLoopPeriod(atTime, withOrdering, timeBetweenEvents);
        scheduleEvent(event);

        return this;
    }

    /**
     * Remove from event queue the given event.
     */
    public void unschedule(final @NonNull Event event) {
        eventQueue.remove(event);
    }

    /**
     * Schedule a looped system event.
     *
     * @param atTime       The time when event will be fired for the first time.
     * @param withOrdering The order that the event will be fired: for two events e1 and e2 scheduled to occur at the
     *                     same time (e1.time == e2.time), if e1.ordering < e2.ordering, then e1 will be fired first.
     *                     If e1.time == e2.time AND e1.ordering == e2.ordering, the first event that was scheduled
     *                     (added to the EventQueue) will be fired first.
     * @param withLoop     The time period between repeated firing of the event. If this parameter is set to 0, this
     *                     event will not be fired more than once.
     * @param engine
     * @param type
     * @return
     */
    public SystemEvent scheduleSystem(final double atTime, final int withOrdering, final double withLoop,
                                      final @NonNull SimulationEngine engine, final @NonNull SystemEventType type) {
        SystemEvent event = new SystemEvent(engine, type);
        event.setTimeOrderingAndLoopPeriod(atTime, withOrdering, withLoop);
        scheduleEvent(event);
        return event;
    }

    /**
     * Return event queue as Event array.
     */
    public @NonNull Event[] getEventArray() {
        return eventQueue.toArray(new Event[]{});
    }
}
