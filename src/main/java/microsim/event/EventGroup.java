package microsim.event;

import lombok.NonNull;
import microsim.engine.SimulationEngine;
import microsim.exception.SimulationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A special implementation of the SimEvent family. It is not a real event, but a container for other events. When fired
 * this object automatically fires the events contained. Each contained event is fired at the group time, any other time
 * pointer is ignored.
 */
public class EventGroup extends Event {
    private final List<Event> actions;

    /**
     * Build a new group event.
     */
    public EventGroup() {
        actions = new ArrayList<>();
    }

    /**
     * Empty the event list.
     */
    public void clear() {
        actions.clear();
    }

    /**
     * Add an event to the list.
     */
    public @NonNull EventGroup addEvent(final @NonNull Event newEvent) {
        actions.add(newEvent);
        return this;
    }

    /**
     * Create a new SimSimpleEvent and add an event to the list, using late binding method.
     *
     * @throws SimulationException
     */
    public @NonNull EventGroup addEvent(final @NonNull Object object,
                                        final @NonNull String method) throws SimulationException {
        actions.add(new SingleTargetEvent(object, method));
        return this;
    }

    /**
     * Create a new SimSimpleEvent and add an event to the list, using early binding method.
     */
    public EventGroup addEvent(final @NonNull Object object, final @NonNull Enum<?> actionType) {
        actions.add(new SingleTargetEvent(object, actionType));
        return this;
    }

    /**
     * Create a new SimSystemEvent and add an event to the list.
     */
    public EventGroup addSystemEvent(final @NonNull SimulationEngine engine,
                                     final @NonNull SystemEventType actionType) {
        actions.add(new SystemEvent(engine, actionType));
        return this;
    }

    public @NonNull EventGroup addCollectionEvent(final @NonNull Collection<?> elements,
                                                  final @NonNull Class<?> objectType,
                                                  final @NonNull String method) throws SimulationException {
        return addCollectionEvent(elements, objectType, method, true);
    }

    /**
     * Create a new SimCollectionEvent and add an event to the list, using late binding method.
     *
     * @throws SimulationException
     */
    public @NonNull EventGroup addCollectionEvent(final @NonNull Collection<?> elements,
                                                  final @NonNull Class<?> objectType,
                                                  final @NonNull String method,
                                                  final boolean readOnly) throws SimulationException {
        actions.add(new CollectionTargetEvent(elements, objectType, method, readOnly));
        return this;
    }

    public @NonNull EventGroup addCollectionEvent(final @NonNull Collection<?> elements,
                                                  final @NonNull Enum<?> actionType) {
        return addCollectionEvent(elements, actionType, true);
    }

    /**
     * Create a new SimCollectionEvent and add an event to the list, using early binding method.
     */
    public @NonNull EventGroup addCollectionEvent(final @NonNull Collection<?> elements,
                                                  final @NonNull Enum<?> actionType, final boolean readOnly) {
        actions.add(new CollectionTargetEvent(elements, actionType, readOnly));
        return this;
    }

    /**
     * Remove the given event from the list.
     */
    public void removeEvent(final @NonNull Event event) {
        actions.remove(event);
    }

    /**
     * Fire each event into the list.
     *
     * @throws SimulationException
     */
    public void fireEvent() throws SimulationException {
        for (Event event : actions) event.fireEvent();
    }

    /**
     * Return a sorted array of the added events.
     *
     * @return An array of SimEvent objects.
     */
    public @NonNull Event[] eventsToArray() {
        return actions.toArray(Event[]::new);
    }

    public @NonNull List<Event> getActions() {
        return actions;
    }

}
