package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.DoubleSource;
import microsim.statistics.ObjectSource;

import java.util.Map;
import java.util.Random;

public class LogitRegression extends LinearRegression implements BinaryChoiceRegression {

	private Random random;
	
	public LogitRegression(MultiKeyCoefficientMap map) {
		super(map);
		random = SimulationEngine.getRnd();
	}

	public LogitRegression(MultiKeyCoefficientMap map, Random random) {
		super(map);		
		this.random = random;
	}
	

	public double getProbability(Map<String, Double> values) {
		final double score = super.getScore(values);		
		return (double) 1 / (1 + Math.exp(- score));
	}
	
	public double getProbability(Object individual) {
		final double score = super.getScore(individual);		
		return (double) 1 / (1 + Math.exp(- score));			
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
	//
	////////////////////////////////
	
	public <T extends Enum<T>> double getProbability(DoubleSource iDblSrc, Class<T> enumType) {
		final double score = super.getScore(iDblSrc, enumType);		
		return (double) 1 / (1 + Math.exp(- score));			
	}
	
	public <T extends Enum<T>> boolean event(DoubleSource iDblSrc, Class<T> enumType) {
		final double probability = getProbability(iDblSrc, enumType);
		return (random.nextDouble() < probability);		
	}
	
	public <T extends Enum<T>, U extends Enum<U>> double getProbability(DoubleSource iDblSrc, Class<T> enumTypeDbl, ObjectSource iObjSrc, Class<U> enumTypeObj) {
		final double score = super.getScore(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);		
		return (double) 1 / (1 + Math.exp(- score));			
	}
	
	public <T extends Enum<T>, U extends Enum<U>> boolean event(DoubleSource iDblSrc, Class<T> enumTypeDbl, ObjectSource iObjSrc, Class<U> enumTypeObj) {
		final double probability = getProbability(iDblSrc, enumTypeDbl, iObjSrc, enumTypeObj);
		return (random.nextDouble() < probability);		
	}

}
