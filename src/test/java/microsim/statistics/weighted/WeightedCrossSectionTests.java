package microsim.statistics.weighted;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import microsim.agent.Weight;


record Agent(double value, double weight) implements Weight {
    @Override
    public double getWeight() {
        return this.weight;
    }
}


class WeightedCrossSectionTests {
    @Test
    void doubleIntrospection() {
        var agents = new ArrayList<Agent>();
        agents.add(new Agent(1.5, 1.0));
        agents.add(new Agent(0.2, 1.3));

        var wcs = new Weighted_CrossSection.Double(agents, Agent.class, "value", true);
        wcs.setCheckingTime(false);

        wcs.updateSource();
        assertArrayEquals(wcs.getDoubleArray(), new double[]{1.5, 0.2});
        assertArrayEquals(wcs.getWeights(), new double[]{1.0, 1.3});

        agents.add(new Agent(3.5, 2.6));
        wcs.updateSource();
        assertArrayEquals(wcs.getDoubleArray(), new double[]{1.5, 0.2, 3.5});
        assertArrayEquals(wcs.getWeights(), new double[]{1.0, 1.3, 2.6});
    }
}
