package microsim.statistics.regression;

import java.util.*;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import org.apache.commons.collections4.keyvalue.MultiKey;

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
	 * Method to package multinomialCoeffMap for instantiating a MultiLogitRegression object
	 * @param clazz an enum class defining the multinomial alternatives considered for analysis
	 * @param multinomialCoefficients a standard MultiKeyCoefficientMap object used to read parameters from Excel and bootstrap
	 *                                The method assumes that coefficients are supplied for each (or N-1) discrete alternative,
	 *                                where "_XXX" is appendend to each coefficient name to indicate association with alternative "XXX"
	 * @return Map<E, MultiKeyCoefficientMap> multinomialCoeffMap
	 * @param <E> Enum object
	 */
	public static <E extends Enum<E>> Map<E, MultiKeyCoefficientMap> populateMultinomialCoefficientMap(Class<E> clazz, MultiKeyCoefficientMap multinomialCoefficients) {

		// create return object
		Map<E, MultiKeyCoefficientMap> multinomialCoeffMap = new LinkedHashMap<>();

		// check inputs
		if (!clazz.isEnum())
			throw new RuntimeException("call to population multinomial coefficient map without defining enum class");
		if (clazz.getEnumConstants().length < 3)
			throw new RuntimeException("multinomial regression must include at least three enum alternatives");
		if (multinomialCoefficients.getKeysNames().length != 1)
			throw new RuntimeException("The remapping routine for multinomial regressions is designed for use with single key coefficient maps.");

		// construct regressors key list
		HashSet<String> regressors = new HashSet<String>();
		for (Object multiKey : multinomialCoefficients.keySet()) {
			final String key = (String) ((MultiKey) multiKey).getKey(0);
			if(!regressors.add(key))
				throw new RuntimeException("Regressor key " + key + " in multinomial remapping is not unique.");
		}

		// populate return object
		int added = 0;
		for(E ee : clazz.getEnumConstants()) {
			// loop over each discrete option

			// initialise storage object
			String[] keyVector = new String[]{"REGRESSOR"};
			String[] valueVector = new String[]{"COEFFICIENT"};
			MultiKeyCoefficientMap coefficients = new MultiKeyCoefficientMap(keyVector, valueVector);

			// define identifier and flags
			String target = new StringBuilder().append("_").append(ee).toString();
			boolean flagAdd = false;
			for (String key : regressors) {
				// search for coefficients to add to current

				if (key.endsWith(target)) {

					Object[] keyValueVector = new Object[2];
					String keyHere = key.substring(0,key.length()-target.length());
					Double valHere;
					if(multinomialCoefficients.getValuesNames().length == 1) {
						valHere = ((Number)(multinomialCoefficients.getValue(key))).doubleValue();
					}
					else {
						String columnName = RegressionColumnNames.COEFFICIENT.toString();
						valHere = ((Number)(multinomialCoefficients.getValue(key, columnName))).doubleValue();	//This allows the prospect of there being several value columns corresponding to not only the coefficients, but also the covariance matrix to be used with RegressionUtils.bootstrap() for example.
					}
					keyValueVector[0] = keyHere;
					keyValueVector[1] = valHere;
					coefficients.putValue(keyValueVector);
					added += 1;
					if (valHere!=null && valHere!=0.0)
						flagAdd = true;
				}
			}
			if (flagAdd) {
				multinomialCoeffMap.put(ee, coefficients);
			}
		}
		if (added != regressors.size())
			throw new RuntimeException("Failed to allocate all supplied regressors in multinomial remapping.");

		return multinomialCoeffMap;
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
	// @author editted by Justin van de Ven to correct bug in evaluation of probabilities
	//////////////////////////////////////////////////////////////////////////////////////////////

	public <E extends Enum<E>> double getLogitTransformOfScore(T event, IDoubleSource iDblSrc, Class<E> Regressors) {
		MultiKeyCoefficientMap map = maps.get(event);
		double score;
		if(map.getKeysNames().length == 1) {
			score = LinearRegression.computeScore(map, iDblSrc, Regressors, true);            //No additional conditioning regression keys used, so no need to check for them
		}
		else {
			score = LinearRegression.computeScore(map, iDblSrc, Regressors);        //Additional conditioning regression keys used (map has more than one key in the multiKey, so need to use reflection (perhaps slow) in order to extract the underlying agents' properties e.g. gender or civil status, in order to determine the relevant regression co-efficients.  If time is critical, consider making the underlying agent (the IDoubleSource) also implement the IObjectSource interface, which uses a faster method to retrieve information about the agent instead of reflection.
		}

		return (double) 1 / (1 + Math.exp(- score));
	}
	public <E extends Enum<E>> double getScore(T event, IDoubleSource iDblSrc, Class<E> Regressors) {
		MultiKeyCoefficientMap map = maps.get(event);
		double score;
		if(map.getKeysNames().length == 1) {
			score = LinearRegression.computeScore(map, iDblSrc, Regressors, true);            //No additional conditioning regression keys used, so no need to check for them
		}
		else {
			score = LinearRegression.computeScore(map, iDblSrc, Regressors);        //Additional conditioning regression keys used (map has more than one key in the multiKey, so need to use reflection (perhaps slow) in order to extract the underlying agents' properties e.g. gender or civil status, in order to determine the relevant regression co-efficients.  If time is critical, consider making the underlying agent (the IDoubleSource) also implement the IObjectSource interface, which uses a faster method to retrieve information about the agent instead of reflection.
		}

		return score;
	}


	public <E extends Enum<E>> Map<T,Double> getProbabilites(IDoubleSource iDblSrc, Class<E> Regressors, Class<T> enumType) {
		Map<T, Double> probs = new HashMap<>();

		double denominator = 1.0;
		for (T event : maps.keySet()) {
			double expScore = Math.exp(getScore(event, iDblSrc, Regressors));
			probs.put(event, expScore);
			denominator += expScore;
		}

		T[] events = enumType.getEnumConstants();
		int countNullEventProbs = 0;
		for (int ii = 0; ii < events.length; ii++) {
			Double val = probs.get(events[ii]);
			if (val == null) {
				// missing should be the base of the multinomial logit - with probability 1 / (1 + sum exp(xb))
				countNullEventProbs++;
				if (countNullEventProbs > 1) {
					throw new RuntimeException("MultiLogitRegression has been constructed with a map that does not contain enough of the possible values of the type T.  The map should contain the full number of T values, or one less than the full number of T values (in which case, the missing value is considered the 'default' case whose regression betas are all zero).");
				} else {
					probs.put((T) events[ii], 1.0 / denominator);        //The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying the Logit transform (the cumulative standard normal distribution) to the score of 0, and the denominator is the sum of Logit transforms for all events.
				}
			} else {
				// all options other than the base have probability exp(xbi) / (1 + sum exp(xb))
				probs.put((T) events[ii], val / denominator);
			}
		}
		if (countNullEventProbs != 1) {
			throw new RuntimeException("MultiLogitRegression did not include a base option.");
		}

		return probs;
	}


	public <E extends Enum<E>> T eventType(IDoubleSource iDblSrc, Class<E> Regressors, Class<T> enumType) {
		Map<T, Double> probs = getProbabilites(iDblSrc, Regressors, enumType);

		T[] eventProbs = enumType.getEnumConstants();
		double[] probArray = new double[probs.size()];
		for (int ii = 0; ii < eventProbs.length; ii++) {
			probArray[ii] = probs.get(eventProbs[ii]);
		}

		return RegressionUtils.event(eventProbs, probArray, random);
	}


}
