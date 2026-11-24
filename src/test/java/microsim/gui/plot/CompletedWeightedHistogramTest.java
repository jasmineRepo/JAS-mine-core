/* (C) Copyright 2026, by Ross Richardson
 * Tests reading completed weighted histogram updates independently of the model's suppliers.
 * @author ross richardson
 */
package microsim.gui.plot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import microsim.dev.statistics.WeightedValues;
import org.jfree.data.statistics.HistogramType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CompletedWeightedHistogramTest {
    private final Weighted_HistogramSimulationPlotter plot =
            new Weighted_HistogramSimulationPlotter("Income", "Value", HistogramType.FREQUENCY, 2, 0.0, 4.0, true);

    @AfterEach
    void disposePlot() {
        plot.dispose();
    }

    @Test
    void readingThePlottedBinsDoesNotResampleTheModel() {
        var calls = new AtomicInteger();
        plot.addSource("People", () -> {
            calls.incrementAndGet();
            return new WeightedValues<>(List.of(1, 2, 3), List.of(0.5, 2.0, 3.0));
        });
        assertTrue(plot.getCompletedHistogram().isEmpty());
        assertEquals(0, calls.get());

        plot.update();
        var snapshot = plot.getCompletedHistogram();
        var series = snapshot.getFirst();
        assertEquals("People", series.name());
        assertEquals(List.of(0.0, 2.0, 4.0), series.binEdges());
        assertEquals(List.of(0.5, 5.0), series.values());
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, series.binEdges()::clear);
        assertThrows(UnsupportedOperationException.class, series.values()::clear);
        assertEquals(1, calls.get());
    }

    @Test
    void previousSnapshotIsReadableThroughoutAnUpdate() throws Exception {
        var calls = new AtomicInteger();
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        plot.addSource("People", () -> {
            int sample = calls.incrementAndGet();
            if (sample == 2) {
                entered.countDown();
                try {
                    assertTrue(release.await(10, TimeUnit.SECONDS), "Update timed out waiting for reader");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
            }
            return new WeightedValues<>(List.of(1, 3), List.of((double) sample, 2.0));
        });
        plot.update();

        try (var workers = Executors.newFixedThreadPool(2)) {
            var update = workers.submit(() -> plot.update());
            try {
                assertTrue(entered.await(5, TimeUnit.SECONDS));
                var previous = workers.submit(plot::getCompletedHistogram).get(2, TimeUnit.SECONDS);
                assertEquals(List.of(1.0, 2.0), previous.getFirst().values());
                release.countDown();
                update.get(5, TimeUnit.SECONDS);
                assertEquals(List.of(2.0, 2.0), plot.getCompletedHistogram().getFirst().values());
                assertEquals(List.of(1.0, 2.0), previous.getFirst().values());
            } finally {
                release.countDown();
            }
        }
    }

    @Test
    void aFailedSeriesLeavesTheLastCompleteSnapshotAvailable() {
        var calls = new AtomicInteger();
        plot.addSource("First", () -> new WeightedValues<>(List.of(1), List.of((double) calls.incrementAndGet())));
        plot.addSource("Second", () -> {
            if (calls.get() > 1) throw new IllegalStateException("Missing model data");
            return new WeightedValues<>(List.of(3), List.of(1.0));
        });
        plot.update();
        var previous = plot.getCompletedHistogram();
        assertEquals(List.of("First", "Second"), previous.stream().map(series -> series.name()).toList());

        assertThrows(IllegalStateException.class, plot::update);
        assertEquals(previous, plot.getCompletedHistogram());
    }
}
