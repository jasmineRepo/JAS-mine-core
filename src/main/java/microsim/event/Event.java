package microsim.event;

import lombok.Getter;
import microsim.exception.SimulationException;

public abstract class Event implements Comparable<Event> {

	private static long eventCounter = Long.MIN_VALUE;

	/** Get the next firing absolute time. */
	@Getter protected double time;
	protected int ordering;
	//If two events have time fields with equal value, their ordering fields will determine the order in which the
	// events are fired, with lower ordering values fired before high ordering values.  If ordering fields are also
	// equal, the event that was scheduled first will be fired first in the schedule (determined comparing the
	// eventNumber field).
	final private long eventNumber = eventCounter++;
	//Designed to break randomness of cases when time and ordering of two events is the same.  In this case, the first
	// event that was scheduled will be fired first in the schedule.
	/** Get the loop length. */
	@Getter protected double loop;

	/**
	   * Set the time, ordering and loop period of the event
	   * @param atTime - the absolute time for the event to be fired
	   * @param withOrdering - the ordering of the event to be fired.
	   * 	If two or more events share an absolute time, their order
	   * 	can be specified using the ordering integer - an event
	   * 	with a lower ordering value will be fired earlier.  If
	   * 	two or more events have equal absolute time and ordering,
	   * 	the first event that was scheduled will be fired first.
	   * @param withLoop - the time period between repeated firing of
	   * 	the event.  If this parameter is set to 0, this event will
	   * 	not be fired more than once.
	   */
	public void setTimeOrderingAndLoopPeriod(double atTime, int withOrdering, double withLoop) {
		time = atTime;
		ordering = withOrdering;
		loop = withLoop;
	}

	public abstract void fireEvent() throws SimulationException;

	/**
	 * Determines the natural ordering of events.  As such it determines
	 * the order in which events are fired from the schedule.
	 * If two events have different time fields, the event with the lower
	 * time field is fired before the event with the higher time field.
	 * If two events have time fields with equal value, their ordering
	 * fields will determine the order in which the events are fired,
	 * with lower ordering values fired before high ordering values.  If
	 * ordering fields are also equal, the first event that was scheduled
	 * will be fired first.
	 */
	public int compareTo(Event e) {
		//Ross Richardson: See Joshua Bloch's Effective Java (2nd Ed.) page 65.  "For floating-point fields, use Double.compare..."
		int compareDouble = Double.compare(time, e.getTime());
		if(compareDouble > 0) return 1;
		if(compareDouble < 0) return -1;

		//time and e.getTime() must be equal, so now check the ordering of events
		if (ordering > e.getOrdering()) return 1;
		if (ordering < e.getOrdering()) return -1;

		//time and ordering must be equal, so now check which event was scheduled first and return an int such that the first event is fired first.
		if(eventNumber > e.eventNumber) return 1;
		if(eventNumber < e.eventNumber) return -1;
		else throw new RuntimeException("Two events have the same eventNumber.  " +
										"This should not be possible!\n" + Thread.currentThread().getStackTrace());

	}

	/** Schedule event at the next loop time.
	 * NOTE - this method does not change the ordering of the event.
	 * Use the setOrdering(int) method if this is necessary.
	 * */
	public void setTimeAtNextLoop() {
		time += loop;
	}


	/** Get the ordering of the event's next firing. */
	public int getOrdering() {
		return ordering;
	}

}
