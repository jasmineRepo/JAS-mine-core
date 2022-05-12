package microsim.alignment.multiple;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import microsim.alignment.AlignmentUtils;
import org.apache.commons.collections4.Predicate;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.List;

import static jamjam.Mean.mean;
import static jamjam.Sum.sum;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.String.format;
import static java.lang.System.arraycopy;

/**
 * Multinomial alignment methods, where there is in general a set 'A' (>=2) of possible outcomes/states to align.
 * This algorithm is called *Logit Scaling* and is based on the minimization of the information loss (relative entropy).
 * This is an abstract class which can be applied to both weighted and non-weighted cases. Additionally, it can be used
 * in conventional binary alignment problems.
 *
 * @implSpec Getters are introduced with the purpose of documenting corresponding variables via {@code lombok}. This
 *           way, their meaning is not hidden in commented lines, but accessible with the help of Javadoc. Do *NOT*
 *           remove them.
 *
 * @implNote The design of this class suboptimal at the moment to make all class implementations share as much code as
 *           possible. In particular, when agents have no weight, they all automatically get one that is equal to 1.
 *           This code extends the original algorithm by adding the case of weighted samples. It also does add a filter
 *           for the whole population to align only the relevant subpopulation.
 *
 *           In the degenerate case when all probabilities are given as open-point distributions this algorithm fails to
 *           converge. This happens due to the fact it conserves 0s & 1s, thus, the entire array of probabilities does
 *           not change.
 *
 * @param <T> The Type parameter usually representing the agent class.
 *
 * @see <a href="https://ideas.repec.org/a/ijm/journl/v9y2016i3p89-102.html">Peter Stephensen, A General Method
 *      for Alignment in Microsimulation models, International Journal of Microsimulation (2016) 9(3) 89-102</a>
 */
public abstract class AbstractLogitScalingAlignment<T> implements AlignmentUtils<T> {
    /**
     * @return totalChoiceNumber The number of possible outcomes of a given event that is the length of
     *                           the {@code targetShare} parameter.
     */
    @Getter(AccessLevel.PACKAGE) private int totalChoiceNumber;

    /**
     * @return filteredAgentList A list of agents after applying {@link #extractAgentList(Collection, Predicate)} and
     *                           {@code filter}.
     */
    @Getter(AccessLevel.PACKAGE) private List<T> filteredAgentList;

    /**
     * @return agentNumber The total number of agents after passing the {@code filter}, obtained as the size of
     *                     {@link #getFilteredAgentList()}.
     */
    @Getter(AccessLevel.PACKAGE) private int agentNumber;

    /**
     * @return weights An array of agent weights, that has the same size as the array returned by
     *                 {@link #getFilteredAgentList()}. Actual values depend on the implementation of
     *                 {@link #extractWeights(double[] w)}.
     */
    @Getter(AccessLevel.PACKAGE) private double[] weights;

    /**
     * @return prob A 2D array of probabilities, every value represents an agent/outcome probability. The size is
     *              defined as {@link #getAgentNumber()}x{@link #getTotalChoiceNumber()}. All values are updated during
     *              simulation.
     */
    @Getter(AccessLevel.PACKAGE) private double[][] prob;

    /**
     * @return total The sum of all weights of all agents, retrieved by passing {@link #getWeights()} to {@link
     *               jamjam.Sum#sum(double...)}.
     */
    @Getter(AccessLevel.PACKAGE) private double total;

    /**
     * @return target The target share of the relevant subpopulation. Sums of the aligned probabilities must be equal
     *                to these values.
     */
    @Getter(AccessLevel.PACKAGE) private double[] targetShare;

    /**
     * @return errorThreshold Cumulative error threshold of the iterative scheme. Calculated for the whole
     *                        subpopulation.
     */
    @Getter(AccessLevel.PACKAGE) private double errorThreshold;

    /**
     * @return count The number of iterations passed.
     */
    @Getter(AccessLevel.PACKAGE) private int count = 0;

    /**
     * @return error Difference between actual and expected probabilities at each iteration.
     */
    @Getter(AccessLevel.PACKAGE) private double error = Double.MAX_VALUE;

    /**
     * @return probSumOverAgents An array of size {@link #getTotalChoiceNumber()}, containing sums of
     *                           probabilities over {@code agents} per choice at current iteration.
     */
    @Getter(AccessLevel.PACKAGE) private double[] probSumOverAgents;

    /**
     * @return previousProbSumOverAgents An array of size {@link #getTotalChoiceNumber()}, containing sums of
     *                                   probabilities over {@code agents} per choice at previous iteration.
     */
    @Getter(AccessLevel.PACKAGE) private double[] previousProbSumOverAgents;

    /**
     * General alignment procedure, it adjusts probabilities using all the provided parameters until the algorithm
     * reaches the target precision/number of iterations.
     * @param agents A collection of agents of a given type. Most commonly, every agent is a person,
     *               however, this class does not limit its usage to humans only.
     * @param filter A filter to select a subpopulation from a given collection of {@code agents}.
     * @param closure An object that specifies how to get the (unaligned) probability of the agent and how to set the
     *                result of the aligned probability.
     * @param targetProbabilityDistribution The target share of the relevant subpopulation (specified as a proportion of
     *                                      {@code agents} filtered with {@code filter} population). Means of the
     *                                      aligned probabilities must be equal to these values.// fixme rewrite this
     * @param maxNumberIterations The maximum number of iterations until the iterative loop in the alignment algorithm
     *                            terminates. The resulting probabilities at that time are then used
     * @param precision The appropriate value here depends on the precision of the probabilities. If the probabilities
     *                  are stated to x decimal places, then it should be 1.e-x.
     * @param warningsOn Enables or disables the warning thrown by {@link #throwDivergenceError(double, boolean, double,
     *                   double, double, int, int)}.
     */
    final public void align(Collection<T> agents, Predicate<T> filter, AlignmentMultiProbabilityClosure<T> closure,
                            double @NonNull [] targetProbabilityDistribution, int maxNumberIterations, double precision,
                            boolean warningsOn){
        // todo sanity check - has to have at least two choices. Do we need it everywhere?

        totalChoiceNumber = targetProbabilityDistribution.length;

        filteredAgentList = extractAgentList(agents, filter);
        agentNumber = filteredAgentList.size();

        val tempAgents = new double[agentNumber];

        weights = new double [getFilteredAgentList().size()];
        extractWeights(weights);

        validateInputData(targetProbabilityDistribution, maxNumberIterations, precision, weights);
        
        prob = new double[agentNumber][totalChoiceNumber];

        total = sum(weights);

        for (var i = 0; i < agentNumber; i++) {
            var agent = filteredAgentList.get(i);
            for(int choice = 0; choice < totalChoiceNumber; choice++)
                prob[i][choice] = closure.getProbability(agent)[choice] * weights[i];
        }

        targetShare = new double[totalChoiceNumber];
        for(var choice = 0; choice < totalChoiceNumber; choice++)
            targetShare[choice] = targetProbabilityDistribution[choice] * total;
        errorThreshold = precision * total;

        probSumOverAgents = new double[totalChoiceNumber];
        previousProbSumOverAgents = new double[totalChoiceNumber];

        while(error >= errorThreshold && count < maxNumberIterations) {
            probabilityAdjustmentCycle(previousProbSumOverAgents, probSumOverAgents, targetShare, weights, tempAgents,
                                       prob);
            error = recalculateProbabilityError(previousProbSumOverAgents, probSumOverAgents);
            count++;
        }
        throwDivergenceError(precision, warningsOn, error, errorThreshold, total, agentNumber, count);
        correctProbabilities(closure, filteredAgentList, weights, prob);
    }

    /**
     * This method uses pre-set alignment parameters that define its precision (1.e-15) and the number of
     * iterations (50), it further passes the parameters to the full {@link #align(Collection, Predicate,
     * AlignmentMultiProbabilityClosure, double[], int, double, boolean) align} method.
     * Use the {@link #align(Collection, Predicate, AlignmentMultiProbabilityClosure, double[], int, double, boolean)
     * align} method if these parameters are to be adjusted.
     * @param agents A collection of agents of a given type. Most commonly, every agent is a person,
     *               however, this class does not limit its usage to humans only.
     * @param filter A filter to select a subpopulation from a given collection of {@code agents}.
     * @param closure An object that specifies how to define the (unaligned) probability of the agent
     *                and how to implement the result of the aligned probability.
     * @param targetShare The target share of the relevant subpopulation (specified as a proportion of {@code agents}
     *                    filtered with {@code filter} population). Means of the aligned probabilities must be equal
     *                    to these values.
     */
    public void align(Collection<T> agents, Predicate<T> filter,
                      AlignmentMultiProbabilityClosure<T> closure, double[] targetShare) {
        align(agents, filter, closure, targetShare, 50, 1.e-15, true);
    }

    /**
     * Extracts weights from individual agents and stores them in a single array.
     * @param w An array storing weights.
     */
    abstract void extractWeights(double[] w);

    /**
     * Does a basic input data validation. Sees that probabilities are in range in [0,1], their sum is not greater than
     * 1; also checks that weights and precision are strictly positive.
     * @param targetShare Probabilities of outcomes of an event.
     * @param maxNumberIterations The number of iterations the method goes through.
     * @param precision The precision of the method.
     * @param weights Weights of agents.
     * @throws AssertionError When any of the checks fail.
     * @throws NullPointerException When {@code targetShare} or {@code weights} or both are null.
     * @throws IllegalArgumentException When sanity checks fail: {@code targetShare} values are out of range [0,1],
     *                                  {@code sum(targetShare) > 1}, {@code maxNumberIterations == 0},
     *                                  {@code precision <= 0}, NaN, or Inf, any element of {@code weights} is
     *                                  {@code <= 0}, NaN, or Inf.
     */
    final void validateInputData(final double @NonNull [] targetShare, final int maxNumberIterations,
                                 final double precision, final double @NonNull [] weights){
        for (var v : targetShare)
            if (v < 0. || v > 1.)
                throw new IllegalArgumentException("Each probability value must lie in [0,1].");
        if (sum(targetShare) > 1.)
            throw new IllegalArgumentException("Sum of all outcomes must be less than or equal to 1"); // TODO check this requirement
        if (maxNumberIterations == 0)
            throw new IllegalArgumentException("The method has to go at least through one iteration.");
        if (precision <= 0. || isNaN(precision) || isInfinite(precision))
            throw new IllegalArgumentException("The precision of the scheme has be greater than 0, but finite");

        for (var weight : weights)
            if (weight <= 0. || isNaN(weight) || isInfinite(weight))
                throw new IllegalArgumentException("Agent's weight cannot be <= 0, NaN, or Inf.");
    }

    /**
     * Throws an error message when the method doesn't converge to the expected solution of the problem within a given
     * range of iterations. This message is optional (conditional), however, additional sanity checks are introduced
     * by default. If at least one of the input parameters is NaN of Inf, something went wrong earlier as these values
     * make no numerical sense and should not be here in any case.
     * @param precisionValue The accuracy of the method.
     * @param warningsOn A boolean flag that switches on/off the warning message.
     * @param errorValue Actual error.
     * @param errorLevel Expected error.
     * @param sampleSize Number of agents.
     * @param iterations Total number of passed iterations at the moment of termination.
     * @param totalAgentWeight The sum of all weights in a given subpopulation.
     * @throws ArithmeticException When the main iterative condition is not satisfied; when double values are NaN, Inf.
     * @throws IllegalArgumentException When parameters are out of the acceptable range.
     */
    final void throwDivergenceError(final double precisionValue, final boolean warningsOn,
                                    final double errorValue, final double errorLevel, final double totalAgentWeight,
                                    final int sampleSize, final int iterations){
        val pv = isNaN(precisionValue) || isInfinite(precisionValue);
        val ev = isNaN(errorValue) || isInfinite(errorValue);
        val el = isNaN(errorLevel) || isInfinite(errorLevel);
        val aw = isNaN(totalAgentWeight) || isInfinite(totalAgentWeight);
        if (pv || ev || el || aw)
            throw new ArithmeticException("Sanity check fails.");
        if (precisionValue <= 0. || errorValue <= 0. || errorLevel <= 0. || totalAgentWeight <= 0. ||
                sampleSize < 1 || iterations < 0)
            throw new IllegalArgumentException("Values of parameters are out of range.");

        if(errorValue >= errorLevel && warningsOn) {
            String className = this.getClass().getCanonicalName();
            throw new ArithmeticException(format("""
                WARNING: The %s align() method terminated with an error of %f, which has a greater magnitude than the
                 precision bounds of +/-%f. The size of the filtered agent collection is %d and the number of iterations
                 was %d. Check the results of the %s alignment to ensure that alignment is good enough for the purpose
                 in question, or consider increasing the maximum number of iterations or the precision!
                """, className, errorValue/totalAgentWeight, precisionValue, sampleSize, iterations, className));
        }
    }

    /**
     * Calculates the absolute probability difference per choice, adds it up and normalizes. The resulting value is used
     * as a measure of the algorithm's convergence.
     * @param oldSum Sums of probabilities at the previous iteration.
     * @param newSum Current sums of probabilities.
     * @return The normalized probability error between two iterations.
     * @throws NullPointerException When gets {@code null} instead of an array.
     * @throws InputMismatchException When inputs are not of the same size.
     */
    final double recalculateProbabilityError(final double @NonNull [] oldSum, final double @NonNull [] newSum){
        if (oldSum.length != newSum.length)
            throw new InputMismatchException("Arrays don't have the same size.");

        val delta = new double[newSum.length];
        for(var choice = 0; choice < newSum.length; choice++)
            delta[choice] = Math.abs(sum(newSum[choice], -oldSum[choice]));
        return mean(delta);
    }

    /**
     * Scales down {@link #getProb()} to 're-normalise', as it was previously scaled up by weight. Replaces individual
     * probabilities with the aligned probabilities {@link #getProb()}.
     * @param closure An instance of implementation of
     *                {@link microsim.alignment.multiple.AlignmentMultiProbabilityClosure}.
     * @param fa A list of filtered agents.
     * @param w Weights of agents.
     * @param probabilities Probabilities of the subpopulation.
     * @throws NullPointerException When there is a {@code null} amongst provided (sub-)arrays.
     * @throws InputMismatchException If {@code probabilities} is not a rectangular matrix; when {@code weights} do not
     *                                have the same size as {@code fa}.
     */
    final void correctProbabilities(final AlignmentMultiProbabilityClosure<T> closure, final List<T> fa,
                                    final double @NonNull [] w, final double @NonNull [][] probabilities){
        for (var probability : probabilities)
            if (probability == null)
                throw new NullPointerException("A sub-array of probabilities is expected, but null was provided.");
        for (var i = 1; i < probabilities.length; i++)
            if (probabilities[0].length != probabilities[i].length)
                throw new InputMismatchException("Input data is of uneven shape.");
        if (probabilities.length != w.length || fa.size() != probabilities.length)
            throw new InputMismatchException("Input data size mismatch.");

        for (var i = 0; i < probabilities.length; i++)
            for(var choice = 0; choice < probabilities[0].length; choice++)
                probabilities[i][choice] /= w[i];

        for (var i = 0; i < probabilities.length; i++) {
            closure.align(fa.get(i), probabilities[i]);
        }
    }

    /**
     * Adjusts the probabilities via consecutive use of {@link #executeGammaTransform} and
     * {@link #executeAlphaTransform}.
     * @param oldProbSum An array to store the sums of probabilities coming from the previous iteration.
     * @param newProbSum Current sums of probabilities.
     * @param targetSum Target sum of each column to match to.
     * @param w Weights of agents.
     * @param agentSizeArray A temporary array to store probabilities of the same size as {@code agents}.
     * @param probabilities Overall probabilities of the subpopulation.
     * @throws NullPointerException When there is a {@code null} amongst provided (sub-)arrays.
     */
    final void probabilityAdjustmentCycle(final double @NonNull [] oldProbSum, final double @NonNull [] newProbSum,
                                          final double @NonNull [] targetSum, final double @NonNull [] w,
                                          final double @NonNull [] agentSizeArray,
                                          final double @NonNull [][] probabilities){
        // fixme add target/agent size check everywhere, at least two both
        // fixme there is no need to check for the interface,
        //  try name calling instead https://www.tutorialspoint.com/java/lang/class_getmethod.htm
        for (val probability : probabilities)
            if (probability == null)
                throw new NullPointerException("A sub-array of probabilities is expected, but null was provided.");

        val gammaValues = generateGammaValues(oldProbSum, newProbSum, targetSum, agentSizeArray, probabilities);

        val probSumOverChoices = new double[w.length];
        for(var agentId = 0; agentId < w.length; agentId++)
            probSumOverChoices[agentId] = executeGammaTransform(agentId, gammaValues, probabilities);

        for(var agentId = 0; agentId < w.length; agentId++) {
            executeAlphaTransform(agentId, w[agentId] / probSumOverChoices[agentId], newProbSum, probabilities);
        }
    }

    /**
     * Generates an array with gamma values; stores (and re-generates) probabilities generated at the previous iteration
     * for further comparison to ensure that the method converges.
     * @param oldProbSum An array to store the sums of probabilities at the previous iteration.
     * @param newProbSum An array with current sums of probabilities.
     * @param targetSum Target sum of each column to match to.
     * @param agentSizeArray A temporary array to store probabilities per choice for every agent.
     * @param probabilities An array of size {@code agents x choices} containing all probabilities.
     * @return An array of gamma values.
     * @throws NullPointerException When there is a {@code null} amongst provided (sub-)arrays.
     * @throws IllegalArgumentException When {@code targetFraction}, {@code agentSizeArray} do not pass validation.
     * @throws InputMismatchException When {@code probabilities} is of uneven shape; arrays to store sums of
     *                                probabilities are not of the same/correct size;
     * @implSpec {@code agentSizeArray} is introduced to avoid extra memory handling as this method is called during
     *           every iteration.
     */
    final double @NotNull [] generateGammaValues(final double @NonNull [] oldProbSum,
                                                 final double @NonNull [] newProbSum,
                                                 final double @NonNull [] targetSum,
                                                 final double @NonNull [] agentSizeArray,
                                                 final double @NonNull [][] probabilities){
        if (targetSum.length <= 1 || agentSizeArray.length <= 1)
            throw new IllegalArgumentException(format("The number of outcomes/agents is %d/%d, has to be " +
                                                             "at least 2/2;", targetSum.length,
                                                             agentSizeArray.length));
        if (oldProbSum.length != newProbSum.length || oldProbSum.length != targetSum.length)
            throw new InputMismatchException("Array size mismatch.");
        for (val probability : probabilities) {
            if (probability == null)
                throw new NullPointerException("A sub-array of probabilities is expected, but null was provided.");
            if (agentSizeArray.length != probability.length)
                throw new InputMismatchException("Inconsistent array shape.");
        }

        val gamma = new double[targetSum.length];

        arraycopy(newProbSum, 0, oldProbSum, 0, targetSum.length);

        for(var choice = 0; choice < targetSum.length; choice++) {
            for (var agentId = 0; agentId < agentSizeArray.length; agentId++)
                agentSizeArray[agentId] = probabilities[agentId][choice];
            gamma[choice] = targetSum[choice] / sum(agentSizeArray);
        }
        return gamma;
    }

    /**
     * Gamma transform of the probabilities. It scales all columns by corresponding elements of {@code gammaValues} in
     * such a way that the total sum of every column matches the target value.
     * @param gammaValues An array with values of the {@code gamma} scaling parameter.
     * @param agentId The agent's index denoting its position in the array.
     * @param probabilities A 2d-array {@code agents x choices} of probabilities to be transformed.
     * @return The sum of probabilities over all choices per agent.
     * @throws NullPointerException When one of the (sub-)arrays provided is {@code null}.
     * @throws ArrayIndexOutOfBoundsException When {@code agentId} is negative as it serves as an array index.
     * @throws InputMismatchException When dimensions of input arrays mismatch.
     */
    final double executeGammaTransform(final int agentId, final double @NonNull [] gammaValues,
                                       final double @NonNull [][] probabilities){
        if (agentId < 0)
            throw new ArrayIndexOutOfBoundsException("Array index can't be negative.");
        if (probabilities[agentId] == null)
            throw new NullPointerException("A sub-array of probabilities is expected, but null was provided.");
        if (probabilities[agentId].length != gammaValues.length)
            throw new InputMismatchException("Length has to correspond to the total choice number.");

        for (var choice = 0; choice < gammaValues.length; choice++)
            probabilities[agentId][choice] *= gammaValues[choice];
        return sum(probabilities[agentId]);
    }

    /**
     * Alpha transform of the probabilities. Each row gets multiplied by the corresponding coefficient {@code alpha} so
     * that the sum of all elements in the row adds up exactly to 1.
     * @param alphaValue Value of the {@code alpha} scaling parameter.
     * @param agentId The agent's index denoting its position in the array.
     * @param probabilities An array of probabilities of size {@code agents x choices}.
     * @param probabilitiesSum An array of sums of probabilities, every value corresponds to a given choice i.e., it's
     *                         a sum over agents.
     * @throws IllegalArgumentException When the value of {@code alpha} is zero, negative, NaN, or (+/-)Infinity.
     * @throws ArrayIndexOutOfBoundsException When {@code agentId} is negative as it serves as an array index.
     * @throws InputMismatchException When dimensions of input arrays mismatch.
     * @throws NullPointerException When one of the (sub-)arrays provided is {@code null}.
     */
    final void executeAlphaTransform(final int agentId, final double alphaValue,
                                     final double @NonNull [] probabilitiesSum,
                                     final double @NonNull [][] probabilities){
        if (isInfinite(alphaValue) || isNaN(alphaValue) || alphaValue <= 0.)
            throw new IllegalArgumentException("Alpha value is out of acceptable range");
        if (agentId < 0)
            throw new ArrayIndexOutOfBoundsException("Array index can't be negative.");
        if (probabilities[agentId] == null)
            throw new NullPointerException("A sub-array of probabilities is expected, but null was provided.");
        if (probabilities[agentId].length != probabilitiesSum.length)
            throw new InputMismatchException("Length has to correspond to the total choice number.");

        for (var choice = 0; choice < probabilitiesSum.length; choice++) probabilities[agentId][choice] *= alphaValue;
        for (var choice = 0; choice < probabilitiesSum.length; choice++)
            probabilitiesSum[choice] = sum(probabilitiesSum[choice], probabilities[agentId][choice]);
    }
}
