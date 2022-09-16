package microsim.statistics.regression;

import lombok.NonNull;
import lombok.val;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static jamjam.Sum.sum;

public class RegressionUtils {
    private static final double SYMMETRIC_MATRIX_EPS = 1.e-15;

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}, {@code events} are passed as
     * {@code eventClass.getEnumConstants()}.
     *
     * @see #event(Object[], double[], Random)
     */
    public static <T> @NonNull T event(final @NonNull Class<T> eventClass, final double @NonNull [] prob) {
        return event(eventClass.getEnumConstants(), prob, SimulationEngine.getRnd());
    }

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}, {@code events} are passed as
     * {@code eventClass.getEnumConstants()}.
     *
     * @see #event(Object[], double[], Random, boolean)
     */
    public static <T> @NonNull T event(final @NonNull Class<T> eventClass, final double @NonNull [] weight,
                                       final boolean checkWeightSum) {
        return event(eventClass.getEnumConstants(), weight, SimulationEngine.getRnd(), checkWeightSum);
    }

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}.
     *
     * @see #event(Object[], double[], Random)
     */
    public static <T> @NonNull T event(final T @NonNull [] events, final double @NonNull [] prob) {
        return event(events, prob, SimulationEngine.getRnd());
    }

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}.
     *
     * @see #event(Object[], double[], Random, boolean)
     */
    public static <T> @NonNull T event(final T @NonNull [] events, final double @NonNull [] weight,
                                       final boolean checkWeightSum) {
        return event(events, weight, SimulationEngine.getRnd(), checkWeightSum);
    }

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}.
     *
     * @see #event(Map, Random)
     */
    public static <T> @NonNull T event(final @NonNull Map<T, Double> map) {
        return event(map, SimulationEngine.getRnd());
    }

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}.
     *
     * @see #event(Map, Random, boolean)
     */
    public static <T> @NonNull T event(final @NonNull Map<T, Double> map, final boolean checkWeightSum) {
        return event(map, SimulationEngine.getRnd(), checkWeightSum); // todo check if this rng is not null always
    }

    /**
     * {@code checkWeightSum} defaults to {@code true}.
     *
     * @see #event(Object[], double[], Random, boolean)
     */
    public static <T> @NonNull T event(final T @NonNull [] events, final double @NonNull [] prob,
                                       final @NonNull Random rnd) {
        return event(events, prob, rnd, true);
    }

    /**
     * Returns an event determined randomly from a set of events and weights. Note that these weights do not necessarily
     * have to sum to 1, as the method can convert the weights into probabilities by calculating their relative
     * proportions. If the user desires that an exception is thrown if the weights do not sum to 1, please set
     * checkProbSum to true.
     *
     * @param events         An array of events of type {@code T}.
     * @param weights        An array of doubles from which the probabilities are calculated (by dividing the weights
     *                       by the sum of weights).
     * @param rnd            A random number generator.
     * @param checkWeightSum A boolean toggle, which if true means that the method will throw an exception if the
     *                       weights do not add to 1. If false, the method will calculate the probabilities associated
     *                       to the weights and use these in the random sampling of the event.
     * @return a randomly chosen event.
     */
    public static <T> @NonNull T event(final T @NonNull [] events, final double @NonNull [] weights,
                                       final @NonNull Random rnd, final boolean checkWeightSum) {
        validateWeights(weights, checkWeightSum);
        return events[(Integer) tosser(0, rnd, weights).get(0) - 1];
    }

    /**
     * {@code rnd} defaults to {@code SimulationEngine.getRnd()}.
     *
     * @see #event(double, Random)
     */
    public static boolean event(final double prob) {
        return event(prob, SimulationEngine.getRnd());
    }

    /**
     * Checks if a randomly generated value belongs to the semi-open {@code [0, prob)} interval.
     *
     * @param prob A probability value that must be in [0, 1] range.
     * @param rnd  A random number generator.
     * @return a boolean that shows if a random boolean is in the [0, prob) interval.
     */
    public static boolean event(final double prob, final @NonNull Random rnd) {
        if (prob < 0. || prob > 1.) throw new IllegalArgumentException("prob outside the valid interval [0,1]!");
        else return rnd.nextDouble() < prob;
    }

    /**
     * {@code checkWeightSum} defaults to {true}.
     *
     * @see #event(Map, Random, boolean)
     */
    public static <T> @NonNull T event(final @NonNull Map<T, Double> map, final @NonNull Random rnd) {
        return event(map, rnd, true);
    }

    /**
     * Transforms a map into two arrays to boost code performance and passes them further.
     *
     * @see #event(Object[], double[], Random, boolean)
     */
    public static <T> @NonNull T event(final @NonNull Map<T, Double> map, final @NonNull Random rnd,
                                       final boolean checkWeightSum) {
        @SuppressWarnings("unchecked")
        //Conversion from set of T to array of T, so conversion should not need checking
        T[] events = (T[]) map.keySet().toArray();

        double[] prob = Arrays.stream(events).mapToDouble(event -> ((Number) map.get(event)).doubleValue()).toArray();
        //This ensures events and prob arrays are aligned by indices.

        return event(events, prob, rnd, checkWeightSum);
    }

    /**
     * The function toss a random double number and search in witch probability range the sampled number is within to
     * select the corresponding event.
     *
     * @param length  The length of an array or a list, can't be negative or zero.
     * @param rnd     A random number generator
     * @param weights An array of corresponding weights.
     * @return an integer that is used as an index later.
     */
    static private @NonNull ArrayList<Object> tosser(final int length, final @NonNull Random rnd,
                                                     final double @Nullable [] weights) {
        var out = new ArrayList<>();
        val toss = rnd.nextDouble();
        double weightSum = 0.;
        int i = 0;

        if ((length > 0) && weights != null)
            throw new IllegalArgumentException("Contradicting arguments.");

        // fixme validate that the case with identical weights results in the same values as when there is no weights at all the time.

        if (weights == null) {
            if (length == 0) throw new ArithmeticException("Division by zero.");
            val prob = 1. / length;
            while (toss >= weightSum) { // fixme improve this part?, replace addition with multiplication
                weightSum += prob;
                i++;
            }
        } else {
            while (toss >= weightSum) { // fixme replace with cumsum for better accuracy
                weightSum += weights[i]; // fixme we can precalculate this: the cost is the same as in the case of regular summation, the last element is the sum of all weights.
                // fixme searching over *ordered* array is cheap.
                i++;
            }
        }
        out.add(i);
        out.add(toss);
        return out; // fixme check the range of i, if in can return 0, all the dependent code fails
    }

    /**
     * Validates weights to see if they all are strictly positive. Calculates anr returns the corresponding sum of
     * {@code weights}. If {@code checkSumWeights} is {true}, checks if the sum is exactly 1, otherwise calculates the
     * associated probabilities by dividing the weights by the sum of weights, i.e., converts weights into probabilities
     * by 'normalising' them.
     *
     * @param weights         An array of weights.
     * @param checkSumWeights A boolean flag to switch on/off the sum of weights check.
     * @return the sum of all weights.
     */
    private static double validateWeights(final double @NonNull [] weights, final boolean checkSumWeights) {
        for (int i = 0; i < weights.length; i++)
            if (weights[i] <= 0.) throw new IllegalArgumentException("Negative" +
                " weights (probabilities) are not allowed! Check 'weights' array " + Arrays.toString(weights) +
                " element number " + i + ", which currently has the value " + weights[i] + ".");

        var x = sum(weights);

        if (checkSumWeights) if (x != 1.0) throw new IllegalArgumentException("As checkWeightSum is set to true, the" +
            " probability weights must sum to 1. The current weights object " + Arrays.toString(weights) + " has" +
            " elements that sum to " + x + ". Either ensure probability weights sum to 1, or set checkWeightSum" +
            " to false so that the weights will be automatically normalised.");
        else for (int i = 0; i < weights.length; i++) weights[i] /= x;

        return x;
    }

    /**
     * For sampling an event where all events in the sample space have equal probability.
     * The probability for each event = 1 / size of sample space.
     *
     * @param events The possible events in the sample space.
     * @param rnd    A random generator.
     * @return the event chosen.
     */
    public static <T> @NonNull T event(final T @NonNull [] events, final @NonNull Random rnd) {
        return events[(Integer) tosser(events.length, rnd, null).get(0) - 1];
    }

    /**
     * This allows events with types that implement the list interface.
     *
     * @see #event(Object[], Random)
     */
    public static <T> @NonNull T event(final @NonNull AbstractList<T> events, final @NonNull Random rnd) {
        return events.get((Integer) tosser(events.size(), rnd, null).get(0) - 1);
    }

    /**
     * {@code checkSumWeights} defaults to {@code true}.
     *
     * @see #eventPiecewiseConstant(double[], double[], Random, boolean)
     */
    public static double eventPiecewiseConstant(final double @NonNull [] events, final double @NonNull [] prob,
                                                final Random rnd) {
        return eventPiecewiseConstant(events, prob, rnd, true);
    }

    /**
     * Performs a linear interpolation on the (numerical) event domain of a piecewise constant probability distribution.
     * The effective expression is derived from {@code (toss - (x - prob[i-1])) / (x - (x - prob[i-1]))}, where
     * {@code x} is the cumulative probability at the start of {@code events[i]} and {@code (x - prob[i-1])} is the
     * cumulative probability at the start of {@code events[i-1]}.
     *
     * @param events         The discrete set of cuts characterising the domain of a piecewise constant probability
     *                       distribution.
     * @param weights        The discrete set of probabilities characterising a piecewise constant probability
     *                       distribution.
     * @param rnd            The random number generator.
     * @param checkWeightSum If true, will check weights elements sum to 1, otherwise they will be normalised (by
     *                       dividing each element by the sum of the elements).
     * @return The value of the event drawn from a compact domain
     */
    public static double eventPiecewiseConstant(final double @NonNull [] events, final double @NonNull [] weights,
                                                final @NonNull Random rnd, final boolean checkWeightSum) {
        double cumulativeProbability = validateWeights(weights, checkWeightSum);

        var out = tosser(0, rnd, weights);
        int i = (Integer) out.get(0);
        double toss = (Double) out.get(1);

        return events[i - 1] + (events[i] -
            events[i - 1]) * ((toss + weights[i - 1] - cumulativeProbability) / weights[i - 1]);
        // fixme improve this expression
    }


    /**
     * Method to bootstrap regression covariates. This method creates a new set of regression estimates by sampling from
     * a multivariate normal distribution with expected values (means) equal to the 'coefficients' parameter and a
     * covariance matrix as specified.
     *
     * @param map A {@code MultiKeyCoefficientMap} that contains both regression coefficients and a corresponding
     *            covariance matrix. This map is required to only have one key entry in the
     *            {@code MultiKeyCoefficientMap} map's {@code MultiKey} with a heading 'REGRESSOR'. The map must also
     *            contain a value column with the heading 'COEFFICIENT' and an additional value column for each
     *            regressor (covariate), in order to represent the (square, symmetric, positive semi-definite)
     *            covariance matrix.
     * @return a {@code MultiKeyCoefficientMap} containing a new set of bootstrapped regression coefficients.
     */
    public static @NonNull MultiKeyCoefficientMap bootstrap(final @NonNull MultiKeyCoefficientMap map) {
        var keys = map.getKeysNames();
        int numRowsInCovarianceMatrix = map.size();
        //TODO: Check this is correct AND robust, i.e. if we envisage changing the structure of the .xls files, what impact does that have?
        int regressorColumnIndex;
        if (keys.length > 1) throw new IllegalArgumentException("There are more conditional keys in the multiKey of" +
            " the map in RegressionUtils.bootstrap(map). This cannot currently be handled.  \nThe Stack Trace is " +
            "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
        else if (keys[0].equals(RegressionColumnNames.REGRESSOR.toString())) regressorColumnIndex = 0;
        else throw new NullPointerException("There is no key named " + RegressionColumnNames.REGRESSOR + " in the" +
                " map argument of RegressionUtils.bootstrap(map).  If map was loaded from an Excel spreadsheet, " +
                "check there is a column with the heading '" + RegressionColumnNames.REGRESSOR + "'.");
        var valuesNames = map.getValuesNames();
        int numValueColumns = valuesNames.length;

        var indexOfValuesNameMap = new HashMap<String, Integer>();
        var valuesMap = new HashMap<String, Integer>();
        int index = 0;
        int estimateIndex = -1;
        for (int i = 0; i < numValueColumns; i++) {
            valuesMap.put(valuesNames[i], i);

            if (valuesNames[i].equals(RegressionColumnNames.COEFFICIENT.toString())) estimateIndex = i;
            else {
                indexOfValuesNameMap.put(valuesNames[i], index);
                //Create consistent column numbering that excludes the estimates column (the order of columns gets mixed up)
                index++;
            }

        }
        if (estimateIndex == -1) throw new NullPointerException("There is no value label named " +
            RegressionColumnNames.COEFFICIENT + " in the map argument of RegressionUtils.bootstrap(map). If map" +
            " was loaded from an Excel spreadsheet, check there is a column with the heading '" +
            RegressionColumnNames.COEFFICIENT + "'.");

        var means = new double[numRowsInCovarianceMatrix];
        var covarianceMatrix = new double[numRowsInCovarianceMatrix][numRowsInCovarianceMatrix];
        for (var iterator = map.mapIterator(); iterator.hasNext(); ) {
            iterator.next();
            var multiKey = (MultiKey<?>) iterator.getKey();
            String regressor = (String) multiKey.getKey(regressorColumnIndex);
            int rowIndex = indexOfValuesNameMap.get(regressor);
            Object[] mapValuesRow = ((Object[]) map.getValue(multiKey));
            means[rowIndex] = ((Number) mapValuesRow[estimateIndex]).doubleValue();
            //Should throw null pointer exception if the RHS returns null
            for (String covariableName : indexOfValuesNameMap.keySet()) {
                int columnIndex = indexOfValuesNameMap.get(covariableName);
                covarianceMatrix[rowIndex][columnIndex] =
                    ((Number) mapValuesRow[valuesMap.get(covariableName)]).doubleValue();
                //Should throw null pointer exception if the RHS returns null
            }
        }

        var realCovarianceMatrix = new Array2DRowRealMatrix(covarianceMatrix);
        MatrixUtils.checkSymmetric(realCovarianceMatrix, SYMMETRIC_MATRIX_EPS);
        //Not used as cannot know the appropriate value of SYMMETRIC_MATRIX_EPS (relative tolerance) a priori
        var multiNormDist = new MultivariateNormalDistribution(SimulationEngine.getRnd(), means,
            covarianceMatrix);
        means = multiNormDist.sample();
        //This returns the bootstrapped values of the estimates

        //Create new multikeycoefficientmap to return with new bootstrapped column, in addition to estimate and standard error data
        var valueNames = new String[1];
        valueNames[0] = RegressionColumnNames.COEFFICIENT.toString();
        var bootstrapMap = new MultiKeyCoefficientMap(keys, valueNames);

        for (var iterator = map.mapIterator(); iterator.hasNext(); ) {
            iterator.next();

            var multiKey = (MultiKey<?>) iterator.getKey();
            var regressor = (String) multiKey.getKey(regressorColumnIndex);
            int rowIndex = indexOfValuesNameMap.get(regressor);
            bootstrapMap.put(multiKey, means[rowIndex]);
        }
        return bootstrapMap;

    }


    /**
     * Method to bootstrap regression covariates. This method creates a new set of regression estimates by sampling from
     * a multivariate normal distribution with expected values (means) equal to the 'coefficients' parameter and a
     * covariance matrix as specified.
     *
     * @param coefficients     A {@code MultiKeyCoefficientMap} that contains a set of regression coefficients. The
     *                         'coefficients' map is required to only have one key entry in each of the
     *                         {@code MultiKeyCoefficientMap} map's {@code MultiKeys}, with the entries being the name
     *                         of the regression covariates. There must be only one values column containing the
     *                         regression coefficients for each covariate (regressor).
     * @param covarianceMatrix A {@code MultiKeyCoefficientMap} that provides the covariance matrix of a regression's
     *                         coefficients. The covarianceMatrix map is required to only have one key entry in each of
     *                         the {@code MultiKeyCoefficientMap} map's {@code MultiKeys}, with each entry corresponding
     *                         to the name of a regression covariate. The values must contain a name key corresponding
     *                         to each of the covariates, though the ordering of the values (columns) need not match the
     *                         {@code MultiKey} (row) ordering.
     * @return a {@code MultiKeyCoefficientMap} of new regression coefficients that is bootstrapped from the input
     * estimates map.
     */
    public static @NonNull MultiKeyCoefficientMap bootstrap(final @NonNull MultiKeyCoefficientMap coefficients,
                                                            final @NonNull MultiKeyCoefficientMap covarianceMatrix) {
        var coefficientKeys = coefficients.getKeysNames();
        if (coefficientKeys.length > 1) throw new IllegalArgumentException("The estimates map in " +
            "RegressionUtils.bootstrap(estimates, covarianceMatrix) should only have one key entry in the" +
            " MultiKey (and this should be a name of a covariate).\nThe Stack Trace is\n" +
            Arrays.toString(Thread.currentThread().getStackTrace()));

        var valuesNames = coefficients.getValuesNames();
        if (valuesNames.length > 1) throw new IllegalArgumentException("The estimates map in" +
            " RegressionUtils.bootstrap(estimates, covarianceMatrix) should only have one value corresponding to" +
            " each MultiKey (and this should be the value of a regression coefficient corresponding to the key's" +
            " covariate). \nThe Stack Trace is\n" + Arrays.toString(Thread.currentThread().getStackTrace()));

        int numCovariates = coefficients.size();
        var covariates = new String[numCovariates];
        int n = 0;
        for (Object o : coefficients.keySet()) {
            //Order of iteration not guaranteed???
            if (o instanceof MultiKey) {
                covariates[n] = ((MultiKey<?>) o).getKey(0).toString();
                //The order in which covariates and their corresponding covariances are handled within this method is fixed by this ordering.
                n++;
            }
        }

        double[][] covarianceMatrixOrdered;
        var means = new double[numCovariates];
        //Create covariance matrix with an order consistent with the covariates array
        covarianceMatrixOrdered = new double[numCovariates][numCovariates];
        for (int row = 0; row < numCovariates; row++) {
            means[row] = ((Number) coefficients.getValue(covariates[row])).doubleValue();
            for (int col = 0; col < numCovariates; col++)
                covarianceMatrixOrdered[row][col] = ((Number) covarianceMatrix.getValue(covariates[row],
                    covariates[col])).doubleValue();
        }

        var realCovarianceMatrix = new Array2DRowRealMatrix(covarianceMatrixOrdered);
        MatrixUtils.checkSymmetric(realCovarianceMatrix, SYMMETRIC_MATRIX_EPS);
        //Not used as cannot know the appropriate value of SYMMETRIC_MATRIX_EPS (relative tolerance) a priori
        var multiNormDist = new MultivariateNormalDistribution(SimulationEngine.getRnd(), means,
            covarianceMatrixOrdered);
        means = multiNormDist.sample();        //This returns the bootstrapped values of the estimates

        var bootstrapMap = new MultiKeyCoefficientMap(coefficientKeys, valuesNames);

        for (int j = 0; j < numCovariates; j++) bootstrapMap.putValue(covariates[j], means[j]);
        return bootstrapMap;
    }

    /**
     * Method to bootstrap multinomial regression covariates. This method creates a new map of sets of regression
     * coefficients by sampling from a multivariate normal distribution with expected values (means) equal to the
     * regression coefficients contained in the 'coefficientOutcomeMap' and a covariance matrix as specified.
     *
     * @param <T>                           The event (outcome) of a multinomial regression.
     * @param eventRegressionCoefficientMap A map whose keys are the possible events (outcomes) of type {@code T} of the
     *                                      multinomial regression, and whose values are MultiKeyCoefficientMaps each
     *                                      containing a set of regression coefficients corresponding to its event (the
     *                                      key). Each MultiKeyCoefficientMap of regression coefficients is used as
     *                                      expected value of a multivariate normal distribution, which is sampled in
     *                                      order to produce a new set of regression coefficients. The
     *                                      {@code MultiKeyCoefficientMaps} are required to only have one key entry,
     *                                      with the entries being the name of the regression covariates. There must be
     *                                      only one values column in the {@code MultiKeyCoefficientMaps}, containing
     *                                      the regression coefficients for each covariate.
     * @param covarianceMatrix              A {@code MultiKeyCoefficientMap} that provides the covariance matrix of a
     *                                      regression's coefficients. The covarianceMatrix map is required to only have
     *                                      one key entry in each of the {@code MultiKeyCoefficientMap} map's
     *                                      {@code MultiKeys}, with each entry corresponding to a string with the
     *                                      structure [event name]_[covariate name]. So, for example, if the set of
     *                                      events of {@code T} are {@code LowEducation} and {@code HighEducation}, and
     *                                      the regression covariates declared in {@code coefficientOutcomeMap} are
     *                                      'age' and 'gender', then the following {@code MultiKey} entries must exist:
     *                                      "LowEducation_age", "LowEducation_gender", "HighEducation_age",
     *                                      "HighEducation_gender", such that the name of the event is a prefix,
     *                                      the character "_" is the 'regular expression' and the name of the regression
     *                                      covariate is the suffix. This prefix/suffix ordering must be preserved in
     *                                      order to avoid confusion between the name of events and covariates, however
     *                                      the order in which the {@code MultiKeys} are specified does not matter.
     *                                      The values of the {@code MultiKeyCoefficientMap} must contain a name key
     *                                      that corresponds to each MultiKey key entry (to ensure labelling of rows and
     *                                      columns match), though the ordering of the values (columns) need not match
     *                                      the {@code MultiKey} (row) ordering.
     * @return a Map whose keys are the possible events (outcomes) of type {@code T} of the multinomial regression and
     * whose values are {@code MultiKeyCoefficientMap} with new regression coefficients (one set of coefficients for
     * each event).
     */
    public static <T> @NonNull Map<T, MultiKeyCoefficientMap> bootstrapMultinomialRegression(
        final @NonNull Map<T, MultiKeyCoefficientMap> eventRegressionCoefficientMap,
        final @NonNull MultiKeyCoefficientMap covarianceMatrix,
        final @NonNull Class<T> enumType) {
        var possibleEvents = enumType.getEnumConstants();

        var specifiedEvents = eventRegressionCoefficientMap.keySet();

        int missingEvents = 0;
        int count = 0;
        String[] multiKeyMapKeyNames = null;            //The name of the MultiKey in the MultiKeyCoefficientMaps
        String[] multiKeyMapValueNames = null;            //The name of the values in the MultiKeyCoefficientMaps
        Set<?> covariateMultiKeys = null;
        T baseT = null;
        for (T t : possibleEvents) {
            if (specifiedEvents.contains(t)) {
                var map = eventRegressionCoefficientMap.get(t);
                if (count == 0) {
                    baseT = t;
                    //base event which we compare the key names and value names of all other events' MultiKeyCoefficientMaps
                    multiKeyMapKeyNames = map.getKeysNames();
                    multiKeyMapValueNames = map.getValuesNames();
                    covariateMultiKeys = map.keySet();
                } else {
                    var otherKeyNames = map.getKeysNames();
                    var otherValueNames = map.getValuesNames();
                    var otherMultiKeys = map.keySet();
                    //Check dimensions match
                    if (multiKeyMapKeyNames.length != otherKeyNames.length) throw new IllegalArgumentException("The" +
                        " number of keys in the regression coefficient MultiKeyCofficientMap for event " + t +
                        " does not match the number of keys in event " + baseT);
                    if (multiKeyMapValueNames.length != otherValueNames.length) throw new IllegalArgumentException(
                        "The number of value names in the regression coefficient MultiKeyCofficientMap for event " +
                            t + " does not match the number of value names in event " + baseT);
                    if (map.keySet().size() != covariateMultiKeys.size()) throw new IllegalArgumentException("The" +
                        " number of covariates specified in the regression coefficient MultiKeyCofficientMap for" +
                        " event " + t + " does not match the number of covariates in event " + baseT);

                    //Check key names and value names match between events
                    for (int i = 0; i < multiKeyMapKeyNames.length; i++)
                        if (!multiKeyMapKeyNames[i].equals(otherKeyNames[i])) throw new IllegalArgumentException("The" +
                            " key names in the regression coefficient MultiKeyCofficientMap for event " + t +
                            " do not match the key names in event " + baseT);
                    for (int i = 0; i < multiKeyMapValueNames.length; i++)
                        if (!multiKeyMapValueNames[i].equals(otherValueNames[i])) throw new IllegalArgumentException(
                            "The value names in the regression coefficient MultiKeyCofficientMap for event " + t +
                                " do not match the value names in event " + baseT);
                    //Check that all events have the same MultiKeys (regression covariates)
                    for (var mk : otherMultiKeys)
                        if (!covariateMultiKeys.contains(mk))
                            throw new IllegalArgumentException("The covariate " + ((MultiKey<?>) mk).getKey(0) +
                                " specified in the regression coefficient MultiKeyCofficientMap for event " + t +
                                " does not appear in the set of covariates of event " + baseT +
                                ". Check that all events have the same set of regression covariates!");
                }
                count++;
            } else {
                missingEvents++;
                //The multinomial regression can go without specifying coefficients for 1 of the events (outcomes) of
                // the type T as the probability of this event can be determined by the residual of the other probabilities.
                if (missingEvents > 1) throw new RuntimeException("MultiProbitRegression has been constructed with a " +
                    "map that does not contain enough of the possible values of the type T. The map should" +
                    " contain the full number of T values, or one less than the full number of T values (in which" +
                    " case, the missing value is considered the 'default' case whose regression betas are all" +
                    " zero).");
            }
        }

        var enlargedCoefficientMap = new MultiKeyCoefficientMap(multiKeyMapKeyNames, multiKeyMapValueNames);
        //Create new MultiKeyCoefficientMap that has matching key and value names as (one of) the entries of the Map<T, MultiKeyCoefficientMap>.
        for (var event : specifiedEvents) {
            var regCoefficientsMap = eventRegressionCoefficientMap.get(event);
            for (var o : regCoefficientsMap.keySet()) {
                var mk = (MultiKey<?>) o;
                var combinedName = event.toString() + "_" + mk.getKey(0).toString();
                enlargedCoefficientMap.putValue(combinedName, regCoefficientsMap.getValue(mk));
            }
        }

        enlargedCoefficientMap = bootstrap(enlargedCoefficientMap, covarianceMatrix);

        var newMap = new HashMap<T, MultiKeyCoefficientMap>(specifiedEvents.size());
        for (T event : specifiedEvents) {
            var newCoefficientMap = new MultiKeyCoefficientMap(multiKeyMapKeyNames, multiKeyMapValueNames);
            for (var mk : covariateMultiKeys) {
                String combinedName = event.toString() + "_" + ((MultiKey<?>) mk).getKey(0).toString();
                double regCoefficient = ((Number) enlargedCoefficientMap.getValue(combinedName)).doubleValue();
                newCoefficientMap.putValue(mk, regCoefficient);
            }
            newMap.put(event, newCoefficientMap);
        }
        return newMap;
    }
}
