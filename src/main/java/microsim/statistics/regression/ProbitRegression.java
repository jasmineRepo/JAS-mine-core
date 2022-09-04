package microsim.statistics.regression;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.DoubleSource;
import microsim.statistics.ObjectSource;

import java.util.Map;
import java.util.Random;

public class ProbitRegression extends LinearRegression implements BinaryChoiceRegression {

	private final Random random;

	private final Normal normalRV;

	public ProbitRegression(MultiKeyCoefficientMap map) {
		super(map);
		random = SimulationEngine.getRnd();
		normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
	}

	public ProbitRegression(MultiKeyCoefficientMap map, Random random) {
		super(map);
		this.random = random;
		normalRV = new Normal(0.0, 1.0, new MersenneTwister(random.nextInt()));
	}

	public double getProbability(Map<String, Double> values) {
		final double score = super.getScore(values);
		return normalRV.cdf(score);
	}

	public double getProbability(Object individual) {
		final double score = super.getScore(individual);
		return normalRV.cdf(score);
	}

//	@Override
	public boolean event(Object individual) {
		final double probability = getProbability(individual);
		return (random.nextDouble() < probability);
	}

//	@Override
	public boolean event(Map<String, Double> values) {
		final double probability = getProbability(values);
		return (random.nextDouble() < probability);
	}

	////////////////////////////////
	//	New methods
	//	@author Ross Richardson
	////////////////////////////////
	public <T extends Enum<T>> double getProbability(DoubleSource iDblSrc, Class<T> enumType) {
		final double score = super.getScore(iDblSrc, enumType);
		return normalRV.cdf(score);
	}

	public <T extends Enum<T>> boolean event(DoubleSource iDblSrc, Class<T> enumType) {
		final double probability = getProbability(iDblSrc, enumType);
		return (random.nextDouble() < probability);
	}

	public <T extends Enum<T>, U extends Enum<U>> double getProbability(DoubleSource iDblSrc, Class<T> enumTypeDbl, ObjectSource iObjSrc, Class<U> enumTypeObj) {
		final double score = super.getScore(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
		return normalRV.cdf(score);
	}

	public <T extends Enum<T>, U extends Enum<U>> boolean event(DoubleSource iDblSrc, Class<T> enumTypeDbl, ObjectSource iObjSrc, Class<U> enumTypeObj) {
		final double probability = getProbability(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
		return (random.nextDouble() < probability);
	}

}
