package microsim.statistics.regression;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OrderedProbitRegression<T extends Enum<T>> extends LinearRegression {

    private Random random;
    private Class<T> enumType;
    private Normal normalRV;
    public OrderedProbitRegression(MultiKeyCoefficientMap map, Class<T> enumType) {
        super(map);
        random = SimulationEngine.getRnd();
        this.enumType = enumType;
        normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
    }

    public OrderedProbitRegression(MultiKeyCoefficientMap map, Class<T> enumType, Random rnd) {
        super(map);
        this.random = rnd;
        this.enumType = enumType;
        normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
    }

    private double probitTransform(double score) {
        return (double) normalRV.cdf(score);
    }
    public <E extends Enum<E>> Map<T, Double> getProbabilities(IDoubleSource iDblSrc, Class<E> Regressors) {
        Map<T, Double> probs = new HashMap<>();

        double score0 = super.getScore(iDblSrc, Regressors);

        T[] events = enumType.getEnumConstants();
        double probHere, probPreceding = 0.0;
        for (int ii = 0; ii < events.length-1; ii++) {
            String key = Strings.concat("cut", Integer.toString(ii));
            double cutVal = super.getCoefficient(key);
            probHere = probitTransform((cutVal-score0));
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
