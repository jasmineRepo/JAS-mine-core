package microsim.statistics.regression;

import java.util.Map;

public interface BinaryChoiceRegression extends LinReg {
	
	boolean event(Object individual);

	boolean event(Map<String, Double> values);

}
