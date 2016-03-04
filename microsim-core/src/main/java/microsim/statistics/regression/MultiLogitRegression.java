package microsim.statistics.regression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class MultiLogitRegression<T extends Enum<T>> implements IMultipleChoiceRegression<T> {

	private Random random;
	
	private Map<T, MultiKeyCoefficientMap> maps = null;
		
	public MultiLogitRegression(Map<T, MultiKeyCoefficientMap> maps) {		
		random = SimulationEngine.getRnd();
		this.maps = maps;
		int count = 0;
		Set<String> covariateNames = new HashSet<String>();
		for(T event : maps.keySet()) {
			@SuppressWarnings("unchecked")
			Set<Object> covariateSet = (maps.get(event)).keySet();
			for(Object covariate : covariateSet) {
				if(count == 0) {
					covariateNames.add(covariate.toString());
				}
				else {
					if(!covariateNames.contains(covariate.toString()) || covariateNames.size() != covariateSet.size()) {
						throw new RuntimeException("The covariates specified for each outcome of type T in the MultiLogitRegression object do not match!");
					}
				}
//				if((maps.get(event)).getValue(covariate).equals(0)) {
//					System.out.print("\nWarning - Regression object has been created with a regression co-efficient of 0 for regressor " + covariate.toString() + ". Check whether it is desired to have a regression co-efficient of 0 (which means the regressor has no influence on the regression score).  Although the simulation will run, consider removing unnecessary regression co-efficients to improve computational efficiency.  Stack Trace is:"
//							+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//				}
			}
			count++;
		}

	}

	public MultiLogitRegression(Map<T, MultiKeyCoefficientMap> maps, Random random) {			
		this.random = random;
		this.maps = maps;
		int count = 0;
		Set<String> covariateNames = new HashSet<String>();
		for(T event : maps.keySet()) {
			@SuppressWarnings("unchecked")
			Set<Object> covariateSet = (maps.get(event)).keySet();
			for(Object covariate : covariateSet) {
				if(count == 0) {
					covariateNames.add(covariate.toString());
				}
				else {
					if(!covariateNames.contains(covariate.toString()) || covariateNames.size() != covariateSet.size()) {
						throw new RuntimeException("The covariates specified for each outcome of type T in the MultiLogitRegression object do not match!");
					}
				}
//				if((maps.get(event)).getValue(covariate).equals(0)) {
//					System.out.print("\nWarning - Regression object has been created with a regression co-efficient of 0 for regressor " + covariate.toString() + ". Check whether it is desired to have a regression co-efficient of 0 (which means the regressor has no influence on the regression score).  Although the simulation will run, consider removing unnecessary regression co-efficients to improve computational efficiency.  Stack Trace is:"
//							+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//				}
			}
			count++;
		}

	}
	
	/**
	 *
	 * Warning - only use when MultiLogitRegression's maps field has values that are MultiKeyCoefficientMaps with only one key.  This method only looks at the first key of the MultiKeyCoefficientMap field of LinearRegression, so any other keys that are used to distinguish a unique multiKey (i.e. if the first key occurs more than once) will be ignored! If the first key of the multiKey appears more than once, the method would return an incorrect value, so will throw an exception.   
	 * @param values
	 * @return
	 */
	public double getLogitTransformOfScore(T event, Map<String, Double> values) {		
		final double score = LinearRegression.computeScore(maps.get(event), values);		
		return (double) 1 / (1 + Math.exp(- score));
	}
	
	public double getLogitTransformOfScore(T event, Object individual) {
		final double score = LinearRegression.computeScore(maps.get(event), individual);
		return (double) 1 / (1 + Math.exp(- score));		
	}
	
	//Original version was incorrect - did not normalise probabilities.  Corrected by Ross Richardson.
//	@Override
	public T eventType(Object individual) {		
		Map<T, Double> probs = new HashMap<T, Double>();

		double denominator = 0.; 
		
		for (T event : maps.keySet()) {
			double LogitTransformOfScore = getLogitTransformOfScore(event, individual);
			probs.put(event, LogitTransformOfScore);
			denominator += LogitTransformOfScore;			
		}
		
		//Check whether there is a base case that has not been included in the regression specification variable (maps).
		T k = null;
		T[] eventProbs = (T[]) k.getClass().getEnumConstants();
		int countNullEventProbs = 0;
		for (int i = 0; i < eventProbs.length; i++) {
			if (probs.get(eventProbs[i]) == null) {						//The multiLogit regression can go without specifying coefficients for 1 of the outcomes as the probability of this event can be determined by the residual of the other probabilities.
				countNullEventProbs++;									//Check no more than one event has null prob, so that it is valid to take the residual to find the probability
				if(countNullEventProbs > 1) {
//					throw new RuntimeException("countNullEventProbs > 1 in MultiLogitRegression object!  More than one event does not have a probability, so the residual cannot be used for the missing probabilities.");
					throw new RuntimeException("MultiLogitRegression has been constructed with a map that does not contain enough of the possible values of the type T.  The map should contain the full number of T values, or one less than the full number of T values (in which case, the missing value is considered the 'default' case whose regression betas are all zero).");
				}
				else {
					denominator += 0.5;		//We include the base case, where score = 0 (as betas are set to zero).  The normalRV.cdf(0) = 0.5 (as the standard normal distribution is symmetric).  The other cases have already been incremented into the denominator.
					probs.put((T) eventProbs[i], 0.5/denominator);		//The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying the Logit transform (the cumulative standard normal distribution) to the score of 0, and the denominator is the sum of Logit transforms for all events.				
				}						
			}			
		}
		
		//Normalise the probabilities of the events specified in the regression maps
		for (T event : maps.keySet()) {		//Only iterate through the cases specified in the regression maps - the base case has already been normalised.
			double LogitTransformOfScoreForEvent = probs.get(event);
			probs.put(event, LogitTransformOfScoreForEvent/denominator);		//Normalise the Logit transform of score (the application of the standard normal cumulative distribution to the score) of the event by the sum for all events
		}
		
		double[] probArray = new double[probs.size()]; 
		for (int i = 0; i < eventProbs.length; i++) {
			probArray[i] = probs.get(eventProbs[i]);
		}
		
		return RegressionUtils.event(eventProbs, probArray, random);				
	}

//	@Override
	/**
	 *
	 * Warning - only use when MultiLogitRegression's maps field has values that are MultiKeyCoefficientMaps with only one key.  This method only looks at the first key of the MultiKeyCoefficientMap field of LinearRegression, so any other keys that are used to distinguish a unique multiKey (i.e. if the first key occurs more than once) will be ignored! If the first key of the multiKey appears more than once, the method would return an incorrect value, so will throw an exception.   
	 * @param values
	 * @return
	 */
	public T eventType(Map<String, Double> values) {
		Map<T, Double> probs = new HashMap<T, Double>();

		double denominator = 0.0;

		for (T event : maps.keySet()) {
			double LogitTransformOfScore = getLogitTransformOfScore(event, values);
			probs.put(event, LogitTransformOfScore);
			denominator += LogitTransformOfScore;			
		}

		//Check whether there is a base case that has not been included in the regression specification variable (maps).
		T k = null;
		T[] eventProbs = (T[]) k.getClass().getEnumConstants();
		int countNullEventProbs = 0;
		for (int i = 0; i < eventProbs.length; i++) {
			if (probs.get(eventProbs[i]) == null) {						//The multiLogit regression can go without specifying coefficients for 1 of the outcomes as the probability of this event can be determined by the residual of the other probabilities.
				countNullEventProbs++;									//Check no more than one event has null prob, so that it is valid to take the residual to find the probability
				if(countNullEventProbs > 1) {
//					throw new RuntimeException("countNullEventProbs > 1 in MultiLogitRegression object!  More than one event does not have a probability, so the residual cannot be used for the missing probabilities.");
					throw new RuntimeException("MultiLogitRegression has been constructed with a map that does not contain enough of the possible values of the type T.  The map should contain the full number of T values, or one less than the full number of T values (in which case, the missing value is considered the 'default' case whose regression betas are all zero).");
				}
				else {
					denominator += 0.5;		//We include the base case, where score = 0 (as betas are set to zero).  The normalRV.cdf(0) = 0.5 (as the standard normal distribution is symmetric).  The other cases have already been incremented into the denominator.
					probs.put((T) eventProbs[i], 0.5/denominator);		//The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying the Logit transform (the cumulative standard normal distribution) to the score of 0, and the denominator is the sum of Logit transforms for all events.				
				}						
			}			
		}

		//Normalise the probabilities of the events specified in the regression maps
		for (T event : maps.keySet()) {		//Only iterate through the cases specified in the regression maps - the base case has already been normalised.
			double LogitTransformOfScoreForEvent = probs.get(event);
			probs.put(event, LogitTransformOfScoreForEvent/denominator);		//Normalise the Logit transform of score (the application of the standard normal cumulative distribution to the score) of the event by the sum for all events
		}

		double[] probArray = new double[probs.size()]; 
		for (int i = 0; i < eventProbs.length; i++) {
			probArray[i] = probs.get(eventProbs[i]);
		}

		return RegressionUtils.event(eventProbs, probArray, random);		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// New methods 
	// @author Ross Richardson
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public <E extends Enum<E>> double getLogitTransformOfScore(T event, IDoubleSource iDblSrc, Class<E> Regressors) {
		final double score = LinearRegression.computeScore(maps.get(event), iDblSrc, Regressors);
		return (double) 1 / (1 + Math.exp(- score));		
	}
	
	public <E extends Enum<E>> T eventType(IDoubleSource iDblSrc, Class<E> Regressors, Class<T> enumType) {		
		Map<T, Double> probs = new HashMap<T, Double>();

		double denominator = 0.; 
		
		for (T event : maps.keySet()) {
			double LogitTransformOfScore = getLogitTransformOfScore(event, iDblSrc, Regressors);
			probs.put(event, LogitTransformOfScore);
			denominator += LogitTransformOfScore;			
		}
		
		//Check whether there is a base case that has not been included in the regression specification variable (maps).
		T[] eventProbs = enumType.getEnumConstants();
//		T[] eventProbs = (T[]) maps.keySet().getClass().getEnumConstants();		//Results in Null Pointer Exception

		int countNullEventProbs = 0;
		for (int i = 0; i < eventProbs.length; i++) {
			if (probs.get(eventProbs[i]) == null) {						//The multiLogit regression can go without specifying coefficients for 1 of the outcomes as the probability of this event can be determined by the residual of the other probabilities.
				countNullEventProbs++;									//Check no more than one event has null prob, so that it is valid to take the residual to find the probability
				if(countNullEventProbs > 1) {
//					throw new RuntimeException("countNullEventProbs > 1 in MultiLogitRegression object!  More than one event does not have a probability, so the residual cannot be used for the missing probabilities.");
					throw new RuntimeException("MultiLogitRegression has been constructed with a map that does not contain enough of the possible values of the type T.  The map should contain the full number of T values, or one less than the full number of T values (in which case, the missing value is considered the 'default' case whose regression betas are all zero).");
				}
				else {
					denominator += 0.5;		//We include the base case, where score = 0 (as betas are set to zero).  The normalRV.cdf(0) = 0.5 (as the standard normal distribution is symmetric).  The other cases have already been incremented into the denominator.
					probs.put((T) eventProbs[i], 0.5/denominator);		//The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying the Logit transform (the cumulative standard normal distribution) to the score of 0, and the denominator is the sum of Logit transforms for all events.				
				}						
			}			
		}
		
		//Normalise the probabilities of the events specified in the regression maps
		for (T event : maps.keySet()) {		//Only iterate through the cases specified in the regression maps - the base case has already been normalised.
			double LogitTransformOfScoreForEvent = probs.get(event);
			probs.put(event, LogitTransformOfScoreForEvent/denominator);		//Normalise the Logit transform of score (the application of the standard normal cumulative distribution to the score) of the event by the sum for all events
		}
		
		double[] probArray = new double[probs.size()]; 
		for (int i = 0; i < eventProbs.length; i++) {
			probArray[i] = probs.get(eventProbs[i]);
		}
		
		return RegressionUtils.event(eventProbs, probArray, random);				
	}

	
}
