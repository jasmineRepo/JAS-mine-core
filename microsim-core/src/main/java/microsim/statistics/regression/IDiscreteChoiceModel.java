package microsim.statistics.regression;

import microsim.statistics.IDoubleSource;

import java.util.List;
import java.util.Map;

public interface IDiscreteChoiceModel {

    <T extends Enum<T> & IntegerValuedEnum> List<T> getEventList();
    <T extends Enum<T> & IntegerValuedEnum, E extends Enum<E>> double getProbability(T event, IDoubleSource iDblSrc, Class<E> Regressors);
    <T extends Enum<T> & IntegerValuedEnum, E extends Enum<E>> Map<T,Double> getProbabilities(IDoubleSource iDblSrc, Class<E> Regressors);
}
