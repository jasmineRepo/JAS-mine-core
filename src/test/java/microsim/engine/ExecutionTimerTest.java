/* (C) Copyright 2026, by Ross Richardson
 * Deterministic execution timing tests for idle gaps, live reads and reset during events.
 * @author ross richardson
 */
package microsim.engine;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExecutionTimerTest {
    @Test void sumsOnlyActiveIntervalsIncludingCurrentEvent() {
        AtomicLong clock = new AtomicLong();
        ExecutionTimer timer = new ExecutionTimer(clock::get);
        clock.set(1000); // Build and waiting before Start.
        assertEquals(0, timer.elapsedNanos());
        long generation = timer.start();
        clock.addAndGet(30);
        assertEquals(30, timer.elapsedNanos()); // Completion message inside event.
        timer.stop(generation);
        clock.addAndGet(5000); // Paused, speed delay or waiting before manual Step.
        assertEquals(30, timer.elapsedNanos());
        generation = timer.start();
        clock.addAndGet(20);
        timer.stop(generation);
        assertEquals(50, timer.elapsedNanos());
        timer.reset();
        assertEquals(0, timer.elapsedNanos());
    }
    @Test void restartingInsideAnEventDoesNotChargeOldTimeToNewRun() {
        AtomicLong clock = new AtomicLong();
        ExecutionTimer timer = new ExecutionTimer(clock::get);
        long oldGeneration = timer.start();
        clock.set(100);
        timer.reset();
        clock.set(500); // Rebuild inside old event.
        timer.stop(oldGeneration);
        assertEquals(0, timer.elapsedNanos());
        long current = timer.start();
        clock.set(510);
        timer.stop(current);
        assertEquals(10, timer.elapsedNanos());
    }
}
