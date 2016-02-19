package microsim.event;

import microsim.exception.SimulationException;


public interface Event extends Comparable<Event> {

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
	  public void setTimeOrderingAndLoopPeriod(double atTime, int withOrdering, double withLoop);
	  
	  /** Get the next firing absolute time.*/
	  public double getTime();

//	  /** Sets time of event assuming the ordering is 0 and there is no loop
//	   * 
//	   * @param atTime - absolute time of event
//	   */
//	  public void setTime(double atTime);

	  public int getOrdering();
	  
//	  public void setOrdering(int withOrdering);
	   
	  /** Get the next firing absolute time.*/
	  public double getLoop();
	  
//	  public void setLoop(double newLoop);
	  
	  public void setTimeAtNextLoop();
	  
	  /** Abstract method to be overridden by real implementation of SimEvent.*/
	  public void fireEvent() throws SimulationException;
}
