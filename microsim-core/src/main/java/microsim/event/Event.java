package microsim.event;

import microsim.exception.SimulationException;

/**
 * Represents an event in the simulation.
 * <p>
 * {@code Event} implements {@code Comparable} specifically so that two
 * {@code Event} objects can be ordered for firing. Note that {@code compareTo}
 * implies that any two {@code Event} instances, {@code eventA} and
 * {@code eventB}, are ordered first on {@code getTime()} (lower values are
 * fired earlier), and, in case {@code eventA.getTime() == eventB.getTime()},
 * they are fired according to {@code getOrdering()} (lower values are fired
 * earlier).
 * <p>
 * If time and ordering are equal, then there's no guarantee what in what
 * sequence any two {@code Event} instances will fire. This may mean that even
 * in a simulation that doesn't seem to have a stochastic event, multiple runs
 * of the same simulation code may not have the same result because events are
 * fired in different orders on different simulation runs. The uncertainty may
 * be eliminated by making sure all events return unique values for
 * {@code getOrdering()}.
 * <p>
 * Concepts of "time" mentioned below refer to time as measured and counted in
 * the simulation.
 */
public abstract class Event implements Comparable<Event> {

	private double time;
	private int ordering;
	private double loop;

	/**
	 * Set the time, ordering and loop period of the event
	 * 
	 * @param atTime
	 *            - the absolute time for the event to be fired
	 * @param withOrdering
	 *            - the ordering of the event to be fired
	 * @param withLoop
	 *            - the time period between repeated firing of the event, or 0
	 *            if it should only fire once
	 */
	public final void setTimeOrderingAndLoopPeriod(double atTime, int withOrdering, double withLoop) {
		time = atTime;
		ordering = withOrdering;
		loop = withLoop;
	}

	/**
	 * Executes the primary activity of this {@code Event}.
	 * @throws SimulationException
	 */
	public abstract void fireEvent() throws SimulationException;

	/**
	 * Determines the natural ordering of events.
	 * <p>
	 * {@code Event} implements {@code Comparable} specifically so that two
	 * {@code Event} objects can be ordered for firing. Note that
	 * {@code compareTo} implies that any two {@code Event} instances,
	 * {@code eventA} and {@code eventB}, are ordered first on {@code getTime()}
	 * (lower values are fired earlier), and, in case
	 * {@code eventA.getTime() == eventB.getTime()}, they are fired according to
	 * {@code getOrdering()} (lower values are fired earlier).
	 * <p>
	 * If time and ordering are equal, then there's no guarantee what in what
	 * sequence any two {@code Event} instances will fire. This may mean that
	 * even in a simulation that doesn't seem to have a stochastic event,
	 * multiple runs of the same simulation code may not have the same result
	 * because events are fired in different orders on different simulation
	 * runs. The uncertainty may be eliminated by making sure all events return
	 * unique values for {@code getOrdering()}.
	 */
	public final int compareTo(Event e) {
		// Ross Richardson: See Joshua Bloch's Effective Java (2nd Ed.) page 65.
		// "For floating-point fields, use Double.compare..."
		int compareDouble = Double.compare(time, e.getTime());
		if (compareDouble > 0)
			return 1;
		if (compareDouble < 0)
			return -1;

		// time and e.getTime() must be equal, so now check the ordering of
		// events
		if (ordering > e.getOrdering())
			return 1;
		if (ordering < e.getOrdering())
			return -1;
		return 0;
	}

	/**
	 * Updates the time field to be the time this should fire in the next loop iteration.
	 */
	public final void setTimeAtNextLoop() {
		time += loop;
	}

	/** 
	 * Returns the next absolute time this event should fire. 
	 */
	public final double getTime() {
		return time;
	}

	/** 
	 * Returns the time period, or "loop length", between iterations of this event. 
	 */
	public final double getLoop() {
		return loop;
	}

	/** 
	 * Get the ordering of the event's next firing. 
	 */
	public final int getOrdering() {
		return ordering;
	}

}
