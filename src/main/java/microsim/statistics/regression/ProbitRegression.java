package microsim.statistics.regression;

import java.util.Map;
import java.util.Random;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IObjectSource;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class ProbitRegression extends LinearRegression implements IBinaryChoiceRegression {

	private Random random;
	
	private Normal normalRV;

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
		return (double) normalRV.cdf(score);
	}
	
	public double getProbability(Object individual) {
		final double score = super.getScore(individual);		
		return (double) normalRV.cdf(score);		
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
	public <T extends Enum<T>> double getProbability(IDoubleSource iDblSrc, Class<T> enumType) {
		final double score = super.getScore(iDblSrc, enumType);				
		return (double) normalRV.cdf(score);
	}
	
	public <T extends Enum<T>> boolean event(IDoubleSource iDblSrc, Class<T> enumType) {
		final double probability = getProbability(iDblSrc, enumType);
		return (random.nextDouble() < probability);		
	}
	
	public <T extends Enum<T>, U extends Enum<U>> double getProbability(IDoubleSource iDblSrc, Class<T> enumTypeDbl, IObjectSource iObjSrc, Class<U> enumTypeObj) {
		final double score = super.getScore(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);		
		return (double) normalRV.cdf(score);			
	}
	
	public <T extends Enum<T>, U extends Enum<U>> boolean event(IDoubleSource iDblSrc, Class<T> enumTypeDbl, IObjectSource iObjSrc, Class<U> enumTypeObj) {
		final double probability = getProbability(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
		return (random.nextDouble() < probability);		
	}

}
