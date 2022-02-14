package microsim.statistics.regression;

import java.util.Map;

import microsim.statistics.regression.ILinearRegression;

public interface IBinaryChoiceRegression extends ILinearRegression {
	
	boolean event(Object individual);

	boolean event(Map<String, Double> values);

}
