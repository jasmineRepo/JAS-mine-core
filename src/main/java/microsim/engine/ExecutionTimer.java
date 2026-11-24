/* (C) Copyright 2026, by Ross Richardson
 * Monotonic wall-clock accumulator for scheduled event execution, independent of launch mode.
 * @author ross richardson
 */
package microsim.engine;

import java.util.function.LongSupplier;

final class ExecutionTimer {
    private final LongSupplier clock;
    private long total, started, generation;
    private boolean active;

    ExecutionTimer() { this(System::nanoTime); }
    ExecutionTimer(LongSupplier clock) { this.clock = clock; }

    synchronized long start() {
        started = clock.getAsLong();
        active = true;
        return generation;
    }

    synchronized void stop(long expectedGeneration) {
        // A scheduled restart can reset the timer within the old event.
        if (active && generation == expectedGeneration) {
            total += clock.getAsLong() - started;
            active = false;
        }
    }

    synchronized void reset() {
        total = 0;
        active = false;
        generation++;
    }

    synchronized long elapsedNanos() {
        return total + (active ? clock.getAsLong() - started : 0);
    }
}
