package microsim.alignment.multiple;

import lombok.Getter;
import lombok.Setter;
import microsim.agent.Weight;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import static jamjam.Sum.sum;
import static org.junit.jupiter.api.Assertions.*;

class LogitScalingAlignmentTest {
    LogitScalingAlignment<A> testClass = new LogitScalingAlignment<>();

    static class A implements Weight {
        @Setter @Getter private double weight;
        @Setter @Getter double [] probability;
    }
    //get collection of As;

    static class B implements Predicate<A> {

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

    static class C implements AlignmentMultiProbabilityClosure<A> {

        @Override
        public double[] getProbability(A agent) {
            return agent.getProbability();
        }

        @Override
        public void align(A agent, double[] alignedProbability) {
            agent.setProbability(alignedProbability);
        }
    }

    static class Aprime {
        @Setter @Getter double [] probability;
    }

    static class Bprime implements Predicate<Aprime> {

        @Override
        public boolean test(Aprime a) {
            return false;
        }

        @NotNull
        @Override
        public Predicate<Aprime> and(@NotNull Predicate<? super Aprime> other) {
            return Predicate.super.and(other);
        }

        @NotNull
        @Override
        public Predicate<Aprime> negate() {
            return Predicate.super.negate();
        }

        @NotNull
        @Override
        public Predicate<Aprime> or(@NotNull Predicate<? super Aprime> other) {
            return Predicate.super.or(other);
        }
    }

    static class Cprime implements AlignmentMultiProbabilityClosure<Aprime> {

        @Override
        public double[] getProbability(Aprime agent) {
            return agent.getProbability();
        }

        @Override
        public void align(Aprime agent, double[] alignedProbability) {
            agent.setProbability(alignedProbability);
        }
    }

    @Test
    void align() {
        var scratchCollection = new ArrayList<A>();
        testClass.align(scratchCollection,
                null,
                new C(),
                new double[]{0.5, 0.5});
    }

    @Test
    void align1() {
        var n = 5;
        var scratchCollection = new ArrayList<A>();
        for (var i = 0; i < n; i++) {
            var scratch = new A();
            scratch.setWeight(1);
            scratch.setProbability(new double[]{0.1, 0.9});
            scratchCollection.add(scratch);
        }
        testClass.align(scratchCollection,
                null,
                new C(),
                new double[]{0.9, 0.1});
    }

    @Test
    void align2() {
        var n = 5;
        var scratchCollection = new ArrayList<A>();
        for (var i = 0; i < n; i++) {
            var scratch = new A();
            scratch.setWeight(i + 1);
            scratch.setProbability(new double[]{0.1, 0.9});
            scratchCollection.add(scratch);
        }
        testClass.align(scratchCollection,
                null,
                new C(),
                new double[]{0.9, 0.1});
    }

    @Test
    void align3() {
        var n = 5;
        var scratchCollection = new ArrayList<A>();
        for (var i = 0; i < n; i++) {
            var scratch = new A();
            scratch.setWeight(i + 1);
            scratch.setProbability(new double[]{0.1, 0.9});
            scratchCollection.add(scratch);
        }
        testClass.align(scratchCollection,
                null,
                new C(),
                new double[]{0.9, 0.1});
    }

    @Test
    void align4() {
        var scratchCollection = new ArrayList<Aprime>();
        LogitScalingAlignment<Aprime> localTestClass = new LogitScalingAlignment<>();
        localTestClass.align(scratchCollection,
                null,
                new Cprime(),
                new double[]{0.5, 0.5});
    }

    @Test
    void align5() {
        var n = 5;
        var scratchCollection = new ArrayList<Aprime>();
        for (var i = 0; i < n; i++) {
            var scratch = new Aprime();
            scratch.setProbability(new double[]{0.1, 0.9});
            scratchCollection.add(scratch);
        }
        LogitScalingAlignment<Aprime> localTestClass = new LogitScalingAlignment<>();
        localTestClass.align(scratchCollection,
                null,
                new Cprime(),
                new double[]{0.9, 0.1});
    }

    @Test
    void validateInputData1() {
        testClass.totalChoiceNumber = 1;
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData2() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0., 1.1};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
        testClass.targetDistribution = new double[]{-0.1, 0.5};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData3() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0., 0.9};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
        testClass.targetDistribution = new double[]{0., 1.1};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData4() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0.5, 0.5};
        testClass.weights = new double[]{-1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
        testClass.weights = new double[]{0, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
        testClass.weights = new double[]{Double.NaN, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
        testClass.weights = new double[]{Double.POSITIVE_INFINITY, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData5() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0.5, 0.5};
        testClass.weights = new double[]{1, 2, 3};
        testClass.probabilities = new double[][]{new double[]{0, 1}, new double[]{0, -1}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
        testClass.probabilities = new double[][]{new double[]{-1, 1}, new double[]{0, -1}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData6() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0.5, 0.5};
        testClass.weights = new double[]{1, 2, 3};
        testClass.probabilities = new double[][]{new double[]{0, 1, 1}, new double[]{1, 1, 0}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData7() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0.5, 0.5};
        testClass.weights = new double[]{1, 2, 3};
        testClass.tempAgents = new double[2];
        testClass.totalAgentNumber = 2;
        testClass.probabilities = new double[][]{new double[]{0, 1}, new double[]{0, 1}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());

        testClass.probabilities = new double[][]{new double[]{1, 0}, new double[]{0, 1}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());

        testClass.probabilities = new double[][]{new double[]{0.5, 0.5}, new double[]{0, 1}};
        testClass.validateInputData();
    }

    @Test
    void validateInputData8() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0.5, 0.5};
        testClass.weights = null;
        testClass.probabilities = new double[][]{new double[]{0, 1, 1}, new double[]{1, 1, 0}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void validateInputData9() {
        testClass.totalChoiceNumber = 2;
        testClass.targetDistribution = new double[]{0.5, 0.5};
        testClass.weights = null;
        testClass.probabilities = new double[][]{new double[]{0, -1, 1}, new double[]{1, 1, 0}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());

        testClass.probabilities = new double[][]{new double[]{0, 1, 1}, new double[]{1, 2, 0}};
        assertThrows(IllegalArgumentException.class, () -> testClass.validateInputData());
    }

    @Test
    void throwDivergenceError() {
        testClass.totalAgentNumber = 2;
        assertThrows(ArithmeticException.class, () -> testClass.throwDivergenceError(1., 10));
        assertThrows(ArithmeticException.class, () -> testClass.throwDivergenceError(LogitScalingAlignment.ERROR_THRESHOLD, 10));
        testClass.throwDivergenceError(0., 10);
    }

    @Test
    void generateGammaValues() {
        testClass.totalChoiceNumber = 4;
        testClass.gammaValues = new double[testClass.totalChoiceNumber];
        testClass.targetShare = new double[]{0.75, 0.75, 0.75, 0.75};
        testClass.probSumOverAgents = new double[]{23. / 40, 47. / 40, 9. / 40, 41. / 40};
        testClass.generateGammaValues();
        assertArrayEquals(new double[]{1.3043478260869565, 0.6382978723404255, 3.333333333333333, 0.7317073170731708},
                testClass.gammaValues);
    }

    @Test
    void generateAlphaValues() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.alphaValues = new double[testClass.totalAgentNumber];
        testClass.targetProbabilitySums = new double[]{1, 1, 1};
        testClass.probSumOverChoices = new double[]{1, 2, 3};
        testClass.generateAlphaValues();
        assertArrayEquals(new double[]{1, 0.5, 0.3333333333333333}, testClass.alphaValues);
    }

    @Test
    void executeGammaTransform() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.3, 0.2}};
        testClass.gammaValues = new double[]{2, 2, 2, 2};
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.executeGammaTransform();
        assertArrayEquals(new double[]{2, 2, 2}, testClass.probSumOverChoices);
    }

    @Test
    void executeGammaTransform1() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.3, 0.2}};
        testClass.gammaValues = new double[]{3, 3, 3, 3};
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.executeGammaTransform();
        assertArrayEquals(new double[]{3, 3, 3}, testClass.probSumOverChoices);
    }

    @Test
    void executeGammaTransform2() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.2, 0.3}};
        testClass.gammaValues = new double[]{2, 3, 3, 2};
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.executeGammaTransform();
        assertArrayEquals(new double[]{2.5, 2.5, 2.5}, testClass.probSumOverChoices);
    }

    @Test
    void executeAlphaTransform() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.3, 0.2}};
        testClass.alphaValues = new double[]{2, 2, 2};
        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.tempAgents = new double[testClass.totalAgentNumber];
        testClass.executeAlphaTransform();
        assertArrayEquals(new double[]{1.6, 2, 1.2, 1.2000000000000002}, testClass.probSumOverAgents);
    }

    @Test
    void executeAlphaTransform2() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.3, 0.2}};
        testClass.alphaValues = new double[]{3, 3, 3};
        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.tempAgents = new double[testClass.totalAgentNumber];
        testClass.executeAlphaTransform();
        assertArrayEquals(new double[]{2.4000000000000004, 3, 1.7999999999999998, 1.8000000000000003}, testClass.probSumOverAgents);
    }

    @Test
    void executeAlphaTransform3() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.3, 0.2}};
        testClass.alphaValues = new double[]{2, 3, 2};
        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.tempAgents = new double[testClass.totalAgentNumber];
        testClass.executeAlphaTransform();
        assertArrayEquals(new double[]{2.1, 2.5, 1.2, 1.2000000000000002}, testClass.probSumOverAgents);
    }

    @Test
    @DisplayName("Non-weighted main adjustment cycle")
    void probabilityAdjustmentCycle() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{0.5, 0.5, 0.0, 0.0},
                new double[]{0.2, 0.3, 0.3, 0.2}};
        testClass.targetShare = new double[]{0.75, 0.75, 0.75, 0.75};

        testClass.gammaValues = new double[testClass.totalChoiceNumber];
        testClass.alphaValues = new double[testClass.totalAgentNumber];

        testClass.tempAgents = new double[testClass.totalAgentNumber];

        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.targetProbabilitySums = new double[testClass.totalAgentNumber];
        Arrays.fill(testClass.targetProbabilitySums, 1.0);

        for (var choice = 0; choice < testClass.totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < testClass.totalAgentNumber; agentId++)
                testClass.tempAgents[agentId] = testClass.probabilities[agentId][choice];
            testClass.probSumOverAgents[choice] = sum(testClass.tempAgents);
        }

        testClass.probabilityAdjustmentCycle();

        assertArrayEquals(new double[]{0.9375, 0.75, 1.25, 1.2499999999999998}, testClass.gammaValues);
        assertArrayEquals(new double[]{1.11875, 0.84375, 1.0374999999999999}, testClass.probSumOverChoices);
        assertArrayEquals(new double[]{0.893854748603352, 1.1851851851851851, 0.9638554216867471},
                testClass.alphaValues);
        assertArrayEquals(new double[][]{
                        new double[]{0.08379888268156425, 0.13407821229050282, 0.3351955307262570, 0.44692737430167595},
                        new double[]{0.5555555555555556, 0.4444444444444444, 0., 0.},
                        new double[]{0.1807228915662651, 0.21686746987951808, 0.3614457831325302, 0.24096385542168675}},
                testClass.probabilities);

        assertArrayEquals(new double[]{0.44692737430167595, 0., 0.24096385542168675}, testClass.tempAgents);
        assertArrayEquals(new double[]{0.82007732980338486161, 0.79539012661446531003, 0.6966413138587872,
                0.68789122972336272464}, testClass.probSumOverAgents);
    }

    @Test
    @DisplayName("Weighted main adjustment cycle")
    void probabilityAdjustmentCycle1() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 4;
        testClass.probabilities = new double[][]{
                new double[]{0.1, 0.2, 0.3, 0.4},
                new double[]{1., 1., 0.0, 0.0},
                new double[]{0.6, 0.9, 0.9, 0.6}};
        testClass.targetShare = new double[]{0.75, 0.75, 0.75, 0.75};

        testClass.gammaValues = new double[testClass.totalChoiceNumber];
        testClass.alphaValues = new double[testClass.totalAgentNumber];

        testClass.tempAgents = new double[testClass.totalAgentNumber];

        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.targetProbabilitySums = new double[]{1, 2, 3};

        for (var choice = 0; choice < testClass.totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < testClass.totalAgentNumber; agentId++)
                testClass.tempAgents[agentId] = testClass.probabilities[agentId][choice];
            testClass.probSumOverAgents[choice] = sum(testClass.tempAgents);
        }

        testClass.probabilityAdjustmentCycle();

        assertArrayEquals(new double[]{0.44117647058823529412, 0.35714285714285714286, 0.62500000000000000000,
                0.75000000000000000000}, testClass.gammaValues);
        assertArrayEquals(new double[]{0.603046218487395, 0.79831932773109243697, 1.5986344537815125},
                testClass.probSumOverChoices);
        assertArrayEquals(new double[]{1.6582476920397143355, 2.5052631578947368421, 1.8766016164005521},
                testClass.alphaValues);
        assertArrayEquals(new double[][]{
                        new double[]{0.073157986413516808918, 0.11844626371712245253, 0.31092144225744643790, 0.49747430761191436},
                        new double[]{1.1052631578947367, 0.894736842105263, 0, 0},
                        new double[]{0.49674748669426383, 0.6031933767001775, 1.0555884092253104672, 0.84447072738024837374}},
                testClass.probabilities);

        assertArrayEquals(new double[]{0.49747430761191436, 0., 0.84447072738024837374}, testClass.tempAgents);
        assertArrayEquals(new double[]{1.6751686310025174003, 1.6163764825225630202, 1.3665098514827569051,
                1.3419450349921629}, testClass.probSumOverAgents);

    }

    @Test
    @DisplayName("Zero-one conservation w/o weights")
    void probabilityAdjustmentCycle2() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 3;
        testClass.probabilities = new double[][]{
                new double[]{0, 1, 0},
                new double[]{1, 0, 0},
                new double[]{0, 0, 1}};
        testClass.targetShare = new double[]{0.6, 0.6, 1.2};

        testClass.gammaValues = new double[testClass.totalChoiceNumber];
        testClass.alphaValues = new double[testClass.totalAgentNumber];

        testClass.tempAgents = new double[testClass.totalAgentNumber];

        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.targetProbabilitySums = new double[testClass.totalAgentNumber];
        Arrays.fill(testClass.targetProbabilitySums, 1.0);

        for (var choice = 0; choice < testClass.totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < testClass.totalAgentNumber; agentId++)
                testClass.tempAgents[agentId] = testClass.probabilities[agentId][choice];
            testClass.probSumOverAgents[choice] = sum(testClass.tempAgents);
        }

        testClass.probabilityAdjustmentCycle();

        assertArrayEquals(new double[]{0.6, 0.6, 1.2}, testClass.gammaValues);
        assertArrayEquals(new double[]{0.6, 0.6, 1.2}, testClass.probSumOverChoices);
        assertArrayEquals(new double[]{1.6666666666666667, 1.6666666666666667, 0.8333333333333334},
                testClass.alphaValues);
        assertArrayEquals(new double[][]{
                        new double[]{0, 1, 0},
                        new double[]{1, 0, 0},
                        new double[]{0, 0, 1}},
                testClass.probabilities);

        assertArrayEquals(new double[]{0, 0, 1}, testClass.tempAgents);
        assertArrayEquals(new double[]{1, 1, 1}, testClass.probSumOverAgents);
    }

    @Test
    @DisplayName("Zero-one conservation w/ weights")
    void probabilityAdjustmentCycle3() {
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 3;
        testClass.probabilities = new double[][]{
                new double[]{0, 1, 0},
                new double[]{2, 0, 0},
                new double[]{0, 0, 3}};
        testClass.targetShare = new double[]{0.6, 0.6, 1.2};

        testClass.gammaValues = new double[testClass.totalChoiceNumber];
        testClass.alphaValues = new double[testClass.totalAgentNumber];

        testClass.tempAgents = new double[testClass.totalAgentNumber];

        testClass.probSumOverAgents = new double[testClass.totalChoiceNumber];
        testClass.probSumOverChoices = new double[testClass.totalAgentNumber];
        testClass.targetProbabilitySums = new double[]{1, 2, 3};

        for (var choice = 0; choice < testClass.totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < testClass.totalAgentNumber; agentId++)
                testClass.tempAgents[agentId] = testClass.probabilities[agentId][choice];
            testClass.probSumOverAgents[choice] = sum(testClass.tempAgents);
        }

        testClass.probabilityAdjustmentCycle();

        assertArrayEquals(new double[]{0.3, 0.6, 0.39999999999999997}, testClass.gammaValues);
        assertArrayEquals(new double[]{0.6, 0.6, 1.2}, testClass.probSumOverChoices);
        assertArrayEquals(new double[]{1.6666666666666667, 3.3333333333333335, 2.5}, testClass.alphaValues);
        assertArrayEquals(new double[][]{
                        new double[]{0, 1, 0},
                        new double[]{2, 0, 0},
                        new double[]{0, 0, 3}},
                testClass.probabilities);

        assertArrayEquals(new double[]{0, 0, 3}, testClass.tempAgents);
        assertArrayEquals(new double[]{2, 1, 3}, testClass.probSumOverAgents);
    }

    @Test
    void extractWeights() {
        testClass.filteredAgentList = new ArrayList<>();
        testClass.weightedModel = false;
        assertNull(testClass.extractWeights());
    }

    @Test
    void extractWeights1() {
        var n = 15;
        testClass.filteredAgentList = new ArrayList<>();
        testClass.weightedModel = true;
        for (var i = 0; i < n; i++) {
            var scratch = new A();
            scratch.setWeight(i);
            testClass.filteredAgentList.add(scratch);
        }
        double[] result = testClass.extractWeights();
        assertNotNull(result);
        assertEquals(n, result.length);
        for (var i = 0; i < n; i++) {
            assertEquals(i * 1.0, result[i]);
        }
    }

    @Test
    void weightedModel() {
        var n = 3;
        testClass.filteredAgentList = new ArrayList<>();
        testClass.weightedModel = true;
        for (var i = 0; i < n; i++) {
            var scratch = new A();
            scratch.setWeight(i);
            testClass.filteredAgentList.add(scratch);
        }
        assertTrue(testClass.isWeighted(testClass.filteredAgentList));
        assertThrows(IllegalArgumentException.class, () -> testClass.isWeighted(new ArrayList<>()));
    }

    @Test
    void weightedModel1() {
        var n = 3;
        LogitScalingAlignment<Aprime> tc = new LogitScalingAlignment<>();

        tc.filteredAgentList = new ArrayList<>();
        tc.weightedModel = false;
        for (var i = 0; i < n; i++) tc.filteredAgentList.add(new Aprime());
        assertFalse(tc.isWeighted(tc.filteredAgentList));
        assertThrows(IllegalArgumentException.class, () -> testClass.isWeighted(new ArrayList<>()));
    }

    @Test
    void correctProbabilities() {
        testClass.weights = null;
        var scratchClosure = new C();
        testClass.totalAgentNumber = 3;
        testClass.filteredAgentList = new ArrayList<>();
        for (var i = 0; i < testClass.totalAgentNumber; i++) {
            var scratch = new A();
            scratch.setProbability(new double[]{1, 2, 3});
            testClass.filteredAgentList.add(scratch);
        }
        testClass.probabilities = new double[][]{
                new double[]{0, 1, 0},
                new double[]{1, 0, 0},
                new double[]{0, 0, 1}
        };

        testClass.correctProbabilities(scratchClosure);
        for (var i = 0; i < testClass.totalAgentNumber; i++) {
            assertArrayEquals(testClass.probabilities[i], testClass.filteredAgentList.get(i).getProbability());
        }
    }

    @Test
    void correctProbabilities1() {
        testClass.weights = new double[]{1, 2, 3};
        var scratchClosure = new C();
        testClass.totalAgentNumber = 3;
        testClass.totalChoiceNumber = 3;
        testClass.filteredAgentList = new ArrayList<>();
        for (var i = 0; i < testClass.totalAgentNumber; i++) {
            var scratch = new A();
            scratch.setProbability(new double[]{1, 2, 3});
            testClass.filteredAgentList.add(scratch);
        }
        testClass.probabilities = new double[][]{
                new double[]{0, 1, 0},
                new double[]{2, 0, 0},
                new double[]{0, 0, 3}
        };

        testClass.correctProbabilities(scratchClosure);
        for (var i = 0; i < testClass.totalAgentNumber; i++) {
            assertArrayEquals(testClass.probabilities[i], testClass.filteredAgentList.get(i).getProbability());
        }
    }
}
