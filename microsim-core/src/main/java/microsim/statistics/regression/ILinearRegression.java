package microsim.statistics.regression;

import java.util.Map;

public interface ILinearRegression {
	
	double getScore(Object individual);
		
	double getScore(Map<String, Double> values);
	
}
