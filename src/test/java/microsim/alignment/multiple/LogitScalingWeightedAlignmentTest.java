package microsim.alignment.multiple;

import lombok.Setter;
import lombok.val;
import microsim.agent.Weight;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class LogitScalingWeightedAlignmentTest {
    static class A implements Weight{
        @Setter private double weight;
        @Override public double getWeight() {
            return weight;
        }
    }
    //get collection of As;

    static class B implements Predicate<A>{

        @Override
        public boolean test(A a) {
            return false;
        }

        @NotNull
        @Override
        public Predicate<A> and(@NotNull Predicate<? super A> other) {
            return Predicate.super.and(other);
        }

        @NotNull
        @Override
        public Predicate<A> negate() {
            return Predicate.super.negate();
        }

        @NotNull
        @Override
        public Predicate<A> or(@NotNull Predicate<? super A> other) {
            return Predicate.super.or(other);
        }
    }
    // get a filter B;

    static class C implements AlignmentMultiProbabilityClosure<A>{

        @Override
        public double[] getProbability(A agent) {
            //double
            //return agent.getWeight();
            return new double[0];
        }

        @Override
        public void align(A agent, double[] alignedProbability) {

        }
    }

    @Test
    void align() {
    }

    @Test
    void testAlign() {
    }

    @Test
    void validateInputData() {
    }

    @Test
    void extractAgentList() {
    }

    @Test
    void throwDivergenceError() {
    }

    @Test
    void recalculateProbabilityError() {
    }

    @Test
    void correctProbabilities() {
    }

    @Test
    void probabilityAdjustmentCycle() {
    }

    @Test
    void generateGammaValues() {
    }

    @Test
    void executeGammaTransform() {
    }

    @Test
    void executeAlphaTransform() {
        val probabilitiesSum = new double[1];
        val probabilities = new double[2][probabilitiesSum.length];
        val probabilitiesNull = new double[2][probabilitiesSum.length];
        probabilitiesNull[1] = null;

        assertAll("Should pass all sanity checks.",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, 0., probabilitiesSum,
                                probabilities), "alpha can't be 0. or negative."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, Double.POSITIVE_INFINITY,
                                probabilitiesSum, probabilities), "alpha can't be Infinity."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, Double.NEGATIVE_INFINITY,
                                probabilitiesSum, probabilities), "alpha can't be -Infinity."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, Double.NaN,
                                probabilitiesSum, probabilities), "alpha can't be NaN."),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(-1, 1., probabilitiesSum,
                                probabilities), "Agent id can't be negative."),
                () -> assertThrows(InputMismatchException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, 1., new double[3],
                                probabilities), "Input arrays have different sizes."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, 1., probabilitiesSum,
                                null), "One of the input arrays is null"),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, 1., null, probabilities),
                        "One of the input arrays is null"),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeAlphaTransform(1, 1., probabilitiesSum,
                                probabilitiesNull), "2d array contains null")
                );

            val ps = new double[]{0.3, 0.7};
            val p = new double[][]{new double[]{1., 1.}, new double[]{1., 1.}};
            val t = new LogitScalingWeightedAlignment<A>();
            t.executeAlphaTransform(1, 2., ps, p);
            assertArrayEquals(new double[][]{new double[]{1., 1.}, new double[]{2., 2.}}, p);
            assertArrayEquals(new double[]{2.3, 2.7}, ps);
    }

    @Test
    void getTotalChoiceNumber() {
    }

    @Test
    void getFilteredAgentList() {
    }

    @Test
    void getAgentNumber() {
    }

    @Test
    void getWeights() {
    }

    @Test
    void getProb() {
    }

    @Test
    void getTotal() {
    }

    @Test
    void getTarget() {
    }

    @Test
    void getErrorThreshold() {
    }

    @Test
    void getCount() {
    }

    @Test
    void getError() {
    }

    @Test
    void getProbSumOverAgents() {
    }

    @Test
    void getPreviousProbSumOverAgents() {
    }

    @Test
    void extractWeights() {
    }
}