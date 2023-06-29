package microsim.statistics.regression;

/**
 * Enum types for use with Regressions.
 * This class defines the names of value columns of MultiKeyCoefficientMaps of Regression objects that carry a
 * particular meaning for the calculation of regression scores and probabilities.
 */
public enum RegressionColumnNames {

    /**
     * Name of key column containing the regressors (also known as covariates)
     */
    REGRESSOR,
    /**
     * Name of values column containing the regression coefficients, for use with LinearRegression.getScore() and
     * derived classes.
     */
    COEFFICIENT,
    /**
     * Name of Covariance Matrix, appearing in the top left corner of the matrix, above the column of covariate names
     */
    COVARIANCE,
}
