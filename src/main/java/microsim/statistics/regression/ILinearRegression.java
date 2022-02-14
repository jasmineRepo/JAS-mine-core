package microsim.statistics.regression;

import java.util.Map;

import microsim.statistics.IDoubleSource;
import microsim.statistics.IObjectSource;

public interface ILinearRegression {
	
	double getScore(Object individual);
		
	double getScore(Map<String, Double> values);
	
	<T extends Enum<T>> double getScore(IDoubleSource iDblSrc, Class<T> enumType);
	
	<T extends Enum<T>, U extends Enum<U>> double getScore(IDoubleSource iDblSrc, Class<T> enumTypeDouble, IObjectSource iObjSrc, Class<U> enumTypeObject);
}
