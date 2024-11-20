package microsim.statistics.regression;

import microsim.statistics.IDoubleSource;

import java.util.Map;

public interface IDiscreteChoiceModel {

    <T extends Enum<T> & IntegerValuedEnum, E extends Enum<E>> Map<T,Double> getProbabilities(IDoubleSource iDblSrc, Class<E> Regressors);
}
