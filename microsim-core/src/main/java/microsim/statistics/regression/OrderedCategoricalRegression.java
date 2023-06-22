package microsim.statistics.regression;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class OrderedCategoricalRegression<T extends Enum<T>> extends LinearRegression {

    private Random random;
    private Class<T> enumType;
    private Normal normalRV;
    private RegressionType regression;

    public OrderedCategoricalRegression(MultiKeyCoefficientMap map, Class<T> enumType, RegressionType rr) {
        super(map);
        random = SimulationEngine.getRnd();
        this.enumType = enumType;
        regression = rr;
        if (RegressionType.OrderedProbit.equals(regression)) normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
        if (this.enumType.getEnumConstants().length <= 2) {
            throw new RuntimeException("Ordered categorical regression requires a regression type with more than 2 alternatives");
        }
    }

    public OrderedCategoricalRegression(MultiKeyCoefficientMap map, Class<T> enumType, RegressionType rr, Random rnd) {
        this(map, enumType, rr);
        random = rnd;
    }

    private double logitTransform(double score) {
        return 1.0 / (1.0 + Math.exp(-score));
    }
    private double probitTransform(double score) {
        return normalRV.cdf(score);
    }

    public <E extends Enum<E>> Map<T, Double> getProbabilities(IDoubleSource iDblSrc, Class<E> Regressors) {
        Map<T, Double> probs = new LinkedHashMap<>();

        double score0 = super.getScore(iDblSrc, Regressors);

        T[] events = enumType.getEnumConstants();
        double probHere, probPreceding = 0.0;
        for (int ii = 0; ii < events.length-1; ii++) {
            String key = Strings.concat("Cut", Integer.toString(ii+1));
            double cutVal = super.getCoefficient(key);
            if (RegressionType.OrderedLogit.equals(regression)) {
                probHere = logitTransform(cutVal-score0);
            } else if (RegressionType.OrderedProbit.equals(regression)) {
                probHere = probitTransform(cutVal-score0);
            } else {
                throw new RuntimeException("ordered regression type not recognised when evaluating probabilities");
            }
            probs.put((T) events[ii], probHere - probPreceding);
            probPreceding = probHere;
        }
        probs.put((T) events[events.length-1], 1.0 - probPreceding);
        return probs;
    }

    public <E extends Enum<E>> T eventType(IDoubleSource iDblSrc, Class<E> Regressors) {
        Map<T, Double> probs = getProbabilities(iDblSrc, Regressors);

        T[] eventProbs = enumType.getEnumConstants();
        double[] probArray = new double[probs.size()];
        for (int ii = 0; ii < eventProbs.length; ii++) {
            probArray[ii] = probs.get(eventProbs[ii]);
        }

        return RegressionUtils.event(eventProbs, probArray, random);
    }
}
