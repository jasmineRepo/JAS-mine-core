package microsim.event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import microsim.exception.SimulationException;

public class EventTest 
    extends TestCase {
	
	/**
	 * An {@code Event} with no real effect.
	 *
	 */
	public static class DumbEvent extends Event {
		
		private final String label;
		
		public DumbEvent(String label){
			this.label = label;
		}
		
		public void fireEvent(){
			System.out.println(label + " happening now...");
		}
		
		public String toString(){
			return label + " event";
		}
	}
	
	/**
	 * An {@code Event} with no real effect but whose firings can be counted.
	 */
	public static class SilentEvent extends Event {
		
		private final String label;
		private int fireCount;
		
		public SilentEvent(String label){
			this.label = label;
			this.fireCount = 0;
		}
		
		public void fireEvent(){
			fireCount++;
		}
		
		public int getCount(){
			return this.fireCount;
		}
		
		public String toString(){
			return label + " event";
		}
	}
	
	/**
	 * A recurring event with no real effect but with its own {@code assertTrue} test.
	 */
	public static class RecurringEvent extends Event {
		private final String label;
		private int fireCount;
		private EventQueue context;
		private List<Double> firings;
		
		public RecurringEvent(String label, EventQueue context){
			this.label = label;
			this.fireCount = 0;
			this.context = context;
			this.firings = new ArrayList<>();
		}
		
		public void fireEvent(){
			fireCount++;
			System.out.println(label + " happening now...");
			
			// this should only be fired at the correct time!
			assertTrue(this.getTime() == context.getTime());
			firings.add(context.getTime());
		}
		
		public int getCount(){
			return this.fireCount;
		}
		
		public String toString(){
			return label + " event";
		}
		
		public EventQueue getContext(){
			return this.context;
		}
		
		public List<Double> getFirings(){
			return firings;
		}
	}
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EventTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EventTest.class );
    }

    /**
     * Tests time ordering, where events are at discrete times that can be ordered without referring to ordering field.
     */
	public void testTimeOrdering()
    {
    	EventQueue eventList;
    	List<Event> events; // the ordered list of events in the order we think they ought to happen
    	eventList = new EventQueue();
    	events = new ArrayList<Event>();
    	
    	Event first = new DumbEvent("first event");
    	Event second = new DumbEvent("second event");
    	Event third = new DumbEvent("third event");
    	third.setTimeOrderingAndLoopPeriod(30, 1, 0);
    	first.setTimeOrderingAndLoopPeriod(10, 1, 0);
    	second.setTimeOrderingAndLoopPeriod(20, 1, 0);
    	    	
    	events.add(third);
    	events.add(second);
    	events.add(first);
    	 
    	Collections.sort(events);
        assertTrue( events.size() == 3 );

    	assertTrue( events.get(0) == first );
        assertTrue( events.get(1) == second );
        assertTrue( events.get(2) == third );
    	
    	eventList.scheduleOnce(second, second.getTime(), second.getOrdering());
    	eventList.scheduleOnce(third, third.getTime(), third.getOrdering());
    	eventList.scheduleOnce(first, first.getTime(), first.getOrdering());
        
    	
    	// the output of the following ought to be manually examined, since that's the easiest to test to make sure these things have been properly executed
		try {
			System.out.println();
			System.out.println("*** TEST OF ORDERING BY TIME FIELD ***");
			System.out.println("[events should fire in first, second, third order]");
			eventList.simulate();

			System.out.println("*** PROPERLY ENDED TEST OF ORDERING BY TIME FIELD ***");			
			
		} catch (SimulationException e) {
			e.printStackTrace();
		}

    }
	
	/**
	 * Test ordering according only to the ordering field.
	 */
	public void testOrdering()
    {
    	EventQueue eventQueue;
    	List<Event> events; // the ordered list of events in the order we think they ought to happen
    	eventQueue = new EventQueue();
    	events = new ArrayList<Event>();
    	
    	Event first = new DumbEvent("first event");
    	Event second = new DumbEvent("second event");
    	Event third = new DumbEvent("third event");
    	third.setTimeOrderingAndLoopPeriod(1000, 3, 0);
    	first.setTimeOrderingAndLoopPeriod(1000, 1, 0);
    	second.setTimeOrderingAndLoopPeriod(1000, 2, 0);
    	    	
    	events.add(third);
    	events.add(second);
    	events.add(first);
    	 
    	// tests the sorting of the events
    	Collections.sort(events);
        assertTrue( events.size() == 3 );

    	assertTrue( events.get(0) == first );
        assertTrue( events.get(1) == second );
        assertTrue( events.get(2) == third );
    	
    	eventQueue.scheduleOnce(second, second.getTime(), second.getOrdering());
    	eventQueue.scheduleOnce(third, third.getTime(), third.getOrdering());
    	eventQueue.scheduleOnce(first, first.getTime(), first.getOrdering());
        
    	assertTrue(Array.getLength(eventQueue.getEventArray()) == 3);

    	// the output of the following ought to be manually examined
    		// since that's the easiest to test to make sure these things have been properly executed
		try {
			System.out.println();
			System.out.println("*** TEST OF ORDERING BY ORDERING FIELD ***");
			System.out.println("[events should fire in first, second, third order]");

			eventQueue.simulate();
			System.out.println("*** PROPERLY ENDED TEST OF ORDERING BY ORDERING FIELD ***");
			
		} catch (SimulationException e) {
			e.printStackTrace();
		}
		
		

    }
	
	/**
	 * Test ordering according to both time and ordering fields.
	 */
	public void testTimeAndOrdering()
    {
		System.out.println("*** TEST OF ORDERING BY TIME AND ORDERING FIELDS ***");
		System.out.println("[events should fire in first, second, third order]");
		EventQueue eventQueue;
    	List<Event> events; // the ordered list of events in the order we think they ought to happen
    	eventQueue = new EventQueue();
    	events = new ArrayList<Event>();
    	
    	Event first = new DumbEvent("first event");
    	Event third = new DumbEvent("third event");
    	Event second = new DumbEvent("second event");
    	third.setTimeOrderingAndLoopPeriod(1001, 1, 0);
    	first.setTimeOrderingAndLoopPeriod(999, 0, 0);
    	second.setTimeOrderingAndLoopPeriod(999, 1, 0);
    	    	
    	events.add(third);
    	events.add(second);
    	events.add(first);
    	 
    	Collections.sort(events);
        assertTrue( events.size() == 3 );

    	assertTrue( events.get(0) == first );
        assertTrue( events.get(1) == second );
        assertTrue( events.get(2) == third );
    	
    	eventQueue.scheduleOnce(second, second.getTime(), second.getOrdering());
    	eventQueue.scheduleOnce(third, third.getTime(), third.getOrdering());
    	eventQueue.scheduleOnce(first, first.getTime(), first.getOrdering());
        
    	// the output of the following ought to be manually examined
		try {
			System.out.println();

			eventQueue.simulate();
			
		} catch (SimulationException e) {
			e.printStackTrace();
		}		
		
		System.out.println("*** END TEST OF ORDERING BY TIME AND ORDERING FIELDS ***");


    }
	
	/**
	 * Test that time-out works properly by checking against number of times event has fired.
	 */
	public void testOutOfTime()
    {
    	EventQueue q;
    	q = new EventQueue();
    	
    	SilentEvent myEvent = new SilentEvent("my event");
    	
    	q.scheduleRepeat(myEvent, 1, 1, 1);
        
		try {
			q.simulate();
			
		} catch (SimulationException e) {
			e.printStackTrace();
		}
		
		assertTrue((double)myEvent.getCount() == EventQueue.getSimulationTimeout());
		
		EventQueue.setSimulationTimeout(100);
        
		q = new EventQueue();
		myEvent = new SilentEvent("my event");
    	q.scheduleRepeat(myEvent, 1, 1, 1);
		
		try {
			q.simulate();			
		} catch (SimulationException e) {
			e.printStackTrace();
		}
		
		assertTrue((double)myEvent.getCount() == EventQueue.getSimulationTimeout());

    }
	
	/**
	 * Test ordering according to both time and ordering fields.
	 */
	public void testRecurringSimple() {
    	EventQueue events;
    	events = new EventQueue();
    	
    	EventQueue.setSimulationTimeout(10);
    	
    	RecurringEvent myEvent = new RecurringEvent("recurring event", events);
    	
    	events.scheduleRepeat(myEvent, 0, 1, 1);
        
    	// the output of the following ought to be manually examined, 
    	// since that's the easiest to test to make sure these things have been properly executed
		try {
			System.out.println("*** BEGIN SIMPLE TEST OF RECURRING EVENTS ***");
			events.simulate();
		} catch (SimulationException e) {
			e.printStackTrace();
		}		
		
		List<Double> firings = myEvent.getFirings();
		assertTrue(firings.size() == 11);
		System.out.println(myEvent + " fired " + firings.size() + " time(s).");
		assertTrue(Double.compare(firings.get(4),5.0) == 0);
		System.out.println("*** END SIMPLE TEST OF RECURRING EVENTS ***");
		
    }

    
}
