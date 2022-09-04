package microsim.statistics.regression;

import java.util.Map;

public interface MultipleChoiceRegression<T extends Enum<T>> {

	T eventType(Object individual);

	T eventType(Map<String, Double> values);
}
