package microsim.alignment.multiple;

import lombok.Setter;
import lombok.val;
import microsim.agent.Weight;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        val stub = new double[]{1., 1.};
        assertAll("Should pass all basic null checks.",
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(null, 2, 1., stub),
                        "Target share null test fails."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(stub, 2, 1., null),
                        "Weights null check fails.")
        );

        assertAll("Target values are in range.",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0., 2.}, 2, 1.,
                                stub), "One of elements is above 1."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{2., 0.}, 2, 1.,
                                stub), "Changing the order of elements breaks initial data validation."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{-1., 0.}, 2, 1.,
                                stub), "Testing negative elements fails.")
                );

        assertThrows(IllegalArgumentException.class,
                () -> new LogitScalingWeightedAlignment<A>().validateInputData(stub, 2, 1., stub),
                "Non-normalized probabilities.");

        assertThrows(IllegalArgumentException.class,
                () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 0, 1., stub),
                "Incorrect number of iterations.");

        assertAll("Precision values should be sane.",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2, -1.,
                                stub), "Precision is negative."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2,
                                Double.POSITIVE_INFINITY, stub), "Precision is infinite."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2,
                                Double.NaN, stub), "Precision is NaN.")
                );
        assertAll("Weights should be sane.",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2, 1.,
                                new double[]{0., 1., 1.}), "Weight is zero."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2, 1.,
                                new double[]{1., 0., 1.}), "Order of arguments has to be irrelevant."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2, 1.,
                                new double[]{Double.POSITIVE_INFINITY, 1., 1.}), "Weight is off the charts."),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new LogitScalingWeightedAlignment<A>().validateInputData(new double[]{0.5, 0.5}, 2, 1.,
                                new double[]{Double.NaN, 1., 1.}), "Weight is NaN.")
        );
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
        val testOld = new double[3];
        val testNew = new double[]{1., 1., 1.};
        val testTarget = new double[]{1., 1.};
        val testAgents = new double[]{1., 1.,};
        val testProbabilities = new double[][]{new double[]{1., 1.}, new double[]{1., 1.}};

        assertAll("Should pass all basic null checks.",
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(null, testNew, testTarget,
                                testAgents, testProbabilities), "Old sum storage is null."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, null, testTarget,
                                testAgents, testProbabilities), "New sum storage is null."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, null,
                                testAgents, testProbabilities), "Target array is null."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, testTarget,
                                null, testProbabilities), "Temporary array is null."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, testTarget,
                                testAgents, null), "Probabilities are null.")
                );

        assertThrows(IllegalArgumentException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, new double[1],
                        testAgents, testProbabilities), "Target array is too small.");
        assertThrows(IllegalArgumentException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, testTarget,
                        new double[1], testProbabilities), "Temporary array is too small.");

        assertThrows(InputMismatchException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, new double[4], testTarget,
                        testAgents, testProbabilities), "Sum storage size mismatch.");

        assertThrows(InputMismatchException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, new double[4],
                        testAgents, testProbabilities), "Sum/target storage size mismatch.");

        assertThrows(NullPointerException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, new double[3],
                        testAgents, new double[][]{new double[]{1., 1.}, null}), "Null sub-arrays.");
        assertThrows(NullPointerException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, new double[3],
                        testAgents, new double[][]{null, new double[]{1., 1.}}),
                "Null sub-arrays, reordering arguments breaks the check.");
        assertThrows(InputMismatchException.class,
                () -> new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, new double[3],
                        new double[3], testProbabilities), "Uneven array of probabilities.");

        new LogitScalingWeightedAlignment<A>().generateGammaValues(testOld, testNew, new double[3], new double[3],
                new double[][]{new double[]{1., 1., 1}, new double[]{1., 1., 1}, new double[]{1., 1., 1.}});

        assertArrayEquals(testNew, testOld, "Memory copy fails");
        assertArrayEquals(testNew, new double[]{1., 1., 1.}, "Probabilities should not change.");

        val t = new double[]{1., 1., 1., 1.};
        val g = new LogitScalingWeightedAlignment<A>().generateGammaValues(new double[4], new double[4],
                new double[]{1., 1., 1., 1.}, t, new double[][]{new double[]{1., 1., 1., 1.},
                        new double[]{1., 1., 1., 1.}, new double[]{1., 2., 3., 4.}, new double[]{1., 2., 3., 4.}});

        assertArrayEquals(t, new double[]{1., 1., 4., 4.}, "First step fails.");

        assertArrayEquals(g, new double[]{1. / 4., 1. / 6., 1. / 8., 1. / 10.}, "gamma calculation fails.");
    }

    @Test
    void executeGammaTransform() {
        val gv = new double[2];
        val p = new double[2][2];
        p[1] = null;

        assertAll("Should pass all sanity checks.",
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeGammaTransform(1, gv, p),
                        "Array of probabilities can't contain null sub-array."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeGammaTransform(1, null, p),
                "Null check fails."),
                () -> assertThrows(NullPointerException.class,
                        () -> new LogitScalingWeightedAlignment<A>().executeGammaTransform(1, gv, null),
                "Second null check fails.")
        );
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> new LogitScalingWeightedAlignment<A>().executeGammaTransform(-1, gv, p),
                "Array index/agent id can't be negative.");
        assertThrows(InputMismatchException.class,
                () -> new LogitScalingWeightedAlignment<A>().executeGammaTransform(0, new double[1], p),
                "Dimensions of input arrays do not match.");

        val t = new LogitScalingWeightedAlignment<A>();
        assertEquals(10., t.executeGammaTransform(1, new double[]{2., 2.}, new double[][]{new double[]{3., 2.},
                new double[]{3., 2.}}), "Calculations went wrong.");
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
            assertArrayEquals(new double[][]{new double[]{1., 1.}, new double[]{2., 2.}}, p, "Incorrect calculations.");
            assertArrayEquals(new double[]{2.3, 2.7}, ps, "Incorrect calculations.");
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