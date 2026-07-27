package microsim.statistics.regression;

import microsim.statistics.IDoubleSource;

import java.util.List;
import java.util.Map;

public interface IDiscreteChoiceModel<E extends Enum<E> & IntegerValuedEnum> {

    List<E> getEventList();

    <E2 extends Enum<E2>> double getProbability(E event, IDoubleSource iDblSrc, Class<E2> Regressors);

    <E2 extends Enum<E2>> Map<E, Double> getProbabilities(IDoubleSource iDblSrc, Class<E2> Regressors);
}
