package microsim.statistics.regression;


import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;

import java.util.*;

/*****************************************************************
 * Binomial Models
 *
 * Let y define a variable that can take one of two values {0,1}
 * ystar = Xb - e
 * y = 1 if ystar>=0 and 0 otherwise
 * P(y=1|X) = P(ystar>=0) = P(e<=Xb) = F(Xb)
 *****************************************************************/
public class BinomialRegression<E1 extends Enum<E1> & IntegerValuedEnum> implements IDiscreteChoiceModel {

    MultiKeyCoefficientMap map;
    private List<E1> eventList;
    ProbabilityCalculator calculator;


    public BinomialRegression(RegressionType type, Class<E1> enumType, MultiKeyCoefficientMap map) {
        this.map = map;
        eventList = Arrays.asList(enumType.getEnumConstants());
        eventList.sort(Comparator.comparingInt(IntegerValuedEnum::getValue));
        if (eventList.size()!=2)
            throw new RuntimeException("BinaryRegression requires exactly two events");
        calculator = new ProbabilityCalculator(type);
    }


    public <E2 extends Enum<E2>> double getScore(IDoubleSource iDblSrc, Class<E2> Regressors) {
        return calculator.getScore(map, iDblSrc, Regressors);
    }

    public double getProbability(double score) {
        return calculator.getProbability(score);
    }

    public <E2 extends Enum<E2>> double getProbability(IDoubleSource iDblSrc, Class<E2> Regressors) {

        return getProbability(eventList.get(1), iDblSrc, Regressors);
    }

    public <E2 extends Enum<E2>> double getProbability(E1 event, IDoubleSource iDblSrc, Class<E2> Regressors) {

        Map<E1, Double> probs = getProbabilities(iDblSrc, Regressors);
        return probs.get(event);
    }

    public <E extends Enum<E> & IntegerValuedEnum, E2 extends Enum<E2>> Map<E,Double> getProbabilities(IDoubleSource iDblSrc, Class<E2> Regressors) {
        // probabilities are obtained for discrete alternatives of dependent variable in increasing order of the feasible set
        // P(y=1|X) = F(Xb)

        Map<E, Double> probs = new HashMap<>();
        E event;
        event = (E) eventList.get(1);
        double prob = calculator.getProbability(map, iDblSrc, Regressors);
        probs.put(event, prob);
        event = (E) eventList.get(0);
        probs.put(event, 1.0-prob);

        return probs;
    }
}
