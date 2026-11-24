/* (C) Copyright 2026, by Ross Richardson
 * Checks that actual engine steps time event work and always stop timing after failure.
 * @author ross richardson
 */
package microsim.engine;

import microsim.event.Event;
import microsim.exception.SimulationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationExecutionTimingTest {
    private SimulationEngine builtEngine() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        var field = SimulationEngine.class.getDeclaredField("modelBuild");
        field.setAccessible(true);
        field.set(engine, true); // No model/database needed for queue integration tests.
        return engine;
    }
    @Test void manualStepsExposeCurrentTimingAndResetClearsIt() throws Exception {
        SimulationEngine engine = builtEngine();
        long[] inside = {0};
        engine.getEventQueue().scheduleOnce(new Event() {
            public void fireEvent() { inside[0] = engine.getExecutionTimeNanos(); }
        }, 0, 0);
        engine.step();
        assertTrue(inside[0] > 0);
        long completed = engine.getExecutionTimeNanos();
        assertTrue(completed >= inside[0]);
        engine.pause();
        assertEquals(completed, engine.getExecutionTimeNanos());
        assertEquals(completed, engine.getExecutionTimeNanos());
        engine.reset();
        assertEquals(0, engine.getExecutionTimeNanos());
    }
    @Test void eventFailureStopsClockWithoutSwallowingException() throws Exception {
        SimulationEngine engine = builtEngine();
        engine.getEventQueue().scheduleOnce(new Event() {
            public void fireEvent() { throw new IllegalStateException("test failure"); }
        }, 0, 0);
        assertThrows(IllegalStateException.class, engine::step);
        long completed = engine.getExecutionTimeNanos();
        assertTrue(completed > 0);
        assertEquals(completed, engine.getExecutionTimeNanos());
    }
}
