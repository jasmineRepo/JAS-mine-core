package microsim.dev.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import microsim.SideEffect;

class Model {
    private int nstep = 0;
    private ArrayList<SideEffect> atStepEnd = new ArrayList<>();

    public void hookStepEnd(SideEffect se) {
        this.atStepEnd.add(se);
    }

    public void step() {
        this.nstep += 1;
        for (var se : this.atStepEnd) {
            se.call();
        }
    }

    public int nstep() {
        return this.nstep;
    }
}

class StatTests {
    @Test
    public void AccumulatorStatsTest() {
        var model = new Model();
        var stats = new AccumulatorStats(model::nstep, model::hookStepEnd);
        var n = 5;
        for (var i = 0; i < n; i++) {
            model.step();
        }
        assertEquals(n, stats.lastValue());
        assertEquals(n * (n + 1) / 2, stats.sum());
        assertEquals((double) (n + 1) / 2, stats.mean());
    }
}
