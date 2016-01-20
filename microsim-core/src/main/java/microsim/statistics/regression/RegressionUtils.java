package microsim.statistics.regression;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

public class RegressionUtils {

	private static final double EPSILON = 1.e-15;	//Consider making larger if there are regular IllegalArgumentException throws due to an unnecessarily high requirement of precision.   

	public static <T> T event(Class<T> eventClass, double[] prob) {
		return event(eventClass.getEnumConstants(), prob, SimulationEngine.getRnd());		
	}
	
	/**
	 * You must provide a vector of events (any type of object) and relative
	 * weights which sum must be equal to 1.0.
	 * 
	 * The function toss a random double number and search in witch probability
	 * range the sampled number is within to select the corresponding event.
	 * 
	 * @param events
	 * @param prob
	 * @return
	 */
	public static <T> T event(T[] events, double[] prob) {
		return event(events, prob, SimulationEngine.getRnd());		
	}
	
	public static <T> T event(Map<T, Double> map) {
		return event(map, SimulationEngine.getRnd());
	}
	
	public static <T> T event(T[] events, double[] prob, Random rnd) {
		
		double x = 0.0;
		for (int i = 0; i < prob.length; i++) {
			x += prob[i];
		}
		
		if (Math.abs(x - 1.0) > EPSILON ) 				//If IllegalArgumentException is too often called, i.e. precision is unnecesarily high, consider increasing value of EPSILON 
			throw new IllegalArgumentException("Choice's weights must sum 1.0. Current vector" + prob + " sums " + x);
		
		double toss = rnd.nextDouble();
		
		x = 0.0;
		int i = 0;
		while (toss >= x)
		{
			x += prob[i];
			i++;					
		}
		
		return events[i-1];			
	}
	
	public static boolean event(double prob) {
		return SimulationEngine.getRnd().nextDouble() < prob;		
	}
	
	public static boolean event(double prob, Random rnd) {
		return rnd.nextDouble() < prob;
	}
	
	/////////////////////////////////////////
	// New methods 
	// @author Ross Richardson
	/////////////////////////////////////////
	
	/**
	 * Although this method is easy to use with Maps with events as the keys and probabilities as 
	 * the values, it will be very slow to call it in a loop, as the events[] and prob[] need to be 
	 * extracted each time this method is called.  Better to use the other method 
	 * (event(T[], Double[]) and do the extracting of the map outside of the loop!
	 * @param map
	 * @param rnd
	 * @return the event chosen
	 * 
	 * @author Ross Richardson
	 */
	public static <T> T event(Map<T, Double> map, Random rnd) {	 
		@SuppressWarnings("unchecked")					//Conversion from set of T to array of T, so conversion should not need checking
		T[] events = (T[]) map.keySet().toArray();
		
		double[] prob = new double[events.length];

		for(int i = 0; i < events.length; i++) {
			T key = events[i];					
			prob[i] = ((Number)map.get(key)).doubleValue();		//This ensures events and prob arrays are aligned by indices.
		}
		return event(events, prob, rnd);
	}
	
	/**
	 * For sampling an event where all events in the sample space have equal probability.
	 * The probability for each event = 1 / size of sample space. 
	 * 
	 * @param events - the possible events in the sample space
	 * @param rnd
	 * @return the event chosen
	 * 
	 * @author Ross Richardson
	 */
	public static <T> T event(T[] events, Random rnd) {
		double x = 0.0;
		double prob = 1. / (double)events.length;		
		double toss = rnd.nextDouble();
		
		int i = 0;
		while (toss >= x)
		{
			x += prob;
			i++;					
		}
		
		return events[i-1];
	}

	/**
	 * For sampling an event where all events in the sample space have equal probability.
	 * The probability for each event = 1 / size of sample space. 
	 * This allows events with types that implement the list interface.
	 * 
	 * @param events - the possible events in the sample space
	 * @param rnd
	 * @return the event chosen
	 * 
	 * @author Ross Richardson
	 */
	public static <T> T event(AbstractList<T> events, Random rnd) {
		double x = 0.0;
		double prob = 1. / (double)events.size();		
		double toss = rnd.nextDouble();
		
		int i = 0;
		while (toss >= x)
		{
			x += prob;
			i++;					
		}
		
		return events.get(i-1);
	}

	/**
	 * Performs a linear interpolation on the (numerical) event domain of a piecewise constant probability distribution
	 *  	
	 * @param events : The discrete set of cuts characterising the domain of a piecewise constant probability distribution
	 * @param prob : The discrete set of probabilities characterising a piecewise constant probability distribution
	 * @param rnd : The random number generator
	 * @return : The value of the event drawn from a compact domain
	 * 
	 * @author Ross Richardson
	 */
	public static double eventPiecewiseConstant(double[] events, double[] prob, Random rnd) {
		
		double x = 0.0;
		for (int i = 0; i < prob.length; i++) {
			x += prob[i];
		}
		
//		if (Math.abs(x - 1.0) > EPSILON ) 				//If IllegalArgumentException is too often called, i.e. precision is unnecesarily high, consider increasing value of EPSILON
		if(x != 1.0)		//Need total probability to sum to 1.0, otherwise there is the possibility that toss > sum of probs.
			throw new IllegalArgumentException("Choice's weights must sum 1.0. Current vector" + prob + " sums " + x);
		
		double toss = rnd.nextDouble();
//		System.out.println("toss " + toss);
		x = 0.0;
		int i = 0;
		while (toss >= x)
		{
//			System.out.println("i " + i + " x " + x + " events[i] " + events[i] + " prob[i] " + prob[i]);
			x += prob[i];
			i++;		
		}
		
		//Linear interpolation of event domain
		double event = events[i-1] + (events[i] - events[i-1]) * ((toss + prob[i-1] - x) / prob[i-1]);		//The expression in the last parentheses is derived from (toss - (x - prob[i-1])) / (x - (x - prob[i-1])), where x is the cumulative probability at the start of events[i] and (x - prob[i-1]) is the cumulative probability at the start of events[i-1]  
		
//		System.out.println("toss " + toss + " events[i-1] " + events[i-1] + " prob[i-1] " + prob[i-1] + " events[i] " + events[i] + " prob[i] " + prob[i] + " x " + x + " x-prob[i] " + (x - prob[i]) + " event " + event);
		
		return event;
	}

//	/**
//	 * Method to bootstrap regression covariates when they are independent from each other. 
//	 * As covariates are assumed to be independent, the input MultiKeyCoefficientMap map does
//	 * not need to have an associated covariance matrix, only a column named 'STANDARD_ERROR'.
//	 * In addition, the covariate estimates must be in a column labelled 'ESTIMATE'
//	 * 
//	 * @param map: A MultiKeyCoefficientMap that must contain two values column called 'ESTIMATE' and 'STANDARD_ERROR'.
//	 * @return a MultiKeyCoefficientMap that is bootstrapped from the input map
//	 * @author richardsonr
//	 */
//	public static MultiKeyCoefficientMap bootstrapUncorrelated(MultiKeyCoefficientMap map) {
//		
//		String[] valuesNames = map.getValuesNames();
//		int estimatesColumnNumber = -1;
//		int errorsColumnNumber = -1;
////		int countEstimate = 0;
////		int countError = 0;
//		String estimatesString = null;
//		String errorsString = null;
//		for(int i = 0; i < valuesNames.length; i++) {
////			if(valuesNames[i].equals("estimate")) {
//			if(valuesNames[i].equals(RegressionColumnNames.ESTIMATE.toString())) {
//				estimatesColumnNumber = i;
//				estimatesString = valuesNames[i];
////				countEstimate++;
//			}
////			else if(valuesNames[i].equals("standard_error")) {
//			else if(valuesNames[i].equals(RegressionColumnNames.STANDARD_ERROR.toString())) {
//				errorsColumnNumber = i;
//				errorsString = valuesNames[i];
////				countError++;
//			}			
//		}
//		if((estimatesColumnNumber < 0) || (errorsColumnNumber < 0)) {
////		if((countEstimate < 1) || (countError < 1)) {
//			throw new RuntimeException(map + " does not contain columns named 'estimate' and 'standard_error', which is necessary in order to call the RegressionUtils.bootstrap() method. \nThe stack trace is "
//					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
////		else if((countEstimate > 1) || (countError > 1)) {		//Will not work, as cannot have non-unique column names stored in MultiKeyCoefficientMap - the column last put in the map will be the one used here (note, the order of the put operations constructing the map depends on the order of iteration)
////			throw new RuntimeException(map + " has more than one column of estimates or standard error values - cannot determine the appropriate values to use in the RegressionUtils.bootstrap() method. \nThe stack trace is "
////					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
////		}
//		
//		//Create new multikeycoefficientmap to return with new bootstrapped column, in addition to estimate and standard error data
//		String[] valueNames = new String[3];
//		valueNames[0] = RegressionColumnNames.COEFFICIENT.toString();
//		valueNames[1] = RegressionColumnNames.ESTIMATE.toString();
//		valueNames[2] = RegressionColumnNames.STANDARD_ERROR.toString();
//		MultiKeyCoefficientMap bootstrapMap = new MultiKeyCoefficientMap(map.getKeysNames(), valueNames);
//		
//		for (MapIterator iterator = map.mapIterator(); iterator.hasNext();) {
//			iterator.next();
//			
//			MultiKey multiKey = (MultiKey) iterator.getKey();
//			int multiKeyLength = multiKey.getKeys().length; 
//			Object[] fullKey = new Object[multiKeyLength + 1];			//To include last key, which is the name of the appropriate value column (i.e. 'estimate' or 'standard_error')
//			for(int i = 0; i < multiKeyLength; i++) {
//				fullKey[i] = multiKey.getKey(i);
//			}
//			fullKey[multiKeyLength] = estimatesString;
//			double estimate = ((Number)map.getValue(fullKey)).doubleValue();			//This represents the expected value of the regression co-efficient
//			
//			fullKey[multiKeyLength] = errorsString;
//			double standardError = ((Number)map.getValue(fullKey)).doubleValue();
//			double errorAdjustment = SimulationEngine.getRnd().nextGaussian() * standardError;		//Adjust standard normal variable to have appropriate variance
//			double coefficient = estimate + errorAdjustment;				//Bootstrap values by randomly sampling from a normal distribution with the appropriate standard error, and add to expected value of the regression co-efficient
//			
//			Object[] values = new Object[] {coefficient, estimate, standardError};	//WARNING: Need to ensure this order is consistent with the column heading names as defined above in valueNames   
//			
//			//Add the new data to the new multikeycoefficientmap to return...
//			bootstrapMap.put(multiKey, values);
//		}
//		return bootstrapMap;
//	}
	
//	//This would possibly work, if the MultiKeyCoefficientMap iterated in the same order as insertion, so that rows and column orderings can be preserved.  Need to re-do MultiKeyCoefficientMaps to ensure this in future???
	// @author richardsonr
//	public static MultiKeyCoefficientMap bootstrap(MultiKeyCoefficientMap map) {
//		
//		String[] valuesNames = map.getValuesNames();
//		int estimatesColumnNumber = -1;
//		int covarianceStartColumnNumber = -1;
//		int covarianceEndColumnNumber = -1;
////		String estimatesString = null;
////		String covarianceStartString = null;
////		String covarianceEndString = null;
//		for(int i = 0; i < valuesNames.length; i++) {
//			if(valuesNames[i].equals(RegressionColumnNames.ESTIMATE.toString())) {
//				estimatesColumnNumber = i;
//				System.out.println("estimatesColumnNumber " + estimatesColumnNumber);
////				estimatesString = valuesNames[i];
//			}
//			else if(valuesNames[i].equals(RegressionColumnNames.COVARIANCE_MATRIX_START.toString())) {
//				covarianceStartColumnNumber = i;
//				System.out.println("covarianceStartColumnNumber " + covarianceStartColumnNumber);
////				covarianceStartString = valuesNames[i];
//			}			
//			else if(valuesNames[i].equals(RegressionColumnNames.COVARIANCE_MATRIX_END.toString())) {
//				covarianceEndColumnNumber = i;
//				System.out.println("covarianceEndColumnNumber " + covarianceEndColumnNumber);
////				covarianceEndString = valuesNames[i];
//			}
//		}
//		int numColumnsInCovarianceMatrix = 1 + covarianceEndColumnNumber - covarianceStartColumnNumber;
//		int numRowsInCovarianceMatrix = map.size();			//TODO: Check this is correct AND robust, i.e. if we envisage changing the structure of the .xls files, what impact does that have?
//		if(estimatesColumnNumber < 0) {
//			throw new RuntimeException(map + " does not contain column named " + RegressionColumnNames.ESTIMATE.toString() + " which is necessary in order to call the RegressionUtils.bootstrap() method. \nThe stack trace is "
//					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
//		if( (covarianceStartColumnNumber < 0) || (covarianceEndColumnNumber < 0)) {
//			throw new RuntimeException(map + " does not contain column named " + RegressionColumnNames.COVARIANCE_MATRIX_START.toString() + " or " + RegressionColumnNames.COVARIANCE_MATRIX_END.toString() + " which is necessary in order to call the RegressionUtils.bootstrap() method. \nThe stack trace is "
//					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
//		if(covarianceEndColumnNumber <= covarianceStartColumnNumber) {
//			throw new RuntimeException(map + " contain column named " + RegressionColumnNames.COVARIANCE_MATRIX_START.toString() + " at a higher (or equal) index than " + RegressionColumnNames.COVARIANCE_MATRIX_END.toString() + " column.  Check that the start column of the covariance matrix is positioned to the left of the end column.  \nThe stack trace is "
//					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
//		if(numColumnsInCovarianceMatrix != numRowsInCovarianceMatrix) {
//			throw new RuntimeException("In RegressionUtils.bootstrap(MultiKeyCoefficientMap map), there are " + numColumnsInCovarianceMatrix + " columns in the covariance matrix and " + numRowsInCovarianceMatrix + " rows (measured by the number of mappings in the map).  These values must be equal for the covariance matrix to be valid!  \nThe stack trace is "
//					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
//
//		int j = 0;
//		double means[] = new double[numRowsInCovarianceMatrix];
//		double[][] covarianceMatrix = new double[numRowsInCovarianceMatrix][numColumnsInCovarianceMatrix];
//		System.out.println("Means columns is:");
//		for(MapIterator iterator = map.mapIterator(); iterator.hasNext();) {		//TODO: Problem - is this guaranteed to iterated in the correct order?
//			iterator.next();
//
//			MultiKey multiKey = (MultiKey) iterator.getKey(); 
//			Object[] mapValuesRow = ((Object[])map.getValue(multiKey));
//			means[j] = ((Number)mapValuesRow[estimatesColumnNumber]).doubleValue();
//			System.out.println(means[j]);
//			for(int k = 0; k < numColumnsInCovarianceMatrix; k++) {
//				covarianceMatrix[j][k] = ((Number)mapValuesRow[covarianceStartColumnNumber + k]).doubleValue();
//			}
//					 
//			j++;
//		}
//		
//		System.out.println("Covariance Matrix is:");
//		for(int i = 0; i < numRowsInCovarianceMatrix; i++) {
//			for(int k = 0; k < numColumnsInCovarianceMatrix; k++) {
//				System.out.print(covarianceMatrix[i][k] + "\t");
//			}
//			System.out.print("\n");
//		}
//		
//		//TODO: Check that the MultivariateNormalDistribution checks for semi-definite nature of the covariance matrix
//		MultivariateNormalDistribution multiNormDist = new MultivariateNormalDistribution(means, covarianceMatrix);
//		means = multiNormDist.sample();		//This returns the bootstrapped values of the estimates
//		
//		//Create new multikeycoefficientmap to return with new bootstrapped column, in addition to estimate and standard error data
//		String[] valueNames = new String[1];
//		valueNames[0] = RegressionColumnNames.COEFFICIENT.toString();
//		MultiKeyCoefficientMap bootstrapMap = new MultiKeyCoefficientMap(map.keys, valueNames);
//		
//		int m = 0;
//		System.out.println("New mean column is:");
//		for (MapIterator iterator = map.mapIterator(); iterator.hasNext();) {
//			iterator.next();
//			
//			MultiKey multiKey = (MultiKey) iterator.getKey();
////			Object[] values = new Object[] {means[m]};   
//			
//			//Add the new data to the new multikeycoefficientmap to return...
//			System.out.println(means[m]);
//			bootstrapMap.put(multiKey, means[m]);
//			m++;
//		}
//		return bootstrapMap;
//
//	}

	/*
	 * Perhaps should create a bootstrap(estimates, covarianceMatrix) method, where the covarianceMatrix is a 
	 * MultiKeyCoefficientMap or something similar, where we can simply specify the relevant element by the two keys 
	 * representing the row variable and column variable names. 
	 */
	/**
	 * 
	 * Method to bootstrap regression covariates.
	 * 
	 * @param map - A MultiKeyCoefficientMap that provides informaion on the covariate estimates and covariance matrix.
	 *   map is required to only have one key entry in the MultiKeyCoefficientMap map's MultiKey.  
	 *   map must also contain a value column with the heading 'ESTIMATE' and an additional value column for each key element, 
	 *   in order to represent the (square, symmetric, positive semi-definite) covariance matrix. 
	 * 
	 * @return a MultiKeyCoefficientMap that is bootstrapped from the input map
	 * @author richardsonr
	 */
	public static MultiKeyCoefficientMap bootstrap(MultiKeyCoefficientMap map) {
		String[] keys = map.getKeysNames();
		int numRowsInCovarianceMatrix = map.size();			//TODO: Check this is correct AND robust, i.e. if we envisage changing the structure of the .xls files, what impact does that have?
		int regressorColumnIndex = -1;
		if(keys.length > 1) {
			throw new IllegalArgumentException("There are more conditional keys in the multiKey of the map in RegressionUtils.bootstrap(map).  This cannot currently be handled.  \nThe Stack Trace is "
					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
//			for(int i = 0; i < map.keys.length; i++) {
//				if(map.keys[i].equals(RegressionColumnNames.REGRESSOR.toString())) {
//					regressorColumnIndex = i;
//				}
//			}
//			System.out.println("Warning, there are more conditional keys in the multiKey of the map in RegressionUtils.bootstrap(map).  Check that the covaraince matrix is represented in the correct way.");
		}
		else {
			if(keys[0].equals(RegressionColumnNames.REGRESSOR.toString())) {
				regressorColumnIndex = 0;				
			}
			else throw new RuntimeException("RegressionUtils.boostrap(map) has no column named " + RegressionColumnNames.REGRESSOR.toString() + " in map.");			
		}
		String[] valuesNames = map.getValuesNames();
		int numValueColumns = valuesNames.length;		
		
		HashMap<String, Integer> indexOfValuesNameMap = new HashMap<String, Integer>();
		HashMap<String, Integer> valuesMap = new HashMap<String, Integer>();
		int index = 0;
		int estimateIndex = 0;
		for(int i = 0; i < numValueColumns; i++) {
			valuesMap.put(valuesNames[i], i);
			
			if(valuesNames[i].equals(RegressionColumnNames.ESTIMATE.toString())) {
				estimateIndex = i;
			}
			else {
				indexOfValuesNameMap.put(valuesNames[i], index);		//Create consistent column numbering that excludes the estimates column (the order of columns gets mixed up)
				index++;
			}

		}
//		for(int i = 0; i < numValueColumns; i++) {
//			System.out.println(valuesNames[i] + "\t" + indexOfValuesNameMap.get(valuesNames[i]) + " valuesNames " + i);
//		}
		
		double means[] = new double[numRowsInCovarianceMatrix];
		double[][] covarianceMatrix = new double[numRowsInCovarianceMatrix][numRowsInCovarianceMatrix];
		for(MapIterator iterator = map.mapIterator(); iterator.hasNext();) {
			iterator.next();
			MultiKey multiKey = (MultiKey) iterator.getKey();
			String regressor = (String) multiKey.getKey(regressorColumnIndex);
			int rowIndex = indexOfValuesNameMap.get(regressor);
			Object[] mapValuesRow = ((Object[])map.getValue(multiKey));
			means[rowIndex] = ((Number)mapValuesRow[estimateIndex]).doubleValue();		//Should throw null pointer exception if the RHS returns null
			for(String covariableName : indexOfValuesNameMap.keySet()) {
				int columnIndex = indexOfValuesNameMap.get(covariableName);
				covarianceMatrix[rowIndex][columnIndex] = ((Number)mapValuesRow[valuesMap.get(covariableName)]).doubleValue();		//Should throw null pointer exception if the RHS returns null
//				System.out.println("regressor " + regressor + " row index " + rowIndex + " covariableName " + covariableName + " column index " + columnIndex + " covarianceMatrix[" + rowIndex + "][" + columnIndex + "] " + covarianceMatrix[rowIndex][columnIndex] + " ((Number)mapValuesRow[mapValuesNamesToColumnIndex.get(covariableName)]).doubleValue() " + ((Number)mapValuesRow[map.valuesMap.get(covariableName)]).doubleValue() + " map.valuesMap.get(covariableName) " + map.valuesMap.get(covariableName));
			}			
		}
		
//		System.out.println("The means are:");
//		for(int i = 0; i < numRowsInCovarianceMatrix; i++) {
//			System.out.println(means[i]);
//		}
		
//		System.out.println("Covariance Matrix is:");
//		for(int i = 0; i < numRowsInCovarianceMatrix; i++) {
//			for(int k = 0; k < numRowsInCovarianceMatrix; k++) {
//				System.out.print(covarianceMatrix[i][k] + "\t");
//			}
//			System.out.print("\n");
//		}
		
		MultivariateNormalDistribution multiNormDist = new MultivariateNormalDistribution(means, covarianceMatrix);
		means = multiNormDist.sample();		//This returns the bootstrapped values of the estimates
		
		//Create new multikeycoefficientmap to return with new bootstrapped column, in addition to estimate and standard error data
		String[] valueNames = new String[1];
		valueNames[0] = RegressionColumnNames.COEFFICIENT.toString();
		MultiKeyCoefficientMap bootstrapMap = new MultiKeyCoefficientMap(keys, valueNames);
		
//		System.out.println("New mean column is:");
		for (MapIterator iterator = map.mapIterator(); iterator.hasNext();) {
			iterator.next();
			
			MultiKey multiKey = (MultiKey) iterator.getKey();
			String regressor = (String) multiKey.getKey(regressorColumnIndex);
			int rowIndex = indexOfValuesNameMap.get(regressor);
			bootstrapMap.put(multiKey, new Object[]{means[rowIndex]});
			
//			Object[] keyValues = new Object[multiKey.getKeys().length+1];
//			for(int i1 = 0; i1 < multiKey.getKeys().length; i1++) {
//				keyValues[i1] = multiKey.getKey(i1);
//			}
//			keyValues[multiKey.getKeys().length] = means[rowIndex];
//			bootstrapMap.putValue(keyValues);
//			System.out.println("regressor " + regressor + " coefficient " + bootstrapMap.getValue(multiKey));
		}
		return bootstrapMap;

	}

}
