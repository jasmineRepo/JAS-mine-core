package microsim.statistics.regression;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import lombok.NonNull;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.DoubleSource;

import java.util.*;

public class MultiProbitRegression<T extends Enum<T>> implements MultipleChoiceRegression<T> {

    private final Random random;

    private final Normal normalRV;

    private final Map<T, MultiKeyCoefficientMap> maps;

    public MultiProbitRegression(final @NonNull Map<T, MultiKeyCoefficientMap> maps) {
        this(maps, SimulationEngine.getRnd());
    }

    public MultiProbitRegression(final @NonNull Map<T, MultiKeyCoefficientMap> maps, final @NonNull Random random) {
        this.random = random;
        this.maps = maps;
        int count = 0;
        Set<String> covariateNames = new HashSet<>();
        for (T event : maps.keySet()) {
            var covariateSet = (maps.get(event)).keySet();
            for (Object covariate : covariateSet) {
                if (count == 0) {
                    covariateNames.add(covariate.toString());
                } else {
                    if (!covariateNames.contains(covariate.toString()) ||
                            covariateNames.size() != covariateSet.size()) {
                        throw new RuntimeException("The covariates specified for each outcome of type T in " +
                                "the MultiProbitRegression object do not match!");
                    }
                }
            }
            count++;
        }
        normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
    }

    /**
     * Warning - only use when MultiProbitRegression's maps field has values that are MultiKeyCoefficientMaps with
     * only one key.  This method only looks at the first key of the MultiKeyCoefficientMap field of LinearRegression,
     * so any other keys that are used to distinguish a unique multiKey (i.e. if the first key occurs more than once)
     * will be ignored! If the first key of the multiKey appears more than once, the method would return an incorrect
     * value, so will throw an exception.
     *
     * @param values
     * @return
     */
    public double getProbitTransformOfScore(final @NonNull T event, final @NonNull Map<String, Double> values) {
        final double score = LinearRegression.computeScore(maps.get(event), values);
        return normalRV.cdf(score);
    }

    public double getProbitTransformOfScore(final @NonNull T event, final @NonNull Object individual) {
        final double score = LinearRegression.computeScore(maps.get(event), individual);
        return normalRV.cdf(score);
    }

    public T eventType(final @NonNull Object individual) {
        Map<T, Double> probs = new HashMap<>();

        double denominator = 0.;

        for (T event : maps.keySet()) {
            double probitTransformOfScore = getProbitTransformOfScore(event, individual);
            probs.put(event, probitTransformOfScore);
            denominator += probitTransformOfScore;
        }

        //Check whether there is a base case that has not been included in the regression specification variable (maps).
        T k = null;
        T[] eventProbs = (T[]) k.getClass().getEnumConstants();
        int countNullEventProbs = 0;
        for (T eventProb : eventProbs) {
            if (probs.get(eventProb) == null) {
                //The multiprobit regression can go without specifying coefficients for 1 of the outcomes as the
                // probability of this event can be determined by the residual of the other probabilities.
                countNullEventProbs++;
                //Check no more than one event has null prob, so that it is valid to take the residual to find the probability
                if (countNullEventProbs > 1) {
                    throw new RuntimeException("MultiProbitRegression has been constructed with a map that does not" +
                            " contain enough of the possible values of the type T.  The map should contain the full number " +
                            "of T values, or one less than the full number of T values (in which case, the missing value" +
                            " is considered the 'default' case whose regression betas are all zero).");
                } else {
                    denominator += 0.5;
                    //We include the base case, where score = 0 (as betas are set to zero).  The normalRV.cdf(0) = 0.5
                    // (as the standard normal distribution is symmetric).  The other cases have already been
                    // incremented into the denominator.
                    probs.put(eventProb, 0.5 / denominator);
                    //The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying
                    // the probit transform (the cumulative standard normal distribution) to the score of 0, and the
                    // denominator is the sum of probit transforms for all events.
                }
            }
        }

        //Normalise the probabilities of the events specified in the regression maps
        for (T event : maps.keySet()) {
            //Only iterate through the cases specified in the regression maps - the base case has already been normalised.
            double probitTransformOfScoreForEvent = probs.get(event);
            probs.put(event, probitTransformOfScoreForEvent / denominator);
            //Normalise the probit transform of score (the application of the standard normal cumulative distribution
            // to the score) of the event by the sum for all events
        }

        double[] probArray = new double[probs.size()];
        for (int i = 0; i < eventProbs.length; i++) {
            probArray[i] = probs.get(eventProbs[i]);
        }

        return RegressionUtils.event(eventProbs, probArray, random);
    }

    /**
     * Warning - only use when MultiProbitRegression's maps field has values that are MultiKeyCoefficientMaps with only
     * one key.  This method only looks at the first key of the MultiKeyCoefficientMap field of LinearRegression, so
     * any other keys that are used to distinguish a unique multiKey (i.e. if the first key occurs more than once) will
     * be ignored! If the first key of the multiKey appears more than once, the method would return an incorrect value,
     * so will throw an exception.
     *
     * @param values
     * @return
     */
    public T eventType(final @NonNull Map<String, Double> values) {
        Map<T, Double> probs = new HashMap<>();

        double denominator = 0.0;

        for (T event : maps.keySet()) {
            double probitTransformOfScore = getProbitTransformOfScore(event, values);
            probs.put(event, probitTransformOfScore);
            denominator += probitTransformOfScore;
        }

        //Check whether there is a base case that has not been included in the regression specification variable (maps).
        T k = null;
        T[] eventProbs = (T[]) k.getClass().getEnumConstants();
        int countNullEventProbs = 0;
        for (T eventProb : eventProbs) {
            if (probs.get(eventProb) == null) {
                //The multiprobit regression can go without specifying coefficients for 1 of the outcomes as
                // the probability of this event can be determined by the residual of the other probabilities.
                countNullEventProbs++;
                //Check no more than one event has null prob, so that it is valid to take the residual to find the probability
                if (countNullEventProbs > 1) {
                    throw new RuntimeException("MultiProbitRegression has been constructed with a map that does not" +
                            " contain enough of the possible values of the type T.  The map should contain the full number" +
                            " of T values, or one less than the full number of T values (in which case, the missing value" +
                            " is considered the 'default' case whose regression betas are all zero).");
                } else {
                    denominator += 0.5;
                    //We include the base case, where score = 0 (as betas are set to zero).  The normalRV.cdf(0) = 0.5
                    // (as the standard normal distribution is symmetric).  The other cases have already been
                    // incremented into the denominator.
                    probs.put(eventProb, 0.5 / denominator);
                    //The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying
                    // the probit transform (the cumulative standard normal distribution) to the score of 0, and the
                    // denominator is the sum of probit transforms for all events.
                }
            }
        }

        //Normalise the probabilities of the events specified in the regression maps
        for (T event : maps.keySet()) {
            //Only iterate through the cases specified in the regression maps - the base case has already been normalised.
            double probitTransformOfScoreForEvent = probs.get(event);
            probs.put(event, probitTransformOfScoreForEvent / denominator);
            //Normalise the probit transform of score (the application of the standard normal cumulative distribution
            // to the score) of the event by the sum for all events
        }

        double[] probArray = new double[probs.size()];
        for (int i = 0; i < eventProbs.length; i++) {
            probArray[i] = probs.get(eventProbs[i]);
        }

        return RegressionUtils.event(eventProbs, probArray, random);
    }

    public <E extends Enum<E>> double getProbitTransformOfScore(final @NonNull T event, DoubleSource iDblSrc,
                                                                final @NonNull Class<E> Regressors) {
        MultiKeyCoefficientMap map = maps.get(event);
        double score;
        if (Objects.requireNonNull(map.getKeysNames()).length == 1) {
            score = LinearRegression.computeScore(map, iDblSrc, Regressors, true);
            //No additional conditioning regression keys used, so no need to check for them
        } else {
            score = LinearRegression.computeScore(map, iDblSrc, Regressors);
            //Additional conditioning regression keys used (map has more than one key in the multiKey, so need to use
            // reflection (perhaps slow) in order to extract the underlying agents' properties e.g. gender or civil
            // status, in order to determine the relevant regression co-efficients.  If time is critical, consider
            // making the underlying agent (the IDoubleSource) also implement the ObjectSource interface, which uses a
            // faster method to retrieve information about the agent instead of reflection.
        }

        return normalRV.cdf(score);
    }


    public <E extends Enum<E>> T eventType(DoubleSource iDblSrc, Class<E> Regressors, Class<T> enumType) {
        Map<T, Double> probs = new HashMap<>();

        double denominator = 0.;

        for (T event : maps.keySet()) {
            double probitTransformOfScore = getProbitTransformOfScore(event, iDblSrc, Regressors);
            probs.put(event, probitTransformOfScore);
            denominator += probitTransformOfScore;
        }

        //Check whether there is a base case that has not been included in the regression specification variable (maps).
        T[] eventProbs = enumType.getEnumConstants();

        int countNullEventProbs = 0;
        for (T eventProb : eventProbs) {
            if (probs.get(eventProb) == null) {
                //The multiprobit regression can go without specifying coefficients for 1 of the outcomes as the
                // probability of this event can be determined by the residual of the other probabilities.
                countNullEventProbs++;
                //Check no more than one event has null prob, so that it is valid to take the residual to find the probability
                if (countNullEventProbs > 1) {
                    throw new RuntimeException("MultiProbitRegression has been constructed with a map that does not " +
                            "contain enough of the possible values of the type T.  The map should contain the full number" +
                            " of T values, or one less than the full number of T values (in which case, the missing value" +
                            " is considered the 'default' case whose regression betas are all zero).");
                } else {
                    denominator += 0.5;
                    //We include the base case, where score = 0 (as betas are set to zero).  The normalRV.cdf(0) = 0.5
                    // (as the standard normal distribution is symmetric).  The other cases have already been incremented
                    // into the denominator.
                    probs.put(eventProb, 0.5 / denominator);
                    //The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying the
                    // probit transform (the cumulative standard normal distribution) to the score of 0, and the
                    // denominator is the sum of probit transforms for all events.
                }
            }
        }

        //Normalise the probabilities of the events specified in the regression maps
        for (T event : maps.keySet()) {
            //Only iterate through the cases specified in the regression maps - the base case has already been normalised.
            double probitTransformOfScoreForEvent = probs.get(event);
            probs.put(event, probitTransformOfScoreForEvent / denominator);
            //Normalise the probit transform of score (the application of the standard normal cumulative distribution
            // to the score) of the event by the sum for all events
        }

        double[] probArray = new double[probs.size()];
        for (int i = 0; i < eventProbs.length; i++) {
            probArray[i] = probs.get(eventProbs[i]);
        }
        return RegressionUtils.event(eventProbs, probArray, random);
    }
}
