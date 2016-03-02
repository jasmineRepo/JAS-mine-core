package microsim.statistics.regression;

/** 
 * Enum types for use with Regressions.  
 * This class defines the names of value columns of MultiKeyCoefficientMaps of Regression objects that carry a particular meaning for the calculation of regression scores and probabilities.
 * 
 * @author richardsonr
 */
public enum RegressionColumnNames {

	REGRESSOR,			//Name of key column containing the regressors (also known as covariates)
	COEFFICIENT,		//Name of values column containing the regression coefficients, for use with LinearRegression.getScore() and derived classes.
	ESTIMATE,			//Name of values column containing the (optimal) estimate of a regression coefficient, for use with RegressionUtils.bootstrap()
	COVARIANCE,
//	STANDARD_ERROR,		//Name of values column containing the standard error of the (optimal) estimate of a regression coefficient, for use with RegressionUtils.bootstrap()
//	COVARIANCE_MATRIX_START,
//	COVARIANCE_MATRIX_END,
	
}
