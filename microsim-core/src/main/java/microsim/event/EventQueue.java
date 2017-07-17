package microsim.event;

import java.util.PriorityQueue;
import java.util.Queue;

import microsim.engine.SimulationEngine;
import microsim.exception.SimulationException;
import microsim.exception.SimulationRuntimeException;

/**
 * The eventQueue manages a time ordered queue of events. It is based on a priority
 * queue. At every simulation step the head of the queue is taken and fired. This
 * class extends a thread, because it runs independently from other processes.
 * When activated it runs the simulation.
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa and Ross Richardson
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
 * @author Michele Sonnessa and Ross Richardson
 *         <p>
 */
public class EventQueue {
	/** The action type passed to step listeners. The int value is 10000. */
	public static final int EVENT_LIST_STEP = 10000;

	private static double SIMULATION_TIMEOUT = 100000;

	protected Queue<Event> eventQueue;			//Ross - changing eventQueue from a linked list to a priority queue in order to improve time complexity
//	protected Iterator<Event> iterator;// = eventQueue.iterator(); 	 //Define below - note this is no longer a list iterator!

	/**
	 * @supplierCardinality 1
	 */
	double time = 0;
	// protected List<ISimEventListener> stepListeners;



	/**
	 * @link dependency
	 * @supplierRole 1..
	 **/
	/* #SimEvent lnkSimEvent; */

	/** Build new event queue with TIME_TICKS time unit. */
	public EventQueue() { 
		eventQueue = new PriorityQueue<Event>(10);
		time = 0;
		// stepListeners = new LinkedList<ISimEventListener>();
	}

	/** Build new event queue inheriting parameters from another EventQueue. */
	public EventQueue(EventQueue previousEventQueue) {
		this();

		if (previousEventQueue != null) {
			time = previousEventQueue.time;
			//stepListeners = previousEventList.stepListeners;			
		}
	}

	/** Empty the eventQueue and set up time for a new simulation. */
	public void clear() {
		eventQueue.clear();
		// stepListeners.clear();
		time = 0;
	}

	/**
	 * Set the simulation timeout. When simulation is started without activating
	 * Control Panel, user cannot stop it. By setting this timeout simulation is
	 * automatically stopped after given steps.
	 */
	public static void setSimulationTimeout(double steps) {
		SIMULATION_TIMEOUT = steps;
	}

	/**
	 * Return current simulation timeout. If not previously set it returns the
	 * default value.
	 */
	public static double getSimulationTimeout() {
		return SIMULATION_TIMEOUT;
	}

	/** Return current simulation timer. */
	public double getTime() {
		return time;
	}

//	/**
//	 * Add an event listener to the listeners list. When an object implementing
//	 * ISimEventListener iterface enters this register at each simulation step
//	 * it will be informed.
//	 */
//	public void addEventListener(ISimEventListener listener) {
//		stepListeners.add(listener);
//	}
//
//	/** Remove a listener from the listeners list. */
//	public void removeEventListener(ISimEventListener listener) {
//		stepListeners.remove(listener);
//	}

	/** Make one simulation step. 
	 * @throws SimulationRuntimeException */
	public synchronized void step() throws SimulationException {
		if(eventQueue.isEmpty()) {
			return;
		}

		Event event = eventQueue.poll();

		time = event.getTime();

		event.fireEvent();
		if (event.getLoop() > 0) {
			event.setTimeAtNextLoop();
			scheduleEvent(event);
		} 

	}



	/**
	 * Run an entire simulation. If model does not stop itself simulation, it
	 * will be stop automatically at timeout time.
	 * @throws SimulationException 
	 */
	public void simulate() throws SimulationException {
		while (eventQueue.size() > 0 && time < SIMULATION_TIMEOUT)
			step();
	}

	protected void scheduleEvent(Event event) {
		eventQueue.add(event);			//Should automatically be fitted into a valid position in the priority queue by simply using the add method.
	}

	/**
	 * Schedule a generic event to occur at a given time. 
	 * 
	 * @param atTime
	 *            The time when event will be fired.
	 * @param withOrdering
	 * 			  The order that the event will be fired: for two events e1 and e2 scheduled to occur at the same time
	 * 			  (e1.time == e2.time), if e1.ordering < e2.ordering, then e1
	 * 			  will be fired first.  If e1.time == e2.time AND e1.ordering == e2.ordering, 
	 * 			  the first event that was scheduled (added to the EventQueue) will be fired first.
	 */
	public EventQueue scheduleOnce(Event event, double atTime, int withOrdering) {
		event.setTimeOrderingAndLoopPeriod(atTime, withOrdering, 0);
		scheduleEvent(event);
		
		return this;
	}

	/**
	 * Schedule a generic looped event at a given time and ordering.
	 * 
	 * @param atTime
	 *            The time when event will be fired for the first time.
	 * @param withOrdering
	 * 			  The order that the event will be fired: for two events e1 and e2 scheduled to occur at the same time
	 * 			  (e1.time == e2.time), if e1.ordering < e2.ordering, then e1
	 * 			  will be fired first.  If e1.time == e2.time AND e1.ordering == e2.ordering, 
	 * 			  the first event that was scheduled (added to the EventQueue) will be fired first.
	 * @param timeBetweenEvents
	 *            The time period between repeated firing of the event. If this parameter is set to 0, this event will not be fired more than once.
	 */
	public EventQueue scheduleRepeat(Event event, double atTime, int withOrdering, double timeBetweenEvents) {
		event.setTimeOrderingAndLoopPeriod(atTime, withOrdering, timeBetweenEvents);
		scheduleEvent(event);
		
		return this;
	}

	/**
	 * Schedule a generic (possibly looped) event at a given time.
	 * 
	 * Warning - This method is deprecated as it doesn't specify the ordering of events scheduled for the same time 
	 * - all events scheduled using this method are set with a default ordering of 0.
	 * In this instance, if events e1 and e2 are scheduled for the same time (i.e. e1.time == e2.time) using this method, 
	 * there is no way of ensuring that, for example, e2 is fired before e1 - the actual order these same-time events is 
	 * determined by the order in which they are added to the EventQueue.
	 * 
	 * This method is still included in the JAS-mine libraries for backwards compatibility with JAS2 models, however it is preferable to use
	 * scheduleOnce(Event, double, int) or scheduleRepeating(Event, double, int, double), where the order of same-time events can be fully specified 
	 * using the int withOrdering parameter.
	 * 
	 * @param atTime
	 *            The time when event will be fired for the first time.
	 * @param timeBetweenEvents
	 *            The time period between repeated firing of the event. If this parameter is set to 0, this event will not be fired more than once.
	 */
	@Deprecated
	public EventQueue schedule(Event event, double atTime, double timeBetweenEvents) {
		event.setTimeOrderingAndLoopPeriod(atTime, 0, timeBetweenEvents);
		scheduleEvent(event);
		
		return this;
	}
	
	/**
	 * Schedule a single generic event at a given time.
	 * 
	 * Warning - This method is deprecated as it doesn't specify the ordering of events scheduled for the same time 
	 * - all events scheduled using this method are set with a default ordering of 0.
	 * In this instance, if events e1 and e2 are scheduled for the same time (i.e. e1.time == e2.time) using this method, 
	 * there is no way of ensuring that, for example, e2 is fired before e1 - the actual order these same-time events is 
	 * determined by the order in which they are added to the EventQueue.
	 * 
	 * This method is still included in the JAS-mine libraries for backwards compatibility with JAS2 models, however it is preferable to use
	 * scheduleOnce(Event, double, int) or scheduleRepeating(Event, double, int, double), where the order of same-time events can be fully specified 
	 * using the int withOrdering parameter.
	 * 
	 * @param atTime
	 *            The time when event will be fired for the first time.
	 */
	@Deprecated
	public EventQueue schedule(Event event, double atTime) {
		event.setTimeOrderingAndLoopPeriod(atTime, 0, 0.);
		scheduleEvent(event);
		
		return this;
	}

	/** Remove from event queue the given event. */
	public void unschedule(Event event) {
		eventQueue.remove(event);
	}

	
	/** Schedule a looped system event.
	 * 
	 * @param atTime
	 *            The time when event will be fired for the first time.
	 * @param withOrdering
	 * 			  The order that the event will be fired: for two events e1 and e2 scheduled to occur at the same time
	 * 			  (e1.time == e2.time), if e1.ordering < e2.ordering, then e1
	 * 			  will be fired first.  If e1.time == e2.time AND e1.ordering == e2.ordering, 
	 * 			  the first event that was scheduled (added to the EventQueue) will be fired first.
	 * @param withLoop
	 *            The time period between repeated firing of the event. If this parameter is set to 0, this event will not be fired more than once.
	 * 
	 * @param engine
	 * @param type
	 * @return
	 */
	public SystemEvent scheduleSystem(double atTime, int withOrdering, double withLoop, SimulationEngine engine, SystemEventType type) {
		SystemEvent event = new SystemEvent(engine, type);
		event.setTimeOrderingAndLoopPeriod(atTime, withOrdering, withLoop);
		scheduleEvent(event);
		return event;
	}

	/** Return event queue as Event array. */
	public Event[] getEventArray() {
		return eventQueue.toArray(new Event[] {});
	}
	
}
	