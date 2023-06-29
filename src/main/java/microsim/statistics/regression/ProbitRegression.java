package microsim.statistics.regression;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import lombok.NonNull;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.DoubleSource;
import microsim.statistics.ObjectSource;

import java.util.Map;
import java.util.Random;

public class ProbitRegression extends LinearRegression implements BinaryChoiceRegression {

    private final Random random;

    private final Normal normalRV;

    public ProbitRegression(final @NonNull MultiKeyCoefficientMap map) {
        this(map, SimulationEngine.getRnd());
    }

    public ProbitRegression(final @NonNull MultiKeyCoefficientMap map, final @NonNull Random random) {
        super(map);
        this.random = random;
        normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
    }

    public double getProbability(final @NonNull Map<String, Double> values) {
        final double score = super.getScore(values);
        return normalRV.cdf(score);
    }

    public double getProbability(final @NonNull Object individual) {
        final double score = super.getScore(individual);
        return normalRV.cdf(score);
    }

    public boolean event(final @NonNull Object individual) {
        final double probability = getProbability(individual);
        return (random.nextDouble() < probability);
    }

    public boolean event(final @NonNull Map<String, Double> values) {
        final double probability = getProbability(values);
        return (random.nextDouble() < probability);
    }

    public <T extends Enum<T>> double getProbability(final @NonNull DoubleSource iDblSrc,
                                                     final @NonNull Class<T> enumType) {
        final double score = super.getScore(iDblSrc, enumType);
        return normalRV.cdf(score);
    }

    public <T extends Enum<T>> boolean event(final @NonNull DoubleSource iDblSrc, final @NonNull Class<T> enumType) {
        final double probability = getProbability(iDblSrc, enumType);
        return (random.nextDouble() < probability);
    }

    public <T extends Enum<T>, U extends Enum<U>> double getProbability(final @NonNull DoubleSource iDblSrc,
                                                                        final @NonNull Class<T> enumTypeDbl,
                                                                        final @NonNull ObjectSource iObjSrc,
                                                                        final @NonNull Class<U> enumTypeObj) {
        final double score = super.getScore(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
        return normalRV.cdf(score);
    }

    public <T extends Enum<T>, U extends Enum<U>> boolean event(final @NonNull DoubleSource iDblSrc,
                                                                final @NonNull Class<T> enumTypeDbl,
                                                                final @NonNull ObjectSource iObjSrc,
                                                                final @NonNull Class<U> enumTypeObj) {
        final double probability = getProbability(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
        return (random.nextDouble() < probability);
    }
}
