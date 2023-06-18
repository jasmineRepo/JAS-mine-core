package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OrderedLogitRegression<T extends Enum<T>> extends LinearRegression {

    private Random random;
    private Class<T> enumType;

    public OrderedLogitRegression(MultiKeyCoefficientMap map, Class<T> enumType) {
        super(map);
        random = SimulationEngine.getRnd();
        this.enumType = enumType;
    }

    public OrderedLogitRegression(MultiKeyCoefficientMap map, Class<T> enumType, Random random) {
        super(map);
        this.random = random;
        this.enumType = enumType;
    }

    private double logitTransform(double score) {
        return 1.0 / (1.0 + Math.exp(-score));
    }
    public <E extends Enum<E>> Map<T, Double> getProbabilities(IDoubleSource iDblSrc, Class<E> Regressors) {
        Map<T, Double> probs = new HashMap<>();

        double score0 = super.getScore(iDblSrc, Regressors);

        T[] events = enumType.getEnumConstants();
        double probHere, probPreceding = 0.0;
        for (int ii = 0; ii < events.length-1; ii++) {
            String key = Strings.concat("cut", Integer.toString(ii));
            double cutVal = super.getCoefficient(key);
            probHere = logitTransform((cutVal-score0));
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
