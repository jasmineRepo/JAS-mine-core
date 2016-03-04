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
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomGenerator;

public class RegressionUtils {

	private static final double EPSILON = 1.e-15;	//Consider making larger if there are regular IllegalArgumentException throws due to an unnecessarily high requirement of precision.   
//	private static final double SYMMETRIC_MATRIX_EPS = 1.e-5;		//Relative tolerance of matrix symmetric test, defined as {Mij - Mji > max(Mij, Mji) * eps}
	
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

	
	/**
	 * 
	 * Method to bootstrap regression covariates.  This method creates a new set of regression estimates by sampling
	 * from a multinomial normal distribution with means equal to the 'coefficients' parameter and a covariance matrix
	 * as specified. 
	 * 
	 * @param map - A MultiKeyCoefficientMap that contains both regression coefficients and a corresponding covariance matrix.
	 *   map is required to only have one key entry in the MultiKeyCoefficientMap map's MultiKey with a heading 'REGRESSOR'.  
	 *   map must also contain a value column with the heading 'COEFFICIENT' and an additional value column for each regressor (covariate), 
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
			
			if(valuesNames[i].equals(RegressionColumnNames.COEFFICIENT.toString())) {
				estimateIndex = i;
			}
			else {
				indexOfValuesNameMap.put(valuesNames[i], index);		//Create consistent column numbering that excludes the estimates column (the order of columns gets mixed up)
				index++;
			}

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
		
//		RealMatrix realCovarianceMatrix = new Array2DRowRealMatrix(covarianceMatrix);
//		MatrixUtils.checkSymmetric(realCovarianceMatrix, SYMMETRIC_MATRIX_EPS);		//Not used as cannot know the appropriate value of SYMMETRIC_MATRIX_EPS (relative tolerance) a priori
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
	 * from a multinomial normal distribution with means equal to the 'coefficients' parameter and a covariance matrix
	 * as specified. 
	 * 
	 * @param coefficients - A MultiKeyCoefficientMap that is used as the means of a multivariate normal distribution.
	 * The means represent the estimates of the regression coefficients.  The 'coefficients' map 
	 * is required to only have one key entry in each of the MultiKeyCoefficientMap map's MultiKeys, with the entries being 
	 * the name of the regression covariates.  If loading from an Excel spreadsheet using the ExcelAssistant.loadCoefficientMap(),
	 * the column containing the covariate names must be labelled with a header 'REGRESSOR'.  There must be only one values column
	 * containing the regression coefficients for each covariate, and this column must be labelled 'COEFFICIENT' in the Excel spreadsheet.
	 * 
	 * @param covarianceMatrix - A MultiKeyCoefficientMap that provides the covariance matrix of a regression's coefficients.  
	 * The first column of covarianceMatrix must be named 'COVARIANCE', and contain the names of the regression covariates.  The other 
	 * columns should be headed with labels matching the regression covariates (though the order need not match the estimates). 
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
		
//		RealMatrix realCovarianceMatrix = new Array2DRowRealMatrix(covarianceMatrixOrdered);
//		MatrixUtils.checkSymmetric(realCovarianceMatrix, SYMMETRIC_MATRIX_EPS);		//Not used as cannot know the appropriate value of SYMMETRIC_MATRIX_EPS (relative tolerance) a priori 
		MultivariateNormalDistribution multiNormDist = new MultivariateNormalDistribution((RandomGenerator) SimulationEngine.getRnd(), means, covarianceMatrixOrdered);
		means = multiNormDist.sample();		//This returns the bootstrapped values of the estimates
				
		MultiKeyCoefficientMap bootstrapMap = new MultiKeyCoefficientMap(coefficientKeys, valuesNames);

		for(int j = 0; j < numCovariates; j++) {
			bootstrapMap.putValue(covariates[j], means[j]);
		}
		return bootstrapMap;
	}
	
	
	
}
