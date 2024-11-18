package microsim.statistics.regression;

import java.util.*;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.regression.RegressionColumnNames;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomGenerator;

public class RegressionUtils {

	private static final double EPSILON = 1.e-15;	//Consider making larger if there are regular IllegalArgumentException throws due to an unnecessarily high requirement of precision.   
	private static final double SYMMETRIC_MATRIX_EPS = 1.e-5;		//Relative tolerance of matrix symmetric test, defined as {Mij - Mji > max(Mij, Mji) * eps}
	
	public static <T> T event(Class<T> eventClass, double[] prob) {
		return event(eventClass.getEnumConstants(), prob, SimulationEngine.getRnd());		
	}
	
	public static <T> T event(Class<T> eventClass, double[] weight, boolean checkWeightSum) {
		return event(eventClass.getEnumConstants(), weight, SimulationEngine.getRnd(), checkWeightSum);		
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
	
	public static <T> T event(T[] events, double[] weight, boolean checkWeightSum) {
		return event(events, weight, SimulationEngine.getRnd(), checkWeightSum);		
	}

	public static <T> T event(Map<T, Double> map) {
		return event(map, SimulationEngine.getRnd());
	}
	
	public static <T> T event(Map<T, Double> map, boolean checkWeightSum) {
		return event(map, SimulationEngine.getRnd(), checkWeightSum);
	}

	public static <T> T event(T[] events, double[] prob, Random rnd) {
		return event(events, prob, rnd, true);
	}
	
	/**
	 * Returns an event determined randomly from a set of events and weights. Note that these weights do not
	 * necessarily have to sum to 1, as the method can convert the weights into 
	 * probabilities by calculating their relative proportions.  If the user desires that an exception is 
	 * thrown if the weights do not sum to 1, please set checkProbSum to true.
	 * 
	 * @param events - an array of events of type T
	 * @param weights - an array of doubles from which the probabilities are calculated (by dividing the weights by the sum of weights). 
	 * @param rnd - Random number generator
	 * @param checkWeightSum - Boolean toggle, which if true means that the method will throw an exception if the weights do not add to 1. 
	 * If false, the method will calculate the probabilities associated to the weights and use these in the random sampling of the event.
	 *
	 * @return - the randomly chosen event
	 */
	public static <T> T event(T[] events, double[] weights, Random rnd, boolean checkWeightSum) {
		
		double x = 0.0;
		for (int i = 0; i < weights.length; i++) {
			if(weights[i] < 0.) {
				throw new IllegalArgumentException("Negative weights (probabilities) are not allowed!  Check 'weights' array " + weights + " element number " + i + ", which currently has the value " + weights[i] + ".");
			}
			x += weights[i];
		}
		
		if (checkWeightSum) {			//Enforce the condition that the weights must sum to 1 (within some small error tolerance).
			if (Math.abs(x - 1.0) > EPSILON )			//If IllegalArgumentException is too often called, i.e. precision is unnecessarily high, consider increasing value of EPSILON 
				throw new IllegalArgumentException("As checkWeightSum is set to true, the probability weights must sum to 1.  The current weights object " + weights + " has elements that sum to " + x + ".  Either ensure probability weights sum to 1, or set checkWeightSum to false so that the weights will be automatically normalised.");
		}
		else {						//Calculate the associated probabilities by dividing the weights by the sum of weights.
			//Convert weights into probabilities by 'normalising' them
			for	(int i = 0; i < weights.length; i++) {
				weights[i] /= x;
			}
		}
		
		double toss = rnd.nextDouble();
		
		x = 0.0;
		int i = 0;
		while (toss >= x)
		{
			x += weights[i];
			i++;					
		}
		
		return events[i-1];			
	}
	
		
	public static boolean event(double prob) {
		if(prob < 0. || prob > 1.) {
			throw new IllegalArgumentException("prob outside the valid interval [0,1] in RegressionUtils.event(rnd)!");
		}
		else return SimulationEngine.getRnd().nextDouble() < prob;		
	}
	
	public static boolean event(double prob, Random rnd) {
		if(prob < 0. || prob > 1.) {
			throw new IllegalArgumentException("prob outside the valid interval [0,1] in RegressionUtils.event(prob, rnd)!");
		}
		else return rnd.nextDouble() < prob;
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
	 * 
	 * If you want to do sampling without replacement, then you should use the 
	 * event(Map<T, Double> map, Random rnd, boolean checkWeightSum) method instead, setting checkWeightSum to false
	 * 
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
	 * 
	 * Useful for sampling without replacement whenever the checkWeightSum flag is set to false, 
	 * as new probabilities are calculated each time this method is called.
	 * 
	 * If checkWeightSum flag is set to true, which signifies that the map's values are
	 * weights that sum to 1, this method will be comparatively slower to call 
	 * in a loop compared to other available methods, as the events[] and prob[] need to be 
	 * extracted each time this method is called.  Better to use the other method 
	 * (event(T[], Double[]) and do the extracting of the map outside of the loop!
	 * 
	 * @param map - contains events as keys and weights as values.  These weights will be normalised to derive probabilities.
	 * @param rnd
	 * @return the event chosen
	 * 
	 * @author Ross Richardson
	 */
	public static <T> T event(Map<T, Double> map, Random rnd, boolean checkWeightSum) {	 
		@SuppressWarnings("unchecked")					//Conversion from set of T to array of T, so conversion should not need checking
		T[] events = (T[]) map.keySet().toArray();
		
		double[] prob = new double[events.length];

		for(int i = 0; i < events.length; i++) {
			T key = events[i];					
			prob[i] = ((Number)map.get(key)).doubleValue();		//This ensures events and prob arrays are aligned by indices.
		}
		return event(events, prob, rnd, checkWeightSum);
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
		return eventPiecewiseConstant(events, prob, rnd, true);
	}
	
	/**
	 * Performs a linear interpolation on the (numerical) event domain of a piecewise constant probability distribution
	 *  	
	 * @param events : The discrete set of cuts characterising the domain of a piecewise constant probability distribution
	 * @param weights : The discrete set of probabilities characterising a piecewise constant probability distribution
	 * @param rnd : The random number generator
	 * @param checkSumWeights : If true, will check weights elements sum to 1, otherwise they will be normalised (by dividing each element by the sum of the elements). 
	 * @return : The value of the event drawn from a compact domain
	 * 
	 * @author Ross Richardson
	 */
	public static double eventPiecewiseConstant(double[] events, double[] weights, Random rnd, boolean checkSumWeights) {
		
		double x = 0.0;
		for (int i = 0; i < weights.length; i++) {
			if(weights[i] < 0.) {
				throw new IllegalArgumentException("Negative weights (probabilities) are not allowed!  Check 'weights' array " + weights + " element number " + i + ", which currently has the value " + weights[i] + ".");
			}
			x += weights[i];
		}
	
		if(checkSumWeights) {
			if(x != 1.0) {		//Need total probability to sum to 1.0 exactly, otherwise there is the possibility that toss > sum of probs.
				throw new IllegalArgumentException("As checkWeightSum is set to true, the probability weights must sum to 1.  The current weights object " + weights + " has elements that sum to " + x + ".  Either ensure probability weights sum to 1, or set checkWeightSum to false so that the weights will be automatically normalised.");
			}
		}
		else {						//Calculate the associated probabilities by dividing the weights by the sum of weights.
			//Convert weights into probabilities by 'normalising' them
			for	(int i = 0; i < weights.length; i++) {
				weights[i] /= x;
			}
		}
		
		double toss = rnd.nextDouble();

		x = 0.0;
		int i = 0;
		while (toss >= x)
		{
			x += weights[i];
			i++;		
		}
		
		//Linear interpolation of event domain
		double event = events[i-1] + (events[i] - events[i-1]) * ((toss + weights[i-1] - x) / weights[i-1]);		//The expression in the last parentheses is derived from (toss - (x - prob[i-1])) / (x - (x - prob[i-1])), where x is the cumulative probability at the start of events[i] and (x - prob[i-1]) is the cumulative probability at the start of events[i-1]  
		
		return event;
	}

	
	/**
	 * 
	 * Method to bootstrap regression covariates.  This method creates a new set of regression estimates by sampling
	 * from a multivariate normal distribution with expected values (means) equal to the 'coefficients' parameter and a covariance matrix
	 * as specified. 
	 * 
	 * @param map - A MultiKeyCoefficientMap that contains both regression coefficients and a corresponding covariance matrix.
	 *   map is required to only have one key entry in the MultiKeyCoefficientMap map's MultiKey with a heading 'REGRESSOR'.  
	 *   map must also contain a value column with the heading 'COEFFICIENT' and an additional value column for each regressor (covariate), 
	 *   in order to represent the (square, symmetric, positive semi-definite) covariance matrix. 
	 * 
	 * @return a MultiKeyCoefficientMap containing a new set of bootstrapped regression coefficients
	 * 
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
			else throw new NullPointerException("There is no key named " + RegressionColumnNames.REGRESSOR.toString() + " in the map argument of RegressionUtils.bootstrap(map).  If map was loaded from an Excel spreadsheet, check there is a column with the heading \'" + RegressionColumnNames.REGRESSOR.toString() + "\'.");			
		}
		String[] valuesNames = map.getValuesNames();
		int numValueColumns = valuesNames.length;		
		
		HashMap<String, Integer> indexOfValuesNameMap = new HashMap<String, Integer>();
		HashMap<String, Integer> valuesMap = new HashMap<String, Integer>();
		int index = 0;
		int estimateIndex = -1;
		for(int i = 0; i < numValueColumns; i++) {
			valuesMap.put(valuesNames[i], i);
			
			if(valuesNames[i].equals(RegressionColumnNames.COEFFICIENT.toString())) {
				estimateIndex = i;
			}
			else {
				indexOfValuesNameMap.put(valuesNames[i], index);		//Create consistent column numbering that excludes the estimates column (the order of columns gets mixed up)
				index++;
			}

		}
		if(estimateIndex == -1) {
			throw new NullPointerException("There is no value label named " + RegressionColumnNames.COEFFICIENT.toString() + " in the map argument of RegressionUtils.bootstrap(map).  If map was loaded from an Excel spreadsheet, check there is a column with the heading \'" + RegressionColumnNames.COEFFICIENT.toString() + "\'.");
		}
		
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
			}			
		}
		
		RealMatrix realCovarianceMatrix = new Array2DRowRealMatrix(covarianceMatrix);
		MatrixUtils.checkSymmetric(realCovarianceMatrix, SYMMETRIC_MATRIX_EPS);		//Not used as cannot know the appropriate value of SYMMETRIC_MATRIX_EPS (relative tolerance) a priori
		MultivariateNormalDistribution multiNormDist = new MultivariateNormalDistribution((RandomGenerator) SimulationEngine.getRnd(), means, covarianceMatrix);
		means = multiNormDist.sample();		//This returns the bootstrapped values of the estimates
		
		//Create new multikeycoefficientmap to return with new bootstrapped column, in addition to estimate and standard error data
		String[] valueNames = new String[1];
		valueNames[0] = RegressionColumnNames.COEFFICIENT.toString();
		MultiKeyCoefficientMap bootstrapMap = new MultiKeyCoefficientMap(keys, valueNames);
		
		for (MapIterator iterator = map.mapIterator(); iterator.hasNext();) {
			iterator.next();
			
			MultiKey multiKey = (MultiKey) iterator.getKey();
			String regressor = (String) multiKey.getKey(regressorColumnIndex);
			int rowIndex = indexOfValuesNameMap.get(regressor);		
			bootstrapMap.put(multiKey, means[rowIndex]);
		}
		return bootstrapMap;

	}

	
	/**
	 * 
	 * Method to bootstrap regression covariates.  This method creates a new set of regression estimates by sampling
	 * from a multivariate normal distribution with expected values (means) equal to the 'coefficients' parameter and 
	 * a covariance matrix as specified. 
	 * 
	 * @param coefficients - A MultiKeyCoefficientMap that contains a set of regression coefficients.  The 'coefficients' map 
	 *  is required to only have one key entry in each of the MultiKeyCoefficientMap map's MultiKeys, with the entries being 
	 *  the name of the regression covariates.  There must be only one values column containing the regression coefficients 
	 *  for each covariate (regressor).
	 * 
	 * @param covarianceMatrix - A MultiKeyCoefficientMap that provides the covariance matrix of a regression's coefficients.  
	 *  The covarianceMatrix map is required to only have one key entry in each of the MultiKeyCoefficientMap map's MultiKeys,
	 *  with each entry corresponding to the name of a regression covariate.  The values must contain a name key corresponding 
	 *  to each of the covariates, though the ordering of the values (columns) need not match the MultiKey (row) ordering. 
	 * 
	 * @return a MultiKeyCoefficientMap of new regression coefficients that is bootstrapped from the input estimates map.
	 * 
	 * @author richardsonr
	 */
	public static MultiKeyCoefficientMap bootstrap(MultiKeyCoefficientMap coefficients, MultiKeyCoefficientMap covarianceMatrix) {

		String[] coefficientKeys = coefficients.getKeysNames();
		if(coefficientKeys.length > 1) {
			throw new IllegalArgumentException("The estimates map in RegressionUtils.bootstrap(estimates, covarianceMatrix) should only have one "
					+ "key entry in the MultiKey (and this should be a name of a covariate)."
					+ "\nThe Stack Trace is\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}

		String[] valuesNames = coefficients.getValuesNames();
		if(valuesNames.length > 1) {
			throw new IllegalArgumentException("The estimates map in RegressionUtils.bootstrap(estimates, covarianceMatrix) should only have one "
					+ "value corresponding to each MultiKey (and this should be the value of a regression coefficient corresponding to the key's covariate)."
					+ "\nThe Stack Trace is\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}

		int numCovariates = coefficients.size();
		String[] covariates = new String[numCovariates];
		int n = 0;
		for(Object o : coefficients.keySet()) {		//Order of iteration not guaranteed???
			if(o instanceof MultiKey) {
				covariates[n] = ((MultiKey)o).getKey(0).toString();		//The order in which covariates and their corresponding covariances are handled within this method is fixed by this ordering.
				n++;
			}
		}
		
		double[][] covarianceMatrixOrdered;
		double[] means = new double[numCovariates];
		//Create covariance matrix with an order consistent with the covariates array
		covarianceMatrixOrdered = new double[numCovariates][numCovariates];
		for(int row = 0; row < numCovariates; row++) {
			means[row] = ((Number)coefficients.getValue(covariates[row])).doubleValue();
			for(int col = 0; col < numCovariates; col++) {
				covarianceMatrixOrdered[row][col] = ((Number)covarianceMatrix.getValue(covariates[row], covariates[col])).doubleValue();
			}
		}
		
		RealMatrix realCovarianceMatrix = new Array2DRowRealMatrix(covarianceMatrixOrdered);
		MatrixUtils.checkSymmetric(realCovarianceMatrix, SYMMETRIC_MATRIX_EPS);		//Not used as cannot know the appropriate value of SYMMETRIC_MATRIX_EPS (relative tolerance) a priori 
		MultivariateNormalDistribution multiNormDist = new MultivariateNormalDistribution((RandomGenerator) SimulationEngine.getRnd(), means, covarianceMatrixOrdered);
		means = multiNormDist.sample();		//This returns the bootstrapped values of the estimates
				
		MultiKeyCoefficientMap bootstrapMap = new MultiKeyCoefficientMap(coefficientKeys, valuesNames);

		for(int j = 0; j < numCovariates; j++) {
			bootstrapMap.putValue(covariates[j], means[j]);
		}
		return bootstrapMap;
	}

	/**
	 * 
	 * Method to bootstrap multinomial regression covariates.  This method creates a new map of sets of regression 
	 * coefficients by sampling from a multivariate normal distribution with expected values (means) equal to the 
	 * regression coefficients contained in the 'coefficientOutcomeMap' and a covariance matrix as specified. 
	 * 
	 * @param <T>  The event (outcome) of a multinomial regression
	 * 
	 * @param eventRegressionCoefficientMap - A map whose keys are the possible events (outcomes) of type T of the multinomial 
	 *  regression, and whose values are MultiKeyCoefficientMaps each containing a set of regression coefficients 
	 *  corresponding to its event (the key).  Each MultiKeyCoefficientMap of regression coefficients is used as 
	 *  expected value of a multivariate normal distribution, which is sampled in order to produce a new set of 
	 *  regression coefficients.  The MultiKeyCoefficientMaps are required to only have one key entry, with the entries being 
	 *  the name of the regression covariates.  There must be only one values column in the MultiKeyCoefficientMaps,
	 *  containing the regression coefficients for each covariate.
	 * 
	 * @param covarianceMatrix - A MultiKeyCoefficientMap that provides the covariance matrix of a regression's coefficients.  
	 *  The covarianceMatrix map is required to only have one key entry in each of the MultiKeyCoefficientMap map's MultiKeys,
	 *  with each entry corresponding to a sring with the structure [event name]_[covariate name].  So for example, if the set 
	 *  of events of T are LowEducation and HighEducation, and the regression covariates declared in coefficientOutcomeMap 
	 *  are 'age' and 'gender', then the following MultiKey entries must exist:- "LowEducation_age", "LowEducation_gender",
	 *  "HighEducation_age", "HighEducation_gender", such that the name of the event is a prefix, the character "_" is the 
	 *  'regular expression' and the name of the regression covariate is the suffix.  This prefix/suffix ordering must be preserved 
	 *  in order to avoid confusion between the name of events and covariates, however the order in which the MultiKeys are specified
	 *   does not matter.  
	 *  The values of the MultiKeyCoefficientMap must contain a name key that corresponds to each MultiKey key entry (to ensure 
	 *  labelling of rows and columns match), though the ordering of the values (columns) need not match the MultiKey (row) ordering. 
	 *  
	 * @return a Map whose keys are the possible events (outcomes) of type T of the multinomial regression and whose values 
	 * are MultiKeyCoefficientMap with new regression coefficients (one set of coefficients for each event).
	 * 
	 * @author richardsonr
	 * 
	 */
	public static <T> Map<T, MultiKeyCoefficientMap> bootstrapMultinomialRegression(Map<T, MultiKeyCoefficientMap> eventRegressionCoefficientMap, MultiKeyCoefficientMap covarianceMatrix, Class<T> enumType) {
		
		T[] possibleEvents = enumType.getEnumConstants();
		
		Set<T> specifiedEvents =  eventRegressionCoefficientMap.keySet();
		
		int missingEvents = 0;
		String[] tNames = new String[specifiedEvents.size()];
		int count = 0;
		String[] multiKeyMapKeyNames = null;			//The name of the MultiKey in the MultiKeyCoefficientMaps
		String[] multiKeyMapValueNames = null;			//The name of the values in the MultiKeyCoefficientMaps
		Set<MultiKey> covariateMultiKeys = null;
		T baseT = null;
		for (T t : possibleEvents) {
			if (specifiedEvents.contains(t)) {
				tNames[count] = t.toString();
				MultiKeyCoefficientMap map = eventRegressionCoefficientMap.get(t);
				if(count == 0) {
					baseT = t;		//base event which we compare the key names and value names of all other events' MultiKeyCoefficientMaps 
					multiKeyMapKeyNames = map.getKeysNames();
					multiKeyMapValueNames = map.getValuesNames();
					covariateMultiKeys = map.keySet();
				}
				else {
					String[] otherKeyNames = map.getKeysNames();
					String[] otherValueNames = map.getValuesNames();
					Set<MultiKey> otherMultiKeys = map.keySet();
					//Check dimensions match
					if(multiKeyMapKeyNames.length != otherKeyNames.length) {
						throw new IllegalArgumentException("The number of keys in the regression coefficient MultiKeyCofficientMap for event " + t + " does not match the number of keys in event " + baseT);
					}
					if(multiKeyMapValueNames.length != otherValueNames.length) {
						throw new IllegalArgumentException("The number of value names in the regression coefficient MultiKeyCofficientMap for event " + t + " does not match the number of value names in event " + baseT);
					}
					if(map.keySet().size() != covariateMultiKeys.size()) {
						throw new IllegalArgumentException("The number of covariates specified in the regression coefficient MultiKeyCofficientMap for event " + t + " does not match the number of covariates in event " + baseT);
					}

					//Check key names and value names match between events
					for(int i = 0; i < multiKeyMapKeyNames.length; i++) {
						if(!multiKeyMapKeyNames[i].equals(otherKeyNames[i])) {
							throw new IllegalArgumentException("The key names in the regression coefficient MultiKeyCofficientMap for event " + t + " do not match the key names in event " + baseT);
						}
					}
					for(int i = 0; i < multiKeyMapValueNames.length; i++) {
						if(!multiKeyMapValueNames[i].equals(otherValueNames[i])) {
							throw new IllegalArgumentException("The value names in the regression coefficient MultiKeyCofficientMap for event " + t + " do not match the value names in event " + baseT);
						}
					}
					//Check that all events have the same MultiKeys (regression covariates)
					for(MultiKey mk : otherMultiKeys) {
						if(!covariateMultiKeys.contains(mk)) {
							throw new IllegalArgumentException("The covariate " + mk.getKey(0) + " specified in the regression coefficient MultiKeyCofficientMap for event " + t + " does not appear in the set of covariates of "
									+ "event " + baseT + ".  Check that all events have the same set of regression covariates!");							
						}
					}					
				}
				count++;
			}
			else {
				missingEvents++;
				if(missingEvents > 1) {							//The multinomial regression can go without specifying coefficients for 1 of the events (outcomes) of the type T as the probability of this event can be determined by the residual of the other probabilities.
					throw new RuntimeException("MultiProbitRegression has been constructed with a "
							+ "map that does not contain enough of the possible values of the type T.  "
							+ "The map should contain the full number of T values, or one less than the "
							+ "full number of T values (in which case, the missing value is considered the "
							+ "'default' case whose regression betas are all zero).");
				}
			}			
		}
		
		MultiKeyCoefficientMap enlargedCoefficientMap = new MultiKeyCoefficientMap(multiKeyMapKeyNames, multiKeyMapValueNames);		//Create new MultiKeyCoefficientMap that has matching key and value names as (one of) the entries of the Map<T, MultiKeyCoefficientMap>.
		for(T event : specifiedEvents) {
			MultiKeyCoefficientMap regCoefficientsMap = eventRegressionCoefficientMap.get(event);
			for(Object o : regCoefficientsMap.keySet()) {
				MultiKey mk = (MultiKey)o; 
				String combinedName = event.toString() + "_" + mk.getKey(0).toString();
				enlargedCoefficientMap.putValue(combinedName, regCoefficientsMap.getValue(mk));
			}
		}
		
		enlargedCoefficientMap = bootstrap(enlargedCoefficientMap, covarianceMatrix);
		
		Map<T, MultiKeyCoefficientMap> newMap = new HashMap<T, MultiKeyCoefficientMap>(specifiedEvents.size());
		for(T event : specifiedEvents) {
			MultiKeyCoefficientMap newCoefficientMap = new MultiKeyCoefficientMap(multiKeyMapKeyNames, multiKeyMapValueNames);
			for(MultiKey mk : covariateMultiKeys) {
				String combinedName = event.toString() + "_" + mk.getKey(0).toString();
//				System.out.println("combinedName " + combinedName);
				double regCoefficient = ((Number)enlargedCoefficientMap.getValue(combinedName)).doubleValue();
				newCoefficientMap.putValue(mk, regCoefficient);
//				System.out.println(mk.toString() + ", " + regCoefficient);
			}
			newMap.put(event, newCoefficientMap);
		}
		
////		Set<String> covarianceEvents = new HashSet<String>(specifiedEvents.size());
//		for(Object o : covarianceMatrix.keySet()) {
//			String key = o.toString();
//			String[] subStrings = key.split("_");
//			if(subStrings.length > 2) {
//				throw new IllegalArgumentException("covarianceMatrix contains a key featuring substrings "
//						+ "separated by more than one '_' character.  There should only be one instance of "
//						+ "the '_' character in the name keys, which should be used to separate a prefix "
//						+ "corresponding to an event (outcome) name, and a suffix corresponding to a regression "
//						+ "covariate name.");
//			}
////			for(int i = 0; i < tNames.length; i++) {
////				if(subStrings[0].equals(tNames[i])) {
////					covarianceEvents.add(subStrings[0]);		//Only check that first substring of key before the 'regular expression' "_" character is an event name.  Thus if LowEdu and HighEdu are events, and the covariance matrix contains keys LowEdu_age and HighEdu_age, this will be satisfied, but not if the keys are age_LowEdu and age_HighEdu.  This is to prevent confusion over what is the event name and the covariate name. 
////				}
////			}
//		}

		return newMap;
		
	}



	/**
	 * Method to package multinomialCoeffMap
	 * @param clazz an enum class defining the multinomial alternatives considered for analysis
	 * @param multinomialCoefficients a standard MultiKeyCoefficientMap object used to read parameters from Excel and bootstrap
	 *                                The method assumes that coefficients are supplied for each (or N-1) discrete alternative,
	 *                                where "_XXX" is appended to each coefficient name to indicate association with alternative "XXX"
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
	 * @deprecated  As of release 4.0.7 because of typo in method name, replaced by {@link #bootstrapMultinomialRegression(Map<T, MultiKeyCoefficientMap> eventRegressionCoefficientMap, MultiKeyCoefficientMap covarianceMatrix, Class<T> enumType)}
	 */
	@Deprecated
	public static <T> Map<T, MultiKeyCoefficientMap> boostrapMultinomialRegression(Map<T, MultiKeyCoefficientMap> eventRegressionCoefficientMap, MultiKeyCoefficientMap covarianceMatrix, Class<T> enumType) {
		return bootstrapMultinomialRegression(eventRegressionCoefficientMap, covarianceMatrix, enumType);		
	}
	
	
}
