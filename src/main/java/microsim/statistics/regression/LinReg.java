package microsim.statistics.regression;

import microsim.statistics.DoubleSource;
import microsim.statistics.ObjectSource;

import java.util.Map;

public interface LinReg {
	
	double getScore(Object individual);
		
	double getScore(Map<String, Double> values);
	
	<T extends Enum<T>> double getScore(DoubleSource iDblSrc, Class<T> enumType);
	
	<T extends Enum<T>, U extends Enum<U>> double getScore(DoubleSource iDblSrc, Class<T> enumTypeDouble, ObjectSource iObjSrc, Class<U> enumTypeObject);
}
