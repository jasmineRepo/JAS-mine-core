package microsim.alignment.multiple;

import lombok.Setter;
import lombok.val;
import microsim.agent.Weight;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

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


        val q = new LogitScalingWeightedAlignment<A>();
        //executeAlphaTransform();
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