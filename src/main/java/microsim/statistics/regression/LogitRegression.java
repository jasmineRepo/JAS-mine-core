package microsim.statistics.regression;

import cern.jet.random.engine.MersenneTwister;
import lombok.NonNull;
import lombok.val;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.DoubleSource;
import microsim.statistics.ObjectSource;

import java.util.Map;

public class LogitRegression extends LinearRegression implements BinaryChoiceRegression {

    private final MersenneTwister random;

    public LogitRegression(final @NonNull MultiKeyCoefficientMap map) {
        this(map, SimulationEngine.getRnd());
    }

    public LogitRegression(final @NonNull MultiKeyCoefficientMap map, final @NonNull MersenneTwister random) {
        super(map);
        this.random = random;
    }


    public double getProbability(final @NonNull Map<String, Double> values) {
        val score = super.getScore(values);
        return (double) 1 / (1 + Math.exp(-score));
    }

    public double getProbability(final @NonNull Object individual) {
        val score = super.getScore(individual);
        return (double) 1 / (1 + Math.exp(-score));
    }

    public boolean event(final @NonNull Object individual) {
        val probability = getProbability(individual);
        return (random.nextDouble() < probability);
    }

    public boolean event(final @NonNull Map<String, Double> values) {
        val probability = getProbability(values);
        return (random.nextDouble() < probability);
    }

    public <T extends Enum<T>> double getProbability(final @NonNull DoubleSource iDblSrc,
                                                     final @NonNull Class<T> enumType) {
        val score = super.getScore(iDblSrc, enumType);
        return (double) 1 / (1 + Math.exp(-score));
    }

    public <T extends Enum<T>> boolean event(final @NonNull DoubleSource iDblSrc, final @NonNull Class<T> enumType) {
        val probability = getProbability(iDblSrc, enumType);
        return (random.nextDouble() < probability);
    }

    public <T extends Enum<T>, U extends Enum<U>> double getProbability(final @NonNull DoubleSource iDblSrc,
                                                                        final @NonNull Class<T> enumTypeDbl,
                                                                        final @NonNull ObjectSource iObjSrc,
                                                                        final @NonNull Class<U> enumTypeObj) {
        val score = super.getScore(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
        return (double) 1 / (1 + Math.exp(-score));
    }

    public <T extends Enum<T>, U extends Enum<U>> boolean event(final @NonNull DoubleSource iDblSrc,
                                                                final @NonNull Class<T> enumTypeDbl,
                                                                final @NonNull ObjectSource iObjSrc,
                                                                final @NonNull Class<U> enumTypeObj) {
        val probability = getProbability(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
        return (random.nextDouble() < probability);
    }

}
