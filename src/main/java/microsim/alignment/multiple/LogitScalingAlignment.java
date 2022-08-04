package microsim.alignment.multiple;

import lombok.NonNull;
import lombok.val;
import microsim.agent.Weight;
import microsim.alignment.AlignmentUtils;
import org.apache.commons.collections4.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static jamjam.Sum.sum;
import static jamjam.probability.StatisticalDistance.KullbackLeiblerDivergence;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.String.format;

/**
 * Multinomial alignment methods, where there is in general a set 'A' (>=2) of possible outcomes/states to align.
 * This algorithm is called *Logit Scaling* and is based on the minimization of the information loss (relative entropy).
 * This is an abstract class which can be applied to both weighted and non-weighted cases. Additionally, it can be used
 * in conventional binary alignment problems.
 *
 * @param <T> The Type parameter usually representing the agent class.
 * @implSpec Getters are introduced with the purpose of documenting corresponding variables via {@code lombok}. This
 * way, their meaning is not hidden in commented lines, but accessible with the help of Javadoc. Do *NOT*
 * remove them.
 * @implNote The design of this class suboptimal at the moment to make all class implementations share as much code as
 * possible. In particular, when agents have no weight, they all automatically get one that is equal to 1.
 * This code extends the original algorithm by adding the case of weighted samples. It also does add a filter
 * for the whole population to align only the relevant subpopulation.
 * <p>
 * In the degenerate case when all probabilities are given as open-point distributions this algorithm fails to
 * converge. This happens due to the fact it conserves 0s & 1s, thus, the entire array of probabilities does
 * not change.
 * @see <a href="https://ideas.repec.org/a/ijm/journl/v9y2016i3p89-102.html">Peter Stephensen, A General Method
 * for Alignment in Microsimulation models, International Journal of Microsimulation (2016) 9(3) 89-102</a>
 */
public class LogitScalingAlignment<T> implements AlignmentUtils<T> {
    final static double ERROR_THRESHOLD = 1e-15;

    /**
     * The expected discrete probability distribution.
     */
    double[] targetDistribution;

    /**
     * The number of possible outcomes of a given event that is the length of the {@code targetShare} parameter.
     */
    int totalChoiceNumber;

    /**
     * A list of agents after applying {@link #extractAgentList(Collection, Predicate)} and {@code filter}.
     */
    List<T> filteredAgentList;

    /**
     * The total number of agents after passing the {@code filter}, obtained as the size of {@link #filteredAgentList}.
     */
    int totalAgentNumber;

    /**
     * An array of agent weights, that has the same size as the array returned by {@link #filteredAgentList}.
     * {@code null} when there is no weights at all.
     */
    double[] weights;

    /**
     * A 2D array of probabilities, every value represents an agent/outcome probability. The size is defined as {@link
     * #totalAgentNumber}x{@link #totalChoiceNumber}. All values are updated during simulation.
     */
    double[][] probabilities;

    /**
     * The sum of all weights of all agents, retrieved by passing {@link #weights} to {@link jamjam.Sum#sum(double...)}.
     */
    double total;

    /**
     * The target share of the relevant subpopulation. Sums of the aligned probabilities must be equal to these values
     * that can be greater than 1.
     */
    double[] targetShare;

    /**
     * An array of size {@link #totalChoiceNumber}, containing sums of probabilities over {@code agents} per choice
     * at current iteration.
     */
    double[] probSumOverAgents;

    /**
     * An array of size {@link #totalAgentNumber}, containing sums of probabilities over {@code choices} per agent at
     * current iteration.
     */
    double[] probSumOverChoices;

    /**
     * A {@code boolean} showing if the model has to deal with weighted data.
     */
    boolean weightedModel;

    /**
     * An array containing alpha values.
     */
    double[] alphaValues;

    /**
     * An array containing gamma values.
     */
    double[] gammaValues;

    /**
     * A temporary array containing a certain property of all agents.
     */
    double[] tempAgents;

    /**
     * A temporary array containing sums of probabilities, i.e., 1 if there is no weights, or weights otherwise.
     */
    double[] targetProbabilitySums;

    /**
     * General alignment procedure, it adjusts probabilities using all the provided parameters until the algorithm
     * reaches the target precision/number of iterations.
     *
     * @param agents                        A collection of agents of a given type. Most commonly, every agent is a
     *                                      person, however, this class does not limit its usage to humans only.
     * @param filter                        A filter to select a subpopulation from a given collection of
     *                                      {@code agents}.
     * @param closure                       An object that specifies how to get the (unaligned) probability of the agent
     *                                      and how to set the result of the aligned probability.
     * @param targetProbabilityDistribution The target discrete probability distribution. Means of the aligned
     *                                      probabilities must be equal to these values.
     * @implNote The total number of iterations defaults to 50. The error threshold of the numerical scheme is 1e-15.
     */
    final public void align(@NonNull Collection<T> agents, @Nullable Predicate<T> filter,
                            @NonNull AlignmentMultiProbabilityClosure<T> closure,
                            double @NonNull [] targetProbabilityDistribution) {
        if (agents.isEmpty()) return;
        targetDistribution = targetProbabilityDistribution;
        weightedModel = isWeighted(agents);

        totalChoiceNumber = targetDistribution.length;

        filteredAgentList = extractAgentList(agents, filter);
        totalAgentNumber = filteredAgentList.size();
        if (totalAgentNumber == 0) return;

        tempAgents = new double[totalAgentNumber];

        weights = extractWeights();
        total = weights == null ? 1.0d * totalAgentNumber : sum(weights);

        probabilities = new double[totalAgentNumber][totalChoiceNumber];

        for (var agentId = 0; agentId < totalAgentNumber; agentId++)
            probabilities[agentId] = closure.getProbability(filteredAgentList.get(agentId));

        validateInputData();

        targetProbabilitySums = new double[totalAgentNumber];
        if (weightedModel) System.arraycopy(weights, 0, targetProbabilitySums, 0, totalAgentNumber);
        else Arrays.fill(targetProbabilitySums, 1.0);

        if (weightedModel)
            for (var agentId = 0; agentId < totalAgentNumber; agentId++)
                for (int choice = 0; choice < totalChoiceNumber; choice++)
                    probabilities[agentId][choice] *= weights[agentId];

        targetShare = new double[totalChoiceNumber];

        gammaValues = new double[totalChoiceNumber];
        alphaValues = new double[totalAgentNumber];

        for (var choice = 0; choice < totalChoiceNumber; choice++)
            targetShare[choice] = targetDistribution[choice] * total;

        probSumOverAgents = new double[totalChoiceNumber];
        probSumOverChoices = new double[totalAgentNumber];
        for (var choice = 0; choice < totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < totalAgentNumber; agentId++)
                tempAgents[agentId] = probabilities[agentId][choice];
            probSumOverAgents[choice] = sum(tempAgents);
        }
        double error = 0.;
        val actualProbabilityDistribution = new double[totalChoiceNumber];
        short iteration;
        for (iteration = 0; iteration < 100; iteration++) {
            probabilityAdjustmentCycle();

            for (var choiceId = 0; choiceId < totalChoiceNumber; choiceId++)
                actualProbabilityDistribution[choiceId] = probSumOverAgents[choiceId] / total;

            error = KullbackLeiblerDivergence(targetDistribution, actualProbabilityDistribution);
            if (error <= ERROR_THRESHOLD)
                break;
        }
        throwDivergenceError(error, iteration);
        correctProbabilities(closure);
    }

    /**
     * Checks if all agents have corresponding weights, i.e., {@link Weight} implemented.
     *
     * @param agentCollection A collection of objects representing agents.
     * @return {@code true} when all of them have weights, {@code false} if none of them have.
     * @throws IllegalArgumentException when {@code agentCollection} is empty.
     * @implNote Introduced to avoid all the hassle with multiple classes. Mixes of weighted and non-weighted objects
     * are not possible. In the case of an empty list returns false for the sake of consistency. The actual value is
     * irrelevant in this case since any empty agent list causes early exit.
     */
    boolean isWeighted(@NotNull Collection<T> agentCollection) {
        if (agentCollection.isEmpty())
            throw new IllegalArgumentException("Empty input is ambiguous.");
        val scratch = agentCollection.iterator().next();
        return scratch instanceof Weight;
    }

    /**
     * Extracts weights - if any - from individual agents and stores them in a single array.
     *
     * @return An array with weights or {@code null}.
     */
    double @Nullable [] extractWeights() {
        int l = filteredAgentList.size();
        val w = weightedModel ? new double[l] : null;

        if (weightedModel) for (int i = 0; i < l; i++) w[i] = ((Weight) filteredAgentList.get(i)).getWeight();
        return w;
    }

    /**
     * Does a basic input data validation. Sees that probabilities are in range in [0,1], their sum is not greater than
     * 1; also checks that weights and precision are strictly positive. Steps in before weight scaling.
     *
     * @throws IllegalArgumentException When sanity checks fail:
     *                                  {@code totalChoiceNumber} is 0 or 1,
     *                                  {@code targetDistribution} values are out of range [0,1],
     *                                  {@code sum(targetDistribution) != 1},
     *                                  any element of {@code weights} is {@code <= 0, NaN, or Inf},
     *                                  {@code probabilities} contains values out of range [0,1],
     *                                  for any agent the sum of all probabilities is not 1,
     *                                  impossible event, i.e., for *every* agent the probability of a particular
     *                                  outcome is strictly zero,
     *                                  the probability array is binary (contains 0 and 1 only) as the method does not
     *                                  converge.
     */
    final void validateInputData() {
        if (totalChoiceNumber < 2)
            throw new IllegalArgumentException("The number of outcomes must be at least 2.");
        for (var v : targetDistribution)
            if (v < 0. || v > 1.)
                throw new IllegalArgumentException("Each probability value must lie in [0,1].");
        if (sum(targetDistribution) != 1.)
            throw new IllegalArgumentException("Sum of all outcomes must be 1 by definition.");
        if (weights != null)
            for (var weight : weights)
                if (weight <= 0. || isNaN(weight) || isInfinite(weight))
                    throw new IllegalArgumentException("Agent's weight cannot be <= 0, NaN, or Inf.");

        for (var p : probabilities)
            for (var v : p)
                if (v < 0. || v > 1.)
                    throw new IllegalArgumentException("Each probability value must lie in [0,1].");

        for (var p : probabilities)
            if (sum(p) != 1)
                throw new IllegalArgumentException("Sum of all outcomes must be 1 by definition.");


        for (var choice = 0; choice < totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < totalAgentNumber; agentId++)
                tempAgents[agentId] = probabilities[agentId][choice];
            if (sum(tempAgents) == 0.)
                throw new IllegalArgumentException("Absolutely impossible event.");
        }

        for (var agentId = 0; agentId < totalAgentNumber; agentId++) {
            val p = probabilities[agentId];
            tempAgents[agentId] = 0.;
            for (var v : p)
                if (v != 0. && v != 1.) {
                    tempAgents[agentId] = 1.;
                    break;
                }
        }
        if (sum(tempAgents) == 0.)
            throw new IllegalArgumentException("Poorly picked data with static events: contains 0 and 1 only," +
                    " does not converge.");
    }

    /**
     * Throws an error message when the method doesn't converge to the expected solution of the problem within a given
     * range of iterations.
     *
     * @param errorValue Actual error.
     * @param iterations Total number of passed iterations at the moment of termination.
     * @throws ArithmeticException When the main iterative condition is not satisfied; when double values are NaN, Inf.
     */
    final void throwDivergenceError(final double errorValue, final int iterations) {
        if (errorValue >= ERROR_THRESHOLD) {
            String className = this.getClass().getCanonicalName();
            throw new ArithmeticException(format("""
                    WARNING: The %s align() method terminated with an error of %f, which has a greater magnitude than
                     the precision bounds of +/-%f. The size of the filtered agent collection is %d and the number of
                     iterations was %d. Check the results of the %s alignment to ensure that alignment is good enough
                     for the purpose in question, or consider changing the criteria for agent selection.
                    """, className, errorValue, ERROR_THRESHOLD, totalAgentNumber, iterations, className));
        }
    }

    /**
     * Scales down {@link #probabilities} to 're-normalise', as it was previously scaled up by weight. Replaces
     * individual probabilities with the aligned probabilities {@link #probabilities}.
     *
     * @param closure An instance of implementation of
     *                {@link microsim.alignment.multiple.AlignmentMultiProbabilityClosure}.
     * @implNote No need to check for division by zero as all such values are filtered out earlier.
     */
    final void correctProbabilities(final @NotNull AlignmentMultiProbabilityClosure<T> closure) {
        if (weights != null) for (var i = 0; i < totalAgentNumber; i++)
            for (var choice = 0; choice < totalChoiceNumber; choice++) probabilities[i][choice] /= weights[i];

        for (var i = 0; i < totalAgentNumber; i++) closure.align(filteredAgentList.get(i), probabilities[i]);
    }

    /**
     * Adjusts the probabilities via consecutive use of {@link #executeGammaTransform} and
     * {@link #executeAlphaTransform}.
     */
    final void probabilityAdjustmentCycle() {
        generateGammaValues();
        executeGammaTransform();

        generateAlphaValues();
        executeAlphaTransform();
    }

    /**
     * Generates an array with gamma values.
     *
     * @implSpec {@code agentSizeArray} is introduced to avoid extra memory handling as this method is called during
     * every iteration.
     * @implNote No division by zero is handled, it's assumed all data of such kind is not valid and thus blocked from
     * processing earlier.
     */
    final void generateGammaValues() {
        for (var choice = 0; choice < totalChoiceNumber; choice++)
            gammaValues[choice] = targetShare[choice] / probSumOverAgents[choice];
    }

    /**
     * Generates corresponding alpha values.
     *
     * @implNote No division by zero here if the data is valid: the sum of all probabilities is 1 by definition. In the
     * case of a weighted population it's not 1 but the corresponding weight value that is not 0 anyway.
     */
    final void generateAlphaValues() {
        for (var agentId = 0; agentId < totalAgentNumber; agentId++)
            alphaValues[agentId] = targetProbabilitySums[agentId] / probSumOverChoices[agentId];
    }

    /**
     * Gamma transform of the probabilities. It scales all columns by corresponding elements of {@code gammaValues} in
     * such a way that the total sum of every column matches the target value.
     */
    final void executeGammaTransform() {
        for (var agentId = 0; agentId < totalAgentNumber; agentId++) {
            for (var choice = 0; choice < totalChoiceNumber; choice++)
                probabilities[agentId][choice] *= gammaValues[choice];
            probSumOverChoices[agentId] = sum(probabilities[agentId]);
        }
    }

    /**
     * Alpha transform of the probabilities. Each row gets multiplied by the corresponding coefficient {@code alpha} so
     * that the sum of all elements in the row adds up exactly to 1 when there is no weights or corresponding weight
     * otherwise. In addition, calculates the sum of probabilities over agents.
     */
    final void executeAlphaTransform() {
        for (var agentId = 0; agentId < totalAgentNumber; agentId++)
            for (var choice = 0; choice < totalChoiceNumber; choice++)
                probabilities[agentId][choice] *= alphaValues[agentId];

        for (var choice = 0; choice < totalChoiceNumber; choice++) {
            for (var agentId = 0; agentId < totalAgentNumber; agentId++)
                tempAgents[agentId] = probabilities[agentId][choice];
            probSumOverAgents[choice] = sum(tempAgents);
        }
    }
}
