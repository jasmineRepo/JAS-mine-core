package microsim.alignment.outcome;

import lombok.NonNull;
import lombok.val;
import microsim.agent.Weight;
import microsim.alignment.AlignmentUtils;
import microsim.engine.SimulationEngine;
import org.apache.commons.collections4.Predicate;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static jamjam.Sum.sum;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static microsim.statistics.regression.RegressionUtils.event;

/**
 * Align the population by resampling of data with (or without) corresponding weights. This involves picking an agent
 * from the relevant collection at random with a probability that depends on an associated weight in the case of
 * weighted agents or uniformly otherwise. The chosen agent then undergoes resampling of its relevant attribute (as
 * specified by the {@link AlignmentOutcomeClosure}). This process is continued until either the alignment target is
 * reached, or the maximum number of attempts to resample has been reached.
 *
 * @implSpec This method is for events with binary outcomes {@code 0, 1} only.
 * @see <a href="https://ideas.repec.org/a/ijm/journl/v7y2014i2p3-39.html">Matteo Richiardi & Ambra Poggi, 2014.
 * "Imputing Individual Effects in Dynamic Microsimulation Models. An application to household formation and labour
 * market participation in Italy," International Journal of Microsimulation, International Microsimulation Association,
 * vol. 7(2), pages 3-39.</a>
 * @see <a href="https://link.springer.com/article/10.1007/s10614-005-9016-0">Leombruni, R., Richiardi, M. LABORsim: An
 * Agent-Based Microsimulation of Labour Supply – An Application to Italy. Comput Econ 27, 63–88 (2006). </a>
 */
public class ResamplingAlignment<T> implements AlignmentUtils<T> {
    final static int avgResampleAttempts = 20;
    double[] weights = null;
    double effectiveSampleSize = 0.;

    /**
     * {@code maxResamplingAttempts} defaults to {@code -1}.
     *
     * @see #align(Collection, Predicate, AlignmentOutcomeClosure, double, int)
     */
    public void align(final @NonNull Collection<T> agents, final @Nullable Predicate<T> filter,
                      final @NonNull AlignmentOutcomeClosure<T> closure, final double targetShare) {
        align(agents, filter, closure, targetShare, -1);
    }

    /**
     * {@code maxResamplingAttempts} defaults to {@code -1}.
     *
     * @see #align(Collection, Predicate, AlignmentOutcomeClosure, double, int)
     */
    public void align(final @NonNull Collection<T> agents, final @Nullable Predicate<T> filter,
                      final @NonNull AlignmentOutcomeClosure<T> closure, final int targetNumber) {
        align(agents, filter, closure, targetNumber, -1);
    }

    /**
     * Align share of population by resampling. This involves picking an agent from the relevant collection of agents at
     * random with a probability that depends on an associated weight or uniformly if no weights exists. The chosen
     * agent then undergoes resampling of its relevant attribute (as specified by the {@link AlignmentOutcomeClosure}).
     * This process is continued until either the alignment target is reached, or the maximum number of attempts to
     * resample has been reached, as specified by the {@code maxResamplingAttempts} parameter, or - if the input data is
     * weighted - the pool of agents has been exhausted.
     *
     * @param agents                A list of agents to potentially be resampled; the agent class may implement the
     *                              {@link Weight} interface if needed by providing a {@link Weight#getWeight()} method.
     *                              In the case of the alignment algorithm, {@link Weight#getWeight()} must return a
     *                              non-negative value.
     * @param filter                Filters the {@code agents} parameter so that only the relevant sub-population of
     *                              agents is sampled.
     * @param closure               {@link AlignmentOutcomeClosure} that specifies how to define the outcome of the
     *                              agent and how to resample it.
     * @param targetShare           The target share of the relevant sub-population (specified as a proportion of the
     *                              filtered population) for which the outcome (defined by the
     *                              {@link AlignmentOutcomeClosure}) must be true.
     * @param maxResamplingAttempts The maximum number of attempts to resample before terminating the alignment.
     *                              Introduced for situations when the resampling (as defined by the
     *                              {@link AlignmentOutcomeClosure}) is unable to alter the outcomes of enough agents,
     *                              due to the nature of the sub-population and the definition of the outcome (i.e. if
     *                              agents' attributes are so far away from a binary outcome threshold boundary, that
     *                              the probability of enough of them switching to the desired outcome is vanishingly
     *                              small). If lower than the size of the subset of the population whose outcomes need
     *                              changing (or negative) the number is automatically enlarged to the default value of
     *                              20 times the size of the subset of the population to be resampled in order to move
     *                              the delta between the simulation and the target towards 0. Therefore, in order to
     *                              improve this delta, a member of the population undergoing alignment will be
     *                              resampled up to a maximum of 20 times on average in order to change their outcome,
     *                              before the alignment algorithm will give up and terminate.
     * @throws IllegalArgumentException when {@code targetShare} is out of {@code [0,1]} range.
     * @implSpec {@code targetNumber} is rounded down for the sake of consistency.
     */
    public void align(final @NonNull Collection<T> agents, final @Nullable Predicate<T> filter,
                      final @NonNull AlignmentOutcomeClosure<T> closure, final double targetShare,
                      int maxResamplingAttempts) {
        if (targetShare < 0. || targetShare > 1.)
            throw new IllegalArgumentException("targetShare is out of [0, 1] range, this is impossible.");

        var list = alignmentSetup(agents, filter);
        if (list == null) return;

        effectiveSampleSize = weights == null ? list.size() : sum(weights);

        doAlignment(list, closure, (int) (targetShare * effectiveSampleSize), maxResamplingAttempts);
    }

    /**
     * Align absolute number of population by weighted resampling. This involves picking an agent from the relevant
     * collection of agents at random with a probability that depends on an associated weight. The chosen agent then
     * undergoes resampling of its relevant attribute (as specified by the {@link AlignmentOutcomeClosure}). This
     * process is continued until either the alignment target is reached, or the maximum number of attempts to resample
     * has been reached, as specified by the {@code maxResamplingAttempts} parameter.
     *
     * @param agents                A list of agents to potentially be resampled; the agent class must implement the
     *                              {@link Weight} interface by providing a getWeight() method. In the case of the
     *                              alignment algorithm, getWeight() must return a positive value.
     * @param filter                Filters the {@code agents} so that only the relevant subpopulation of agents is
     *                              sampled.
     * @param closure               {@link AlignmentOutcomeClosure} that specifies how to define the outcome of the
     *                              agent and how to resample it.
     * @param targetNumber          The target number of the filtered population for which the outcome (defined by the
     *                              {@link AlignmentOutcomeClosure}) must be true.
     * @param maxResamplingAttempts The maximum number of attempts to resample before terminating the alignment.
     *                              Introduced for situations when the resampling (as defined by the
     *                              {@link AlignmentOutcomeClosure}) is unable to alter the outcomes of enough agents,
     *                              due to the nature of the subpopulation and the definition of the outcome (i.e. if
     *                              agents' attributes are so far away from a binary outcome threshold boundary, that
     *                              the probability of enough of them switching to the desired outcome is vanishingly
     *                              small). If lower than the size of the subset of the population whose outcomes need
     *                              changing (or negative) the number is automatically enlarged to the default value of
     *                              20 times the size of the subset of the population to be resampled in order to move
     *                              the delta between the simulation and the target towards 0. Therefore, in order to
     *                              improve this delta, a member of the population undergoing alignment will be
     *                              resampled up to a maximum of 20 times on average in order to change their outcome,
     *                              before the alignment algorithm will give up and terminate.
     * @throws IllegalArgumentException when targetNumber is negative or exceeds the population size.
     */
    public void align(final @NonNull Collection<T> agents, final @Nullable Predicate<T> filter,
                      final @NonNull AlignmentOutcomeClosure<T> closure, final int targetNumber,
                      int maxResamplingAttempts) {
        if (targetNumber < 0) throw new IllegalArgumentException("targetNumber is negative, this is impossible.");

        var list = alignmentSetup(agents, filter);
        if (list == null) return;

        effectiveSampleSize = weights == null ? list.size() : sum(weights);

        if (targetNumber > effectiveSampleSize)
            throw new IllegalArgumentException("Resampling Alignment target is larger than the sample size" +
                " (over 100%)! This is impossible to reach.");
        doAlignment(list, closure, targetNumber, maxResamplingAttempts);
    }

    /**
     * The main alignment method. Calculates the difference between the target value and the actual sum of all positive
     * (weighted) outcomes. Depending on the sign of this delta, resamples the agent (tries to change the outcome
     * value).
     *
     * @param list                  A list of agents.
     * @param closure               A closure object to update the state of an agent.
     * @param targetNumber          The target sum of positive outcomes.
     * @param maxResamplingAttempts The total number of resampling attempts, if reached, the method picks randomly
     *                              another agent.
     * @implSpec The list argument here is already filtered for the relevant agents in a corresponding {@code align}
     * method.
     * @implSpec Consistent delta comparison for all cases: compare to zero every time.
     * @implSpec Consistent values of initial delta for given target numbers and equivalent target shares. Say, when
     * there are 12 positive outcomes and the target number is 9, the equivalent target share must be 0.75 and
     * - provided the rng seed stays the same - results must be identical.
     * @implSpec Resampling number adjustment is different for different signs of delta.
     */
    public void doAlignment(@NonNull List<T> list, @NonNull AlignmentOutcomeClosure<T> closure,
                            final double targetNumber, int maxResamplingAttempts) {
        Collections.shuffle(list, SimulationEngine.getRnd());
        int count;
        double deltaSimulationTarget;
        double sumPositiveOutcomes;

        if (weights != null) {
            var trueAgentMap = new HashMap<T, Double>();
            var falseAgentMap = new HashMap<T, Double>();

            for (T agent : list) {
                var weight = ((Weight) agent).getWeight();
                if (closure.getOutcome(agent)) trueAgentMap.put(agent, weight);
                else falseAgentMap.put(agent, weight);
            }

            var weightsPositive = trueAgentMap.values().stream().mapToDouble(i -> i).toArray();

            sumPositiveOutcomes = sum(weightsPositive);

            deltaSimulationTarget = sumPositiveOutcomes - targetNumber;

            ArrayList<Object> out;

            if (deltaSimulationTarget > 0.) out = adjustDelta(trueAgentMap, closure, maxResamplingAttempts,
                deltaSimulationTarget);
            else if (deltaSimulationTarget < 0.) out = adjustDelta(falseAgentMap, closure, maxResamplingAttempts,
                deltaSimulationTarget);
            else return;

            count = (Integer) out.get(0);
            maxResamplingAttempts = (Integer) out.get(1);
            deltaSimulationTarget = (Double) out.get(2);
        } else {
            count = 0;

            sumPositiveOutcomes = list.stream().mapToDouble(agent -> (closure.getOutcome(agent) ? 1 : 0)).sum();

            deltaSimulationTarget = sumPositiveOutcomes - targetNumber;

            if (deltaSimulationTarget > 0) {
                maxResamplingAttempts = adjustMaxResamplingAttempts((int) sumPositiveOutcomes, maxResamplingAttempts);

                while (deltaSimulationTarget > 0 &&
                    count < maxResamplingAttempts) {
                    T nextAgent = event((AbstractList<T>) list, SimulationEngine.getRnd());
                    if (closure.getOutcome(nextAgent)) {
                        count++;
                        closure.resample(nextAgent);
                        if (!closure.getOutcome(nextAgent)) {
                            deltaSimulationTarget--;
                            count = 0;
                        }
                    }
                }
            } else if (deltaSimulationTarget < 0) {
                maxResamplingAttempts = adjustMaxResamplingAttempts((int) sumPositiveOutcomes, maxResamplingAttempts);
                while (deltaSimulationTarget < 0 &&
                    count < maxResamplingAttempts) {
                    T nextAgent = event((AbstractList<T>) list, SimulationEngine.getRnd());
                    if (!closure.getOutcome(nextAgent)) {
                        count++;
                        closure.resample(nextAgent);
                        if (closure.getOutcome(nextAgent)) {
                            deltaSimulationTarget++;
                            count = 0;
                        }
                    }
                }
            } else return;
        }

        if (count >= maxResamplingAttempts)
            System.out.printf("Count, %d, maxResamplingAttempts, %d, Resampling Alignment Algorithm has reached" +
                    " the maximum number of resample attempts (on average, %d attempts per object to be" +
                    " aligned) and has terminated. Alignment may have failed. The difference between the" +
                    " population in the system with the desired outcome and the target number is %s (%s" +
                    " percent). If this is too large, check the resampling method and the subset of" +
                    " population to understand why not enough of the population are able to change their" +
                    " outcomes.%n", count, maxResamplingAttempts, avgResampleAttempts,
                deltaSimulationTarget, deltaSimulationTarget * 100. / targetNumber);
    }

    /**
     * if there is an agent whose weight was too large for the initial resampling procedure, but still was sufficiently
     * small, decrements the magnitude of delta so that it gets even closer to {@code 0}. This only happens if
     * addition/subtraction of the corresponding weight makes its absolute magnitude even smaller.
     *
     * @param agent      A weighted agent if any.
     * @param closure    A closure to operate the agent.
     * @param deltaValue The value of delta.
     * @return updated delta.
     */
    private double finalAgentAdjustment(final @Nullable T agent, final @NonNull AlignmentOutcomeClosure<T> closure,
                                        double deltaValue) {
        if (agent != null) {
            var s = -signum(deltaValue);
            if (abs(deltaValue + s * ((Weight) agent).getWeight()) < abs(deltaValue)) {
                int totalResampleAttempts = 0;
                while (totalResampleAttempts < avgResampleAttempts) {
                    totalResampleAttempts++;
                    closure.resample(agent);
                    if (closure.getOutcome(agent)) {
                        deltaValue += s * ((Weight) agent).getWeight();
                        break;
                    }
                }
            }
        }
        return deltaValue;
    }

    /**
     * This method catches the case where {@code maxResamplingAttempts} is not included in the arguments, i.e., defaults
     * to {@code -1}. Also, it provides a lower bound for the user to specify, which is the size of the subset of the
     * population whose outcomes need changing. Anything less, and the number is automatically enlarged. This creates a
     * default value of 20 times the size of the subset of the population to be resampled in order to move the delta
     * towards 0 by 1 (or by the agent's weight value). Therefore, in order to improve delta by 1, a member of the
     * population undergoing alignment will be resampled up to a maximum of 20 times on average in order to change their
     * outcome, before the alignment algorithm will give up and move to the next agent.
     *
     * @param size         The sample size.
     * @param initialValue THe initial number of resampling attempts.
     * @return the max number of resampling attempts.
     */
    private int adjustMaxResamplingAttempts(final int size, final int initialValue) {
        return initialValue < size ? avgResampleAttempts * size : initialValue;
    }

    /**
     * This method iteratively adjusts the value of {@code delta} to make it as close to {@code 0} as possible. It is
     * also map-agnostic, i.e., works with both positive and negative outcomes. When the number of positive ones is too
     * low {@code delta < 0} and vice versa. The cases of {@code delta == 0} are not considered here.
     *
     * @param map     A map of event outcomes with corresponding weights.
     * @param closure A closure object to get access to attributes of agents.
     * @param max     The maximum number of resampling attempts.
     * @param delta   The delta value
     * @return an ArrayList with final values of {@code count}, {@code max}, and {@code delta}.
     * @implSpec If the agent's weight is too high resulting in delta changing its sign (can't be resampled normally)
     * the agent itself is stored elsewhere and also removed from the resampling pool to avoid empty cycles. During
     * every iteration - if there is another agent fitting the description, but with smaller weight (that is still just
     * too big to be used) - it replaces the previous one. Finally, only after the whole resampling pool has been
     * exhausted, an attempt is made to run the final adjustment using
     * {@link #finalAgentAdjustment(Object, AlignmentOutcomeClosure, double)}.
     */
    private @NonNull ArrayList<Object> adjustDelta(final @NonNull HashMap<T, Double> map,
                                                   final @NonNull AlignmentOutcomeClosure<T> closure, int max,
                                                   double delta) {
        int count = 0;
        T agent = null;
        var out = new ArrayList<>();

        val initialSignum = signum(delta);
        var runningSignum = signum(delta);

        val s = initialSignum == signum(1.);

        max = adjustMaxResamplingAttempts(map.size(), max);
        while (runningSignum == initialSignum &&
            count < max &&
            !map.isEmpty()) {
            count++;
            var nextAgent = event(map, false);
            var weight = ((Weight) nextAgent).getWeight();
            if (runningSignum * delta >= weight) {
                closure.resample(nextAgent);
                if (s ^ closure.getOutcome(nextAgent)) {
                    delta = sum(delta, -runningSignum * weight);
                    count = 0;
                    map.remove(nextAgent);
                }
            } else {
                if (agent == null || ((Weight) agent).getWeight() > ((Weight) nextAgent).getWeight()) agent = nextAgent;
                map.remove(nextAgent);
            }
            runningSignum = signum(delta);
        }

        out.add(count);
        out.add(max);
        out.add(finalAgentAdjustment(agent, closure, delta));
        return out;
    }

    /**
     * Processes the input data by filtering it and extracting/validating the weights if any. The method checks weights
     * for non-positive values, NaNs and Infinities.
     *
     * @param agents A collection of agents.
     * @param filter A predicate object to filter the agents.
     * @return a list of filtered agents
     * @throws IllegalArgumentException when a weight is non-positive, Nan, or Infinity.
     */
    private @Nullable List<T> alignmentSetup(final @NonNull Collection<T> agents, final @Nullable Predicate<T> filter) {
        var list = extractAgentList(agents, filter);
        if (list.isEmpty()) return null;

        if (list.get(0) instanceof Weight) {
            weights = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                var w = ((Weight) list.get(i)).getWeight();
                if (w <= 0.) throw new IllegalArgumentException("Weight cannot be zero or negative.");
                if (isNaN(w)) throw new IllegalArgumentException("Weight is not a number.");
                if (isInfinite(w)) throw new IllegalArgumentException("Weight is infinite.");
                weights[i] = w;
            }
        }
        return list;
    }
}
