package microsim.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import microsim.engine.SimulationEngine;
import microsim.exception.SimulationException;

/**
 * A special implementation of the SimEvent familiy. It is not a real event, but
 * a container for other events. When fired this object automatically fires the
 * events contained. Each contained event is fired at the group time, any other
 * time pointer is ignored.
 *
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa
 * </p>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 *         <p>
 */
public class EventGroup extends Event {
	private List<Event> actions;

	/** Build a new group event. */
	public EventGroup() {
		actions = new ArrayList<Event>();
	}

	/** Empty the event list. */
	public void clear() {
		actions.clear();
	}

	/** Add an event to the list. */
	public EventGroup addEvent(Event newEvent) {
		actions.add(newEvent);
		return this;
	}

	/**
	 * Create a new SimSimpleEvent and add an event to the list, using late
	 * binding method.
	 *
	 * @throws SimulationException
	 */
	public EventGroup addEvent(Object object, String method)
			throws SimulationException {
		actions.add(new SingleTargetEvent(object, method));
		return this;
	}

	/**
	 * Create a new SimSimpleEvent and add an event to the list, using early
	 * binding method.
	 */
	public EventGroup addEvent(Object object, Enum<?> actionType) {
		actions.add(new SingleTargetEvent(object, actionType));
		return this;
	}

	/** Create a new SimSystemEvent and add an event to the list. */
	public EventGroup addSystemEvent(SimulationEngine engine, SystemEventType actionType) {
		actions.add(new SystemEvent(engine, actionType));
		return this;
	}

	public EventGroup addCollectionEvent(Collection<?> elements,
			Class<?> objectType, String method) throws SimulationException {
		return addCollectionEvent(elements, objectType, method, true);
	}

	/**
	 * Create a new SimCollectionEvent and add an event to the list, using late
	 * binding method.
	 *
	 * @throws SimulationException
	 */
	public EventGroup addCollectionEvent(Collection<?> elements,
			Class<?> objectType, String method, boolean readOnly) throws SimulationException {
		actions.add(new CollectionTargetEvent(elements, objectType, method, readOnly));
		return this;
	}

	public EventGroup addCollectionEvent(Collection<?> elements,
			Enum<?> actionType) {
		return addCollectionEvent(elements, actionType, true);
	}

	/**
	 * Create a new SimCollectionEvent and add an event to the list, using early
	 * binding method.
	 */
	public EventGroup addCollectionEvent(Collection<?> elements,
			Enum<?> actionType, boolean readOnly) {
		actions.add(new CollectionTargetEvent(elements, actionType, readOnly));
		return this;
	}

	/** Remove the given event from the list. */
	public void removeEvent(Event event) {
		actions.remove(event);
	}

	/**
	 * Fire each event into the list.
	 *
	 * @throws SimulationException
	 */
	public void fireEvent() throws SimulationException {
		for (Event event : actions)
			event.fireEvent();
	}

	/**
	 * Return an sorted array of the added events.
	 *
	 * @return An array of SimEvent objects.
	 */
	public Event[] eventsToArray() {
		Event[] events = new Event[actions.size()];
		for (int i = 0; i < events.length; i++)
			events[i] = actions.get(i);
		return events;
	}

	public List<Event> getActions() {
		return actions;
	}

}
