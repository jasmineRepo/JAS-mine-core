package microsim.statistics.regression;

import java.util.Map;

public interface IMultipleChoiceRegression<T extends Enum<T>> {

	T eventType(Object individual); 
	
	T eventType(Map<String, Double> values);
}
